package com.ghx.api.operations.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.util.QueryHelper;


/**
 *
 * @author Ajith
 *
 */
@Repository
public class VCRelationRepositoryImpl implements VCRelationRepositoryCustom {

    /** The EntityManager */
    @PersistenceContext
    private transient EntityManager entityManager;

    /** The QueryHelper */
    @Autowired
    private transient QueryHelper queryHelper;

    /**
     * validate one active rep
     */
    @Override
    @SuppressWarnings({ "unchecked", "deprecation" })
    public Map<String, String> validateOneActiveRep(String idnOid) {
        Query sqlQuery = entityManager.createNativeQuery(queryHelper.validateOneActiveRep().toString());
        return (Map<String, String>) sqlQuery.setParameter("idnOid", idnOid).unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList().stream().findFirst().orElse(null);
    }

    /**
     * Get impacted reps message details
     * @param customerOids
     * @param vendorOid
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, String>> getTierRequestMessageDetails(List<String> customerOids, String vendorOid) {
        return entityManager.createNativeQuery(queryHelper.getTierRequestMessageDetails().toString()).setParameter(1, customerOids)
                .setParameter(2, vendorOid).unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }
    
    
	/**
	 * Get paid and unpaid Idn count 
	 *	@param vendorOid
	 *	@param paid status
	 */
    @Override
	public int getIDNCount(String vendorOid, boolean isPaid) {
    	List result = entityManager.createNativeQuery(queryHelper.getIDNCount(isPaid).toString()).setParameter("vendorOid", vendorOid).getResultList();
    	return ((BigInteger) result.get(0)).intValue();
    }
    
}
