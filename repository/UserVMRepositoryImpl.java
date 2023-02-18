package com.ghx.api.operations.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import com.ghx.api.operations.util.QueryHelper;

/**
 * 
 * @author Ajith
 *
 */
@Repository
public class UserVMRepositoryImpl implements UserVMRepositoryCustom {

    /** The EntityManager */
    @PersistenceContext
    private transient EntityManager entityManager;

    /** The QueryHelper */
    @Autowired
    private transient QueryHelper queryHelper;

    /**
     * get Active Users
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getActiveUsers(Set<String> userIds) {
        StringBuilder strQuery = new StringBuilder(
                "select uv.user_id from vision.user_vm uv where lower(uv.user_id) in :userIds and uv.user_status_code = 'ACT' ");
        return entityManager.createNativeQuery(strQuery.toString()).setParameter("userIds", userIds).getResultList();
    }

    /**
     * get Inactive Users
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getInactiveUsers(Set<String> userIds) {
        StringBuilder strQuery = new StringBuilder(
                "select uv.user_id from vision.user_vm uv where lower(uv.user_id) in :userIds and uv.user_status_code in ('INACT','InACT')");
        return entityManager.createNativeQuery(strQuery.toString()).setParameter("userIds", userIds).getResultList();
    }

    /**
     * Get Tier Change Requestor Name
     * @param emailId
     * @return String
     */
    @Override
    public String getTierChangeRequestorName(String emailId) {
        StringBuilder strQuery = new StringBuilder("select CONCAT(uv.first_name, ' ', uv.last_name) from vision.user_vm uv where lower(uv.user_id) = ?1 ");
        return (String) entityManager.createNativeQuery(strQuery.toString()).setParameter(1, emailId).getSingleResult();
    }

    /**
     * fetch User Details
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public List<Map<String, Object>> fetchUserDetails(Set<String> userIds) {
        String query = queryHelper.fetchUserDetails().toString();
        return entityManager.createNativeQuery(query).setParameter("userId", userIds).unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }

}
