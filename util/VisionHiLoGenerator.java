package com.ghx.api.operations.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * Existing logic from visioncore services.
 * Class to generate primary key based on maximum oid value from seed container table.
 */
public class VisionHiLoGenerator implements PersistentIdentifierGenerator, Configurable {
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(VisionHiLoGenerator.class);

    /**
     * The column parameter
     */
    public static final String COLUMN = "column";

    /**
     * The table parameter
     */
    public static final String TABLE = "table";

    /**
     * The install id parameter
     */
    public static final String INSTALL_ID = "install_id";

    /**
     * The max_lo parameter
     */
    public static final String MAX_LO = "max_lo";

    private String seedContainerTableName;

    private String highOidColName;

    private String installIdColName;

    private String query;

    private String update;

    private long hi;

    private int lo;

    private int maxLo;

    private String installId;

    public void configure(Type type, Properties params, Dialect dialect) {

        this.seedContainerTableName = ConfigurationHelper.getString(TABLE, params, "seed_container");
        this.highOidColName = ConfigurationHelper.getString(COLUMN, params, "high_oid");
        this.installIdColName = ConfigurationHelper.getString(INSTALL_ID, params, "seed_id");

        String schemaName = params.getProperty(SCHEMA);
        if (schemaName != null && seedContainerTableName.indexOf('.') < 0) {
            seedContainerTableName = schemaName + '.' + seedContainerTableName;
        }

        query = "select " + highOidColName + "," + installIdColName + " from " + seedContainerTableName;
        update = "update " + seedContainerTableName + " set " + highOidColName + " = ? where " + installIdColName + " = ?";

        maxLo = ConfigurationHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
        lo = maxLo + 1;
    }

    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        synchronized (this) {
            if (lo > maxLo) {
                try {
                    hi = ((Integer) realGenerate(session, obj)).intValue();
                    lo = 1;
                } catch (SQLException | HibernateException e1) {

                    if (!retryRealGenerate(session, obj)) {
                        ExceptionUtils.getStackTrace(e1);
                        throw new HibernateException(e1);
                    }
                }
                LOGGER.debug("new hi value: ", hi);
            }
            return VisionHiLoGenerator.getNextOid(installId, hi, hi + lo++);
        }
    }

    public Serializable realGenerate(SharedSessionContractImplementor session, Object object) throws SQLException, HibernateException {
        synchronized (this) {
            Connection conn = null;
            int currentHiVal;
            int rows;
            int transx = Connection.TRANSACTION_READ_COMMITTED;
            boolean autoCommit = false;
            try {
                conn = session.connection();

                autoCommit = conn.getAutoCommit();
                if (autoCommit) {
                    conn.setAutoCommit(false);
                }
                do {
                    transx = conn.getTransactionIsolation();
                    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

                    try (PreparedStatement qps = conn.prepareStatement(query); ResultSet rSet = qps.executeQuery()) {
                        if (!rSet.next()) {
                            String err = "could not read a hi value - you need to populate the table: " + seedContainerTableName;
                            LOGGER.error(err);
                            throw new IdentifierGenerationException(err);
                        }
                        currentHiVal = rSet.getInt(1);
                        installId = rSet.getString(2);
                    } catch (SQLException | HibernateException sqle) {
                        LOGGER.error("could not read a hi value", sqle);
                        throw sqle;
                    }

                    try (PreparedStatement ups = conn.prepareStatement(update)) {
                        ups.setInt(1, currentHiVal + 1);
                        ups.setString(2, installId);
                        rows = ups.executeUpdate();
                    } catch (SQLException sqle) {
                        LOGGER.error("could not update hi value in: " + seedContainerTableName, sqle);
                        throw sqle;
                    }
                } while (rows == 0);

            } finally {
                if (conn != null) {
                    conn.commit();
                    conn.setAutoCommit(autoCommit);
                    conn.setTransactionIsolation(transx);
                } 
                if (!Thread.holdsLock(this) && conn != null) {
                    conn.close();
                }
            }
            
            return Integer.valueOf(currentHiVal);
        }
    }

    /**
     * This method will call again the realGenerate method if any exception occurs.
     * @param session
     * @param obj
     * @return
     */
    public boolean retryRealGenerate(SharedSessionContractImplementor session, Object obj) {
        synchronized (this) {
            boolean generated = false;
            try {
                hi = ((Integer) realGenerate(session, obj)).intValue();
                lo = 1;
                generated = true;
            } catch (SQLException | HibernateException e) {
                try {
                    hi = ((Integer) realGenerate(session, obj)).intValue();
                    lo = 1;
                } catch (SQLException | HibernateException e1) {
                    generated = false;
                }

            }
            return generated;
        }
    }

    public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
        return new String[]{ "create table " + seedContainerTableName + " ( " + highOidColName + " " + dialect.getTypeName(Types.INTEGER) + " )",
                "insert into " + seedContainerTableName + " values ( 0 )" };
    }

    public String sqlDropString(Dialect dialect) {
        StringBuffer sqlDropString = new StringBuffer().append("drop table ");
        if (dialect.supportsIfExistsBeforeTableName()) {
            sqlDropString.append("if exists ");
        }
        sqlDropString.append(seedContainerTableName).append(dialect.getCascadeConstraintsString());
        if (dialect.supportsIfExistsAfterTableName()) {
            sqlDropString.append(" if exists");
        }
        return sqlDropString.toString();
    }

    public Object generatorKey() {
        return seedContainerTableName;
    }

    public static String getNextOid(String installationId, long hiValue, long hiPlusLow) {
        return installationId + // Strictly limited to 3 chars
                pad(Long.toHexString(hiValue), 8, '0') + // Range of 4294967295 (429Million), and after that change the install ID and reset the HI to
                                                         // 1
                pad(Long.toHexString(hiPlusLow), 9, '0');// wITH MAX LOW AT 100k this gives us a range of 429M s Hi Value, before we start overflowing
                                                         // hiPlus Low in Hex. Note we just pad and not truncate (the reason we do this so that we
                                                         // will get and error and we will know, else it will mess up the DB)
    }

    public static String pad(String str, int length, char value) {
        StringBuffer sb = new StringBuffer();
        int num = length - str.length();
        for (int i = 0; i < num; i++) {
            sb.append(value);
        }
        sb.append(str);
        return sb.toString();
    }

    public String[] sqlDropStrings(Dialect arg0) throws HibernateException {
        return new String[0];
        
    }

    @Override
    public void registerExportables(Database arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void configure(Type arg0, Properties params, ServiceRegistry registry) throws MappingException {
        this.seedContainerTableName = ConfigurationHelper.getString(TABLE, params, "seed_container");
        this.highOidColName = ConfigurationHelper.getString(COLUMN, params, "high_oid");
        this.installIdColName = ConfigurationHelper.getString(INSTALL_ID, params, "seed_id");

        String schemaName = params.getProperty(SCHEMA);
        if (schemaName != null && seedContainerTableName.indexOf('.') < 0) {
            seedContainerTableName = schemaName + '.' + seedContainerTableName;
        }

        query = "select " + highOidColName + "," + installIdColName + " from " + seedContainerTableName;
        update = "update " + seedContainerTableName + " set " + highOidColName + " = ? where " + installIdColName + " = ?";

        maxLo = ConfigurationHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
        lo = maxLo + 1;

    }
}
