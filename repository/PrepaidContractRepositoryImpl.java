package com.ghx.api.operations.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghx.api.operations.dto.PrepaidContractDTO;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.model.PrepaidContract;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.QueryHelper;

import ma.glasnost.orika.MapperFacade;

/**
 * The Class PrepaidContractRepositoryImpl.
 */
@Repository
@SuppressWarnings({ "unchecked", "deprecation" })
public class PrepaidContractRepositoryImpl implements PrepaidContractRepositoryCustom {
    
    /** The entity manager. */
    @PersistenceContext
    private transient EntityManager entityManager;

    @Autowired
    private transient QueryHelper queryHelper;


    @Autowired
    private transient MapperFacade mapper;

    @Override
    public List<Map<String, Object>> searchVendorByFein(String fein) {

        return entityManager.createNativeQuery("select vd.ein_or_ssn,vd.legal_name from vision.vendor_detail vd where ein_or_ssn  = ?")
                .setParameter(1, fein).unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }

    @Override
    public PrepaidContractDTO findDetailsByOid(String oid) {
        List<Map<String, Object>> listOfMaps = entityManager
                .createNativeQuery(queryHelper.buildPrepaidContractQuery().append(" where pc.oid = ?1 group by pc.oid, l.description ").toString()).setParameter(1, oid)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return CollectionUtils.isNotEmpty(listOfMaps) ? mapper.map(listOfMaps.get(0), PrepaidContractDTO.class) : null;
    }

    @Override
    public PrepaidContractDTO findDetailsByFein(String fein) {

        List<Map<String, Object>> listOfMaps = entityManager
                .createNativeQuery(queryHelper.buildPrepaidContractQuery().append(" where pcs.fein = ?1 group by pc.oid, l.description order by pc.contract_end_date desc ").toString()).setParameter(1, fein)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return CollectionUtils.isNotEmpty(listOfMaps) ? mapper.map(listOfMaps.get(0), PrepaidContractDTO.class) : null;
    }

    @Override
    public PrepaidContractDTO getActualUserAndIDNCountByFein(String fein) {
        StringBuilder query = new StringBuilder()
                .append("select count(distinct vc.oid) as current_idn_count,count(distinct uv.oid) as current_user_count")
                .append(" from vision.vendor_detail vd left join vision.prepaid_contracts pc on")
                .append(" pc.fein = vd.ein_or_ssn left join vision.vcrelation vc on vd.vendor_oid = vc.vendor_oid")
                .append(" LEFT join vision.vendor_rep vr on  vc.oid = vr.vcrelation_oid LEFT JOIN vision.actor a ON ")
                .append(" vr.oid=a.oid LEFT JOIN vision.user_vm uv ON a.user_oid = uv.oid where ")
                .append(" (vc.status_code = 'ACT'or vc.status_code = 'ACTCR')").append(" and uv.user_status_code = 'ACT' and vd.ein_or_ssn = ?");

        Map<String, Object> map = (Map<String, Object>) entityManager.createNativeQuery(query.toString()).setParameter(1, fein)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();

        PrepaidContractDTO configDTO = new PrepaidContractDTO();
        configDTO.setCurrentIDNCount(Integer.parseInt(map.get("current_idn_count").toString()));
        configDTO.setCurrentUserCount(Integer.parseInt(map.get("current_user_count").toString()));
        return configDTO;
    }


    /**
     * This method is used to fetch prepaid grid contract details.
     *
     * @param prepaidDetails
     *            the payload DTO
     * @return the list
     */
    @Override
    public List<PrepaidContractDTO> findAllPrepaidContracts(PrepaidContractDTO prepaidDetails, Pageable pageable, String requestType) {

        final StringBuffer query = queryHelper.buildPrepaidContractQueryStr(prepaidDetails, requestType);

        // sorting condition
        queryHelper.appendPrepaidContractSort(prepaidDetails, query, pageable);

        Query sqlQuery = entityManager.createNativeQuery(query.toString(), PrepaidContract.FIND_BY_PREPAID_CONTRACT_RESULTSET);

        int firstResult = pageable.getPageSize() * (pageable.getPageNumber() + 1) - pageable.getPageSize();

        int maxResult = pageable.getPageSize();

        // set parameter conditions
        queryHelper.parameterConditions(prepaidDetails, sqlQuery);

        sqlQuery.setFirstResult(firstResult).setMaxResults(maxResult);

        List<PrepaidContractDTO> prpaidContractList = sqlQuery.getResultList();
        if (StringUtils.equalsIgnoreCase(requestType, ConstantUtils.GET)) {
            prpaidContractList.forEach(l -> {
                List<String> feins = Objects.nonNull(l.getFein()) ? Arrays.asList(l.getFein().split(",")) : null;
                List<String> supplierNames = Objects.nonNull(l.getSupplierName()) ? Arrays.asList(l.getSupplierName().split(ConstantUtils.RELATIONSHIP_LIST_SPLITTER)) : null;
                l.setFein(CollectionUtils.isNotEmpty(feins) ? feins.get(0) : "");
                l.setSupplierName(CollectionUtils.isNotEmpty(supplierNames) ? supplierNames.get(0) : "");
            });
        }
        // This part is only using for export with fein or supplier name search 
        if (StringUtils.equalsIgnoreCase(requestType, ConstantUtils.EXPORT)
                && (StringUtils.isNoneEmpty(prepaidDetails.getFein()) || StringUtils.isNoneEmpty(prepaidDetails.getSupplierName()))) {
            List<String> oidList = new ArrayList<>();
            prpaidContractList.forEach(list -> {
                oidList.add(list.getOid());
            });
            final StringBuffer exportQuery = queryHelper.buildPrepaidContractQueryStr(prepaidDetails, ConstantUtils.SEARCH_EXPORT);
            queryHelper.appendPrepaidContractSort(prepaidDetails, exportQuery, pageable);
            Query sqlExportQuery = entityManager.createNativeQuery(exportQuery.toString(), PrepaidContract.FIND_BY_PREPAID_CONTRACT_RESULTSET)
                    .setParameter("oid", oidList);
            int firstExportResult = pageable.getPageSize() * (pageable.getPageNumber() + 1) - pageable.getPageSize();
            int maxExportResult = pageable.getPageSize();
            sqlExportQuery.setFirstResult(firstExportResult).setMaxResults(maxExportResult);
            prpaidContractList = sqlExportQuery.getResultList();
        }

        return prpaidContractList;
    }


    /**
     * This method is used to fetch prepaid grid contract counts.
     *
     * @param prepaidDetails
     *            the payload DTO
     * @return the long
     */
    @Override
	public String findPrepaidContractCount(PrepaidContractDTO prepaidDetails) {

		final StringBuffer query = new StringBuffer();

		query.append("SELECT count(*) from (" + queryHelper.buildPrepaidContractQueryStr(prepaidDetails, ConstantUtils.EMPTY) + ") count");

		Query sqlQuery = entityManager.createNativeQuery(query.toString());

		//set parameter conditions
		queryHelper.parameterConditions(prepaidDetails, sqlQuery);

		return sqlQuery.getSingleResult().toString();
	}

    @Override
    public String getLookupNameByCode(String code) {
        Map<String, Object> map = (Map<String, Object>) entityManager
                .createNativeQuery("select l.description as tiername from vision.lookup l where l.code = ?").setParameter(1, code)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
        return MapUtils.isNotEmpty(map) ? (String) map.get("tiername") : null;

    }

    /**
     * find prepaid oid by fein
     */
    @Override
    public List<Map<String, Object>> findPrepadiOidByFein(List<String> feinList) {
        StringBuilder query = new StringBuilder().append(
                "select pcs.prepaid_contract_oid, pcs.fein from prepaid_contracts pc join prepaid_contract_suppliers pcs on pc.oid = pcs.prepaid_contract_oid\n"
                        + " where pc.contract_start_date <= CURRENT_DATE and pc.contract_end_date >= CURRENT_DATE and pc.deleted is false and pcs.fein in (?1)");
        return (List<Map<String, Object>>) entityManager.createNativeQuery(query.toString()).setParameter(1, feinList)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }

    /**
     * Fetch list of associated feins for prepaid contract
     *
     * @param prepaidContractOid
     * @return list
     */
    @Override
    public List<String> getPrepaidFeins(String prepaidContractOid) {
        StringBuilder query = new StringBuilder(" select pcs.fein from vision.prepaid_contract_suppliers pcs where pcs.prepaid_contract_oid = ?1 ");
        return entityManager.createNativeQuery(query.toString()).setParameter(1, prepaidContractOid).getResultList();
    }

    /**
     * Fetch list of supplier details for prepaid contract
     *
     * @param feins
     * @param pageable
     * @return list
     */
    @Override
    public List<PrepaidContractSupplierDTO> fetchSupplierDetails(List<String> feins, String oid, Pageable pageable) {
        StringBuilder selectQuery = new StringBuilder(
                "select pcs.prepaid_contract_oid as oid, pcs.fein, case when vd.ein_or_ssn = '' or vd.ein_or_ssn is null then 'NA' else cast(count(distinct u.oid) as varchar) end as \"userCount\", case when vd.ein_or_ssn = '' or vd.ein_or_ssn is null then 'NA' else CAST(count(distinct vc.oid) as varchar) end as \"idnCount\", case when vd.legal_name = '' or vd.legal_name is null then pcs.supplier_name else vd.legal_name end as \"legalName\" "
                + " from vision.prepaid_contract_suppliers pcs left join vision.vendor_detail vd on pcs.fein = vd.ein_or_ssn ")
                        .append(queryHelper.joinQueryForPrepaidSuppliers())
                        .append(" where pcs.fein in (?1) and pcs.prepaid_contract_oid = ?2 group by pcs.prepaid_contract_oid, pcs.fein, pcs.supplier_name, vd.legal_name, vd.ein_or_ssn ");

        pageable.getSort().get().forEach(order -> {
            String fieldName = order.getProperty();
            String direction = order.getDirection().name();
            if (StringUtils.equalsAnyIgnoreCase(fieldName, ConstantUtils.LEGALNAME)) {
                selectQuery.append(" order by case when vd.legal_name = '' or vd.legal_name is null then pcs.supplier_name else vd.legal_name end ").append(direction);
            } else {
                selectQuery.append(" order by ").append(fieldName).append(StringUtils.SPACE).append(direction);
            }
        });
        int firstResult = pageable.getPageSize() * (pageable.getPageNumber() + 1) - pageable.getPageSize();
        int maxResult = pageable.getPageSize();

        return entityManager.createNativeQuery(selectQuery.toString()).setParameter(1, feins).setParameter(2, oid).setFirstResult(firstResult).setMaxResults(maxResult)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(PrepaidContractSupplierDTO.class))
                .getResultList();
    }

    /**
     * Returns number of users marked as unpaid
     *
     * @param feins
     * @param updatedBy
     * @return int
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int markUsersUnpaid(List<String> feins, String updatedBy) {
        StringBuilder sqlQuery = new StringBuilder(
                "update vision.user_detail as ud set paid_user = false, paid_amount = 0, discount_amount = 0, renewal_date = null, paid_tier_plan_code = null, coupon_code = null, updated_on = current_timestamp, updated_by = ?2 "
                        + "from vision.user_vm as uv join vision.vendor_detail vd on uv.org_oid = vd.vendor_oid where vd.ein_or_ssn  in (?1) "
                        + "and ud.paid_user is true  and uv.oid = ud.user_oid ");
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, feins).setParameter(2, updatedBy).executeUpdate();
    }

    /**
     * Returns number of users marked as unpaid
     *
     * @param feins
     * @param updatedBy
     * @param paidTierPlanCode
     * @return int
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int markUsersPaid(List<String> feins, String updatedBy, String paidTierPlanCode) {
        StringBuilder sqlQuery = new StringBuilder(
                "update vision.user_detail as ud set paid_user = true, updated_by = ?2, paid_tier_plan_code = ?3, updated_on = current_timestamp, paid_amount = 0, renewal_date = null, coupon_code = null "
                        + "from vision.user_vm as uv join vision.vendor_detail vd on uv.org_oid = vd.vendor_oid where vd.ein_or_ssn  in (?1) "
                        + " and uv.oid = ud.user_oid ");
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, feins).setParameter(2, updatedBy).setParameter(3, paidTierPlanCode).executeUpdate();
    }
}
