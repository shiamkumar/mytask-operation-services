package com.ghx.api.operations.repository;

import static org.apache.commons.collections.MapUtils.getString;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.dto.ProviderDetailsDTO;
import com.ghx.api.operations.dto.SupplierDetailsDTO;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.QueryHelper;

/**
 * 
 * @author Ajith
 *
 */
@Repository
public class VendorDetailRepositoryImpl implements VendorDetailRepositoryCustom {
    /** The EntityManager */
    @PersistenceContext
    private transient EntityManager entityManager;
    /** The QueryHelper */
    @Autowired
    private transient QueryHelper queryHelper;
    /** The ModelMapper */
    private final transient ModelMapper modelMapper = new ModelMapper();

    /**
     * get VendorDetails By VendorOid
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public SupplierDetailsDTO getVendorDetailsByVendorOid(String vendorOid) {
        Query sqlQuery = entityManager.createNativeQuery(queryHelper.getVendorDetailsByVendorOid().toString());
        Map<String, Object> vendorDetails = (Map<String, Object>) sqlQuery.setParameter("vendorOid", vendorOid)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList().stream().findFirst()
                .orElse(null);
        return modelMapper.map(vendorDetails, SupplierDetailsDTO.class);
    }

    @Override
    @SuppressWarnings({ "unchecked", "deprecation" })
    public List<Map<String, Object>> getPrepaidContractVendorDetails(List<String> feins) {
        StringBuilder query = new StringBuilder(
                "select vd.legal_name as \"legalName\", max(case when (pc.oid is not null  and pc.contract_start_date <= CURRENT_DATE "
                + " and pc.contract_end_date >= CURRENT_DATE and pc.deleted is false) then 'true' else 'false' end) as \"contractExists\" ,"
                + " max(case when (vd.ein_or_ssn is null and pcs.fein is not null) then pcs.fein else vd.ein_or_ssn end ) as fein"        
                + " from vision.prepaid_contract_suppliers pcs left join vision.prepaid_contracts pc on pc.oid = pcs.prepaid_contract_oid"
                + " right outer join vision.vendor_detail vd on pcs.fein = vd.ein_or_ssn right outer join vision.vendor v on vd.vendor_oid = v.oid "
                + " where (vd.enable_program_change is true and vd.ein_or_ssn in (?1) and v.vendor_status_code = 'ACT') or (pcs.fein in (?1) and vd.ein_or_ssn is null)"
                + " group by vd.legal_name");
        
        return (List<Map<String, Object>>) entityManager.createNativeQuery(query.toString()).setParameter(1, feins)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }
    
    /**
     * get vendor details by feins
     * @param feins
     */
    @Override
    @SuppressWarnings({ "deprecation", "unchecked" })
    public List<PrepaidContractSupplierDTO> getVendorDetailsByFein(List<String> feins) {
        StringBuilder query = new StringBuilder(
                "select vd.legal_name as \"legalName\", vd.ein_or_ssn as fein from vision.vendor_detail vd where vd.ein_or_ssn in (?1) ");
        List<Map<String, Object>> vendorDetails = (List<Map<String, Object>>) entityManager.createNativeQuery(query.toString()).setParameter(1, feins)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return vendorDetails.stream().map(vendorDetail -> modelMapper.map(vendorDetail, PrepaidContractSupplierDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * get vendor details by oid
     * @param oid
     */
    @Override
    @SuppressWarnings({ "deprecation", "unchecked" })
    public Map<String, Object> getDetailsByVendorOid(String oid) {
        StringBuilder query = new StringBuilder(
                "select vd.tier_plan_code as \"tierPlanCode\", v.vendor_status_code as \"vendorStatus\" from vision.vendor_detail vd join vision.vendor v on vd.vendor_oid = v.oid where vd.vendor_oid = ?1 ");
        return (Map<String, Object>) entityManager.createNativeQuery(query.toString()).setParameter(1, oid).unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
    }

    /**
     * Get User and IDN Count details by Feins
     * @param feins
     * @return
     */
    public Map<String, Object> getUserAndIdnCountByFein(List<String> feins) {
        StringBuilder query = new StringBuilder(" select vd.ein_or_ssn as fein, cast(count(distinct u.oid) as varchar) as \"userCount\" , CAST(count(distinct vc.oid) as varchar) as \"idnCount\""
                + " from vision.vendor_detail vd ");
        query.append(queryHelper.joinQueryForPrepaidSuppliers())
        .append(" where vd.ein_or_ssn in (?1) group by vd.ein_or_ssn ");
        List<Map<String, Object>> resultList = entityManager.createNativeQuery(query.toString()).setParameter(1, feins).unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        Map<String, Object> userIdnCountDetails = new HashMap<>();
        AtomicInteger totalUserCount = new AtomicInteger();
        AtomicInteger totalIdnCount = new AtomicInteger();
        if (CollectionUtils.isEmpty(resultList)) {
            return userIdnCountDetails;
        } else {
            resultList.forEach(userIdnDetail -> {
                int userCount = MapUtils.getInteger(userIdnDetail, "userCount");
                int idnCount = MapUtils.getInteger(userIdnDetail, "idnCount");
                totalIdnCount.addAndGet(idnCount);
                totalUserCount.addAndGet(userCount);
                userIdnCountDetails.put(getString(userIdnDetail, "fein"), userIdnDetail);
            });
            userIdnCountDetails.put("totalUserCount", totalUserCount);
            userIdnCountDetails.put("totalIdnCount", totalIdnCount);
        }
        return userIdnCountDetails;
    }

    /**
     * get activeUser for Prepaid contract
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int activeUserCountForPrepaid(String prepaidContractOid) {
        StringBuilder countQuery = new StringBuilder("select count(distinct uv.oid) from vision.user_vm uv ")
                .append(queryHelper.activeUserForPrepaid().toString());
        Query sqlQuery = entityManager.createNativeQuery(countQuery.toString());
        List users = sqlQuery.setParameter("prepaidContractOid", prepaidContractOid).getResultList();
        return CollectionUtils.isNotEmpty(users) ? ((BigInteger) users.get(0)).intValue() : ConstantUtils.ZERO_INDEX;
    }

    /**
     * fetch paid user count for vendor
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int getPaidUserCount(String vendorOid) {
        int count = 0;
        StringBuilder query = queryHelper.buildPaidUserQuery();
        Query sqlQuery = entityManager.createNativeQuery(query.toString());
        List userCount = sqlQuery.setParameter(ConstantUtils.VENDOR_OID, vendorOid).getResultList();
        if (CollectionUtils.isNotEmpty(userCount)) {
            count = ((BigInteger) userCount.get(0)).intValue();
        }
        return count;
    }

    /**
     * update vendor detail table pricing tier code
     * @param feins
     * @param updatedBy
     * @param pricingTierCode
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateVendorPricingTier(List<String> feins, String updatedBy, String pricingTierCode) {
        StringBuilder sqlQuery = new StringBuilder(
                " update vision.vendor_detail set tier_plan_code = ?1, updated_on = CURRENT_TIMESTAMP, updated_by = ?2 where ein_or_ssn in (?3)");
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, pricingTierCode).setParameter(2, updatedBy).setParameter(3, feins)
                .executeUpdate();
    }

    /**
     * get VendorDetails By VendorOid
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public SupplierDetailsDTO getVendorDetailsByOid(String vendorOid) {
        Query sqlQuery = entityManager.createNativeQuery(queryHelper.buildVendorDetailsByVendorOid().toString());
        Map<String, Object> vendorDetails = (Map<String, Object>) sqlQuery.setParameter("vendorOid", vendorOid)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList().stream().findFirst()
                .orElse(null);
        return vendorDetails != null ? modelMapper.map(vendorDetails, SupplierDetailsDTO.class) : null;
    }
    
    @Override
    public Map<String, Object> getPaidUserAndIdnCount(List<String> vendorOids, List<String> selectedIdns, String userOid, String currentVendorOid) {
            StringBuilder query = queryHelper.buildPaidUserAndIdnQuery(selectedIdns,userOid,currentVendorOid);
            Query sqlQuery = entityManager.createNativeQuery(query.toString());
            if (CollectionUtils.isNotEmpty(selectedIdns)) {
                    sqlQuery.setParameter("idns", selectedIdns);
                    if (StringUtils.isNotEmpty(currentVendorOid)) {
                        sqlQuery.setParameter("currentVendorOid", currentVendorOid);
                    }
            } 
            if (StringUtils.isNotEmpty(userOid)) {
                    sqlQuery.setParameter("userOid", userOid);
            }
            
            List<Map<String, Object>> paidUserIDNList = sqlQuery.setParameter("vendorOid", vendorOids).unwrap(org.hibernate.query.Query.class)
                    .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
            if(CollectionUtils.isNotEmpty(paidUserIDNList)) {
                return paidUserIDNList.get(0);
            }
            return null;
    }

    /**
     * Get IDN details
     * @param eliminatedIdns list
     * @param pageable
     * @return
     */
    @Override
    public List<ProviderDetailsDTO> getIdnsDetails(List<String> eliminatedIdns, Pageable pageable) {
        StringBuilder sqlQuery = new StringBuilder(
                "select c.oid, c.company_name as \"companyName\" from vision.customer c where c.oid in (?1) order by c.company_name asc ");
        int firstResult = pageable.getPageSize() * (pageable.getPageNumber() + 1) - pageable.getPageSize();
        int maxResult = pageable.getPageSize();
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, eliminatedIdns).setFirstResult(firstResult)
                .setMaxResults(maxResult).unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(ProviderDetailsDTO.class))
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getVendorByFein(List<String> feins) {
        StringBuilder query = new StringBuilder(" select vd.vendor_oid from vision.vendor_detail vd where vd.ein_or_ssn in (?1) ");
        return entityManager.createNativeQuery(query.toString()).setParameter(1, feins).getResultList();
    }
    
    /**
     * get activeUser for Prepaid contract
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<String> activeUsersForPrepaid(List<String> feins) {
        StringBuilder countQuery = new StringBuilder("select distinct uv.oid from vision.user_vm uv ")
                .append(queryHelper.activeUserForSupplier().toString());
        Query sqlQuery = entityManager.createNativeQuery(countQuery.toString());
        List users = sqlQuery.setParameter("fein", feins).getResultList();
        return CollectionUtils.isNotEmpty(users) ? users : new ArrayList<>();
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public Map<String, Object> getVendorDetailsByFein(String fein) {
        StringBuilder query = new StringBuilder(
                "select distinct v.oid as \"vendorOid\", vd.legal_name as \"legalName\", vd.ein_or_ssn as fein , v.vendor_status_code as status , vd.enable_program_change as \"enableProgramChange\" "
                        + " ,gp.oid as \"globalProfileOid\", gp.deleted as \"deleted\",gp.expiration_date as \"expirationDate\", gp.grp_plan as \"grpPlan\" "
                        + " from vision.vendor_detail vd join vendor v on v.oid = vd.vendor_oid left join vision.global_profile_keys gpk  on gpk.key = vd.ein_or_ssn "
                        + " left join vision.global_profile gp on gp.oid = gpk.global_profile_oid where vd.ein_or_ssn = ? ");
        List<Map<String, Object>> suppliers = entityManager.createNativeQuery(query.toString()).setParameter(1, fein)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return CollectionUtils.isNotEmpty(suppliers) ? suppliers.get(0) : null;
    }

    /**
     * fetch Global Profile
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public List<Map<String, Object>> fetchVendorInfo(String fein) {
        return entityManager.createNativeQuery(queryHelper.buildVendorInfoQuery().toString()).setParameter(1, fein)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }

    /**
     * fetch Merge Supplier Request for fein
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int fetchMergeSupplierRequest(String fein) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select count(mvr.oid) from merge_vendor_request mvr where deleted_fein = ? ");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }

    /**
     * get Rfpmt Count
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int getRfpmtCount(String fein, Timestamp paymentProfileGraceDate) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append(
                "select count(distinct vc.oid) from vision.vcrelation vc join vision.payment_profile pp on vc.oid = pp.vcrelation_oid and vc.status_code='RFPMT' ")
                .append(" and pp.created_on > '").append(paymentProfileGraceDate)
                .append("' join vision.vendor_detail vd on vc.vendor_oid = vd.vendor_oid  where vc.customer_oid !='vendormate' and vc.express_registered is false and  ein_or_ssn = ? ");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }

    /**
     * check GRP For Fein
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int checkGRPForFein(String fein) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select count(gpk) from  global_profile_keys gpk join global_profile gp on gp.oid = gpk.global_profile_oid ")
                .append(" where gpk.key = ? and (gp.expiration_date is null or gp.expiration_date >= cast(now() as date)) and gp.deleted is false");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }


    /**
     * check Prepaid contract For Fein
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int checkFeinHasNewPrepaidContract(String fein) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select count(pcs) from prepaid_contract_suppliers pcs join prepaid_contracts pc on  pc.oid=pcs.prepaid_contract_oid  ")
                .append(" where pc.contract_start_date <= CURRENT_DATE and pc.contract_end_date >= CURRENT_DATE and pc.deleted is false and  pcs.fein = ?  ");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }

    /**
     * get Rfpmt Count
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int getAllRfpmtCount(String fein) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select count(distinct vc.oid) from vision.vcrelation vc  join vision.vendor_detail vd on vc.vendor_oid = vd.vendor_oid")
                .append(" where  vc.status_code='RFPMT' and vc.customer_oid !='vendormate' and vc.express_registered is false and  vd.ein_or_ssn = ? ");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }

    /**
     * fetchAssociatedGrpFeins
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> fetchAssociatedGrpFeins(String fein) {
        StringBuilder sqlQuery = queryHelper.fetchAssociatedGrpFeinsQuery();
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
    }

    /**
     * fetch Users Count
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public List<Map<String, Object>> fetchUsersCount(List<String> totalFeins) {
        StringBuilder sqlQuery = queryHelper.fetchUsersCountQuery();
        return entityManager.createNativeQuery(sqlQuery.toString()).setParameter(ConstantUtils.FEIN, totalFeins)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
    }

    /**
     * fetch inactive vc count for vendor
     */
    @SuppressWarnings("rawtypes")
    @Override
    public int getInactiveVcCount(String fein) {
        int count = 0;
        StringBuilder sqlQuery = new StringBuilder(
                "select count(distinct vc.oid) from vision.vcrelation vc  join vision.vendor_detail vd on vc.vendor_oid = vd.vendor_oid")
                        .append(" where  vc.status_code='INACT' and vc.express_registered is false and  vd.ein_or_ssn = ? ");
        List result = entityManager.createNativeQuery(sqlQuery.toString()).setParameter(1, fein).getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            count = ((BigInteger) result.get(0)).intValue();
        }
        return count;
    }
    
}
