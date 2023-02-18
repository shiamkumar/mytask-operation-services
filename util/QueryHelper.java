package com.ghx.api.operations.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.PrepaidContractDTO;

/**
 * @author Anithaa K S The Class QueryHelper.
 */
@Component
public class QueryHelper {

    /**
     * Builds the prepaid contract query str.
     *
     * @param prepaidDetails
     *            the payload DTO
     * @return the string buffer
     */
    public StringBuffer buildPrepaidContractQueryStr(PrepaidContractDTO prepaidDetails, String requestType) {
        final StringBuffer query = new StringBuffer();
        query.append(
                " select pc.oid , string_agg(pcs.fein, ',') as fein, string_agg( case when vd.legal_name = '' or vd.legal_name is null then  pcs.supplier_name else \n"
                        + "vd.legal_name end, '#:#') as supplierName, case when pc.contract_end_date < CURRENT_DATE then 'Expired' when pc.contract_start_date <= CURRENT_DATE \n"
                        + "and pc.contract_end_date >= CURRENT_DATE then 'Active' when pc.contract_start_date > CURRENT_DATE then 'Pending' end as contractStatus,\n"
                        + "pc.contract_start_date as contractStartDate, pc.contract_end_date as contractEndDate, pc.current_user_count as currentUserCount, pc.current_idn_count as currentIDNCount,\n"
                        + "pc.max_user_count as maxUserCount, pc.max_idn_count as maxIDNCount, pc.updated_by as updatedBy, pc.notes, l.code as pricingTierCode, \n"
                        + "pc.created_on as createdOn, pc.created_by as createdBy, pc.updated_on as updatedOn, l.description as pricingTierName,\n"
                        + "(select count(prepaid_contract_oid) from prepaid_contract_suppliers pcs where pcs.prepaid_contract_oid =pc.oid) as supplierCount  \n"
                        + "from prepaid_contracts pc\n" + "join lookup l on l.code = pc.pricing_tier_code\n"
                        + "join vision.prepaid_contract_suppliers pcs on pcs.prepaid_contract_oid = pc.oid\n"
                        + "left join vision.vendor_detail vd on pcs.fein = vd.ein_or_ssn \n"
                        + "where l.category = 'Prepaid' and pc.deleted is false ");
     // filter conditions
        if (StringUtils.equalsIgnoreCase(requestType, ConstantUtils.SEARCH_EXPORT)) {
            query.append(" and pc.oid in (:oid) group by pc.oid, l.description,l.code ");
        } else {
            appendPrepaidContractFilters(prepaidDetails, query);
        }
        return query;
    }
    
    /**
     * Append prepaid contract sort.
     *
     * @param prepaidDetails
     *            the payload DTO
     * @param query
     *            the query
     */
    public void appendPrepaidContractSort(PrepaidContractDTO prepaidDetails, final StringBuffer query, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            List<String> sortFields = new ArrayList<>();
            String orderType = pageable.getSort().get().map(value -> value.getDirection()).collect(Collectors.toList()).get(0).name();
            pageable.getSort().get().forEach(order -> {
                sortFields.add(ConstantUtils.SORT_MAP.get(order.getProperty()));
            });
            query.append(" order by " + String.join(ConstantUtils.COMMA, sortFields) + " " + orderType);
        }
    }

    /**
     * Append prepaid contract filters.
     *
     * @param prepaidDetails
     *            the payload DTO
     * @param query
     *            the query
     */
	private void appendPrepaidContractFilters(PrepaidContractDTO prepaidDetails, final StringBuffer query) {
		if (StringUtils.isNoneBlank(prepaidDetails.getFein())) {
			query.append(" and pcs.fein = :fein ");
		}
		if (StringUtils.isNoneBlank(prepaidDetails.getSupplierName())) {

			query.append(" and (lower(pcs.supplier_name) like lower(:supplierName) or Lower(vd.legal_name) like lower(:supplierName))");
		}
		if (null != prepaidDetails.getStartFromDt() && null != prepaidDetails.getStartToDt()) {

			query.append(" and pc.contract_start_date between :startFromDate and :startToDate");
		}
		if (null != prepaidDetails.getEndFromDt() && null != prepaidDetails.getEndToDt()) {

			query.append(" and pc.contract_end_date between :endFromDate and :endToDate");
		}

		if (StringUtils.isNoneBlank(prepaidDetails.getUpdatedBy())) {
			query.append(" and Lower(pc.updated_by) like Lower(:updatedBy)");
		}

		if (StringUtils.isNoneBlank(prepaidDetails.getContractStatus())) {

			if (ConstantUtils.EXPIRED.equalsIgnoreCase(prepaidDetails.getContractStatus())) {
				query.append(" and pc.contract_end_date < CURRENT_DATE");
			} else if (ConstantUtils.ACTIVE.equalsIgnoreCase(prepaidDetails.getContractStatus())) {
				query.append(" and pc.contract_start_date <= CURRENT_DATE and pc.contract_end_date >= CURRENT_DATE");
			} else if (ConstantUtils.PENDING.equalsIgnoreCase(prepaidDetails.getContractStatus())) {
				query.append(" and pc.contract_start_date > CURRENT_DATE");
			}

		}

		if (StringUtils.isNoneBlank(prepaidDetails.getPricingTierCode())
				&& !ConstantUtils.ALL.equalsIgnoreCase(prepaidDetails.getPricingTierCode())) {
			query.append(" and l.code = :pricingTierCode");
		}
		query.append(" group by pc.oid, l.description,l.code ");
	}
	
	/**
	 * Parameter conditions.
	 *
	 * @param prepaidDetails the prepaid details
	 * @param sqlQuery the sql query
	 */
	public void parameterConditions(PrepaidContractDTO prepaidDetails, Query sqlQuery) {
		if (StringUtils.isNoneBlank(prepaidDetails.getFein())) {
			sqlQuery.setParameter("fein", prepaidDetails.getFein());
		}
		if (StringUtils.isNoneBlank(prepaidDetails.getSupplierName())) {

			sqlQuery.setParameter("supplierName", "%" + prepaidDetails.getSupplierName() + "%");
		}
		if (null != prepaidDetails.getStartFromDt() && null != prepaidDetails.getStartToDt()) {

			sqlQuery.setParameter("startFromDate", prepaidDetails.getStartFromDt());
			sqlQuery.setParameter("startToDate", prepaidDetails.getStartToDt());
		}
		if (null != prepaidDetails.getEndFromDt() && null != prepaidDetails.getEndToDt()) {
			sqlQuery.setParameter("endFromDate", prepaidDetails.getEndFromDt());
			sqlQuery.setParameter("endToDate", prepaidDetails.getEndToDt());
		}

		if (StringUtils.isNoneBlank(prepaidDetails.getUpdatedBy())) {
			sqlQuery.setParameter("updatedBy", "%" + prepaidDetails.getUpdatedBy() + "%");
		}

		if (StringUtils.isNoneBlank(prepaidDetails.getPricingTierCode())
				&& !ConstantUtils.ALL.equalsIgnoreCase(prepaidDetails.getPricingTierCode())) {
			sqlQuery.setParameter("pricingTierCode", prepaidDetails.getPricingTierCode());
		}
	}

    /**
     * Contract details query
     * return StringBuilder
     */
    public StringBuilder buildPrepaidContractQuery() {
        return new StringBuilder()
                .append("select pc.oid,pc.contract_start_date as \"contractStartDate\" "
                        + " ,case when pc.contract_end_date < CURRENT_DATE then 'Expired' "
                        + " when pc.contract_start_date <= CURRENT_DATE and pc.contract_end_date >= CURRENT_DATE then 'Active' "
                        + " when pc.contract_start_date > CURRENT_DATE then 'Pending' end as \"contractStatus\" "
                        + ",pc.contract_end_date as \"contractEndDate\", pc.max_idn_count as \"maxIDNCount\",pc.max_user_count as \"maxUserCount\" "
                        + ",pc.current_idn_count as \"currentIDNCount\", pc.current_user_count as \"currentUserCount\" "
                        + ",pc.pricing_tier_code as \"pricingTierCode\", pc.notes as notes, pc.created_by as \"createdBy\", pc.created_on as \"createdOn\", "
                        + " pc.updated_on as \"updatedOn\", pc.updated_by as \"updatedBy\", pc.unlimited_reps as \"unlimitedReps\", pc.deleted "
                        + ",l.description as \"pricingTierName\" " + ",string_agg(pcs.fein,',') as \"feinsList\" "
                        + ", pc.oas_contract as \"oasContract\" "
                        + " from prepaid_contracts pc join vision.lookup l on pc.pricing_tier_code=l.code and pc.deleted=false "
                        + " join prepaid_contract_suppliers pcs on pc.oid = pcs.prepaid_contract_oid ");
    }

    /**
     * validate One Active Rep
     * @return StringBuilder
     */
    public StringBuilder validateOneActiveRep() {
        return new StringBuilder()
                .append("select a.oid as \"actorOid\",vd.vendor_oid as \"vendorOid\",vd.ein_or_ssn as \"fein\",vc.customer_oid as \"customerOid\" ,"
                        + " vd.legal_name as \"legalName\",c.company_name as \"providerName\" from user_vm u join actor a on a.user_oid=u.oid join vendor_rep vrep"
                        + " on a.oid=vrep.oid join vcrelation vc on vc.oid=vrep.vcrelation_oid join vendor_detail vd on vc.vendor_oid=vd.vendor_oid"
                        + " join customer c on c.oid=vc.customer_oid where u.user_status_code='ACT' and a.status_code='ACT'"
                        + " and vc.oid= :idnOid order by a.created_on desc limit 1");
    }

    /**
     * get VendorDetails By VendorOid
     * @return StringBuilder
     */
    public StringBuilder getVendorDetailsByVendorOid() {
        return new StringBuilder().append("select vd.legal_name as \"supplierName\",vd.ein_or_ssn as \"fein\",pc.max_user_count as \"maxUserCount\","
                + " case when pc.oid is not null then 'true' when pc.oid is null then 'false' end as \"prepaidSupplier\", pc.oid as prepaidContractOid, "
                + " CONCAT(l.category , ' - ', l.description) AS \"tierCode\" "
                + " from  vision.vendor_detail vd join lookup l on vd.tier_plan_code = l.code "
                + " left join vision.prepaid_contract_suppliers pcs on vd.ein_or_ssn = pcs.fein "
                + " left join prepaid_contracts pc on pcs.prepaid_contract_oid = pc.oid "
                + " and pc.contract_start_date <= CURRENT_DATE and pc.contract_end_date >= CURRENT_DATE and pc.deleted is false "
                + " where vd.vendor_oid = :vendorOid ");
    }

    /**
     * Common join query for Prepaid suppliers
     * @return StringBuilder
     */
    public StringBuilder joinQueryForPrepaidSuppliers() {
        return new StringBuilder(" left join vision.user_vm u on vd.vendor_oid = u.org_oid and u.user_status_code = 'ACT' ")
                .append(" left join user_detail ud on ud.user_oid = u.oid ")
                .append(" left join vision.actor a on u.oid = a.user_oid ")
                .append(" left join vision.vendor_rep vr on a.oid = vr.oid ").append(" left join vision.vcrelation vc on vr.vcrelation_oid = vc.oid ")
                .append(" and vc.express_registered is false and vc.customer_oid != 'vendormate' and vc.status_code in ('ACT','ACTCR') and vc.vendor_oid = vd.vendor_oid ");
    }

    /**
     * get activeUser For Prepaid
     * @return StringBuilder
     */
    public StringBuilder activeUserForPrepaid() {
        return new StringBuilder()
                .append(" join vision.user_detail ud on uv.oid = ud.user_oid AND uv.user_status_code = 'ACT' and ud.paid_user is true "
                        + " join vision.vendor_detail vd on uv.org_oid  = vd.vendor_oid "
                        + " join vision.vendor v on vd.vendor_oid = v.oid and v.vendor_status_code ='ACT' "
                        + " join vision.prepaid_contract_suppliers pcs on vd.ein_or_ssn = pcs.fein "
                        + " where pcs.prepaid_contract_oid=:prepaidContractOid");
    }

    /**
     * build paid user Query
     * @return StringBuilder
     */
    public StringBuilder buildPaidUserQuery() {
        return new StringBuilder().append("select count(distinct(uv.oid)) as current_user_count from user_vm uv "
                + " join vision.vendor_detail vd on uv.org_oid = vd.vendor_oid"
                + " left join user_detail ud on uv.oid = ud.user_oid and uv.user_status_code = 'ACT' left join actor ac on uv.oid = ac.user_oid"
                + " left join vendor_rep vr on ac.oid = vr.oid"
                + " left join vcrelation vc on vr.vcrelation_oid = vc.oid and vc.customer_oid not in ('vendormate') "
                + " and vc.status_code in ('ACT','ACTCR') and vc.express_registered is false  where uv.org_oid in ( :vendorOid) and (ud.paid_user is true)");
    }
    
    
    /**
     * build VendorDetails By VendorOid
     * @return StringBuilder
     */
    public StringBuilder buildVendorDetailsByVendorOid() {
        return new StringBuilder().append("select vd.legal_name as \"supplierName\",vd.ein_or_ssn as \"fein\", vd.tier_plan_code as \"tierCode\""
                + " from  vision.vendor_detail  vd"
                + " where vd.vendor_oid = :vendorOid ");
    }
    
    /**
     * UserOid is optional as only Existing Users will have it
     * @param selectedIdns
     * @param userOid
     * @return
     */
    public StringBuilder buildPaidUserAndIdnQuery(List<String> selectedIdns, String userOid, String currentVendorOid) {
        StringBuilder query = new StringBuilder().append(
                        "select count(distinct(uv.oid)) as current_user_count,count(distinct(vc.oid)) as vc_count from user_vm uv"
                                        + " join vision.vendor_detail vd on uv.org_oid = vd.vendor_oid"
                                        + " left join user_detail ud on uv.oid = ud.user_oid and uv.user_status_code = 'ACT'"
                                        + " left join actor ac on uv.oid = ac.user_oid" 
                                        + " left join vendor_rep vr on ac.oid = vr.oid"
                                        + " left join vcrelation vc on vr.vcrelation_oid = vc.oid and vc.customer_oid != 'vendormate'");
        if (CollectionUtils.isNotEmpty(selectedIdns)) {
                query.append(" and vc.oid not in (select oid from vision.vcrelation v where customer_oid in (:idns) ");
                if (StringUtils.isNotBlank(currentVendorOid)) {
                    query.append(" and vd.vendor_oid= :currentVendorOid) ");
                } else {
                    query.append(" ) ");
                }
        }               
        query.append(" and vc.status_code in ('ACT','ACTCR') and vc.express_registered is false where uv.org_oid in ( :vendorOid)").append(buildUserIdCondition(userOid));
        return query;
}

    private String buildUserIdCondition(String userOid) {
             String userOidCondition = " and (ud.paid_user is true)";
             if (StringUtils.isNotEmpty(userOid)) {
                     userOidCondition = " and (uv.oid = :userOid or ud.paid_user is true)";
             }
             return userOidCondition;
     }
    
    /**
     * get activeUser For supplier
     * @return StringBuilder
     */
    public StringBuilder activeUserForSupplier() {
        return new StringBuilder()
                .append(" join vision.user_detail ud on uv.oid = ud.user_oid AND uv.user_status_code = 'ACT' "
                        + " join vision.vendor_detail vd on uv.org_oid  = vd.vendor_oid "
                        + " join vision.vendor v on vd.vendor_oid = v.oid and v.vendor_status_code ='ACT' "
                        + " where vd.ein_or_ssn in (:fein)");
    }

    /**
     * build build Vendor Info Query - query will return vendor with atleast one act vc having relationship with user
     * @return StringBuilder
     */
    public StringBuilder buildVendorInfoQuery() {
        return new StringBuilder(
                "select vd.vendor_oid as \"vendorOid\", vd.ein_or_ssn as fein , vd.legal_name as \"legalName\" ,gp.oid as \"globalProfileOid\","
                        + " gp.deleted as \"deleted\",gp.expiration_date as \"expirationDate\", gp.grp_plan as \"grpPlan\", "
                        + " count(distinct u.oid) filter (where u.user_status_code = 'ACT') as \"activeUsers\",  "
                        + " count(distinct u.oid) filter (where u.user_status_code in('INACT','InACT')) as \"inactiveUsers\", "
                        + " count(distinct vc.oid) filter (where vc.status_code in ('ACT','ACTCR') and u.user_status_code = 'ACT' and vc.express_registered is false ) as \"activeVcs\" "
                        + " from vision.vendor_detail vd join vision.vcrelation vc on vd.vendor_oid = vc.vendor_oid "
                        + " join vision.vendor_rep vr on vr.vcrelation_oid = vc.oid join vision.actor a on  a.oid = vr.oid "
                        + " join vision.user_vm u on u.oid = a.user_oid  left join vision.global_profile_keys gpk  on gpk.key = vd.ein_or_ssn "
                        + " left join vision.global_profile gp on gp.oid = gpk.global_profile_oid "
                        + " where vd.ein_or_ssn = ? group by vd.vendor_oid ,vd.ein_or_ssn , vd.legal_name , gp.oid ,gp.deleted,gp.expiration_date,gp.grp_plan");
    }

    /**
     *
     * Get impacted reps message details query
     */
    public StringBuilder getTierRequestMessageDetails() {
        return new StringBuilder(
                "select uv.first_name as \"firstName\", uv.last_name as \"lastName\", uv.user_id as \"emailId\", string_agg(distinct(c.company_name), ',') as \"activeIDNSList\" from vision.vcrelation vc ").append(
                        " join vision.customer c on vc.customer_oid = c.oid and vc.status_code in ('ACT', 'ACTCR') and vc.express_registered is false and vc.customer_oid != 'vendormate' ")
                        .append(" join vision.vendor_rep vr on vc.oid = vr.vcrelation_oid join vision.actor a on a.oid = vr.oid ")
                        .append(" join vision.user_vm uv on uv.oid = a.user_oid and uv.user_status_code = 'ACT' join vision.user_detail ud on uv.oid = ud.user_oid and ud.paid_user is true ")
                        .append(" where vc.customer_oid in (?1) and vc.vendor_oid = ?2 group by uv.first_name, uv.last_name, uv.user_id ");
    }

    /**
     * fetch Associated Grp Feins Query
     * @return StringBuilder
     */
    public StringBuilder fetchAssociatedGrpFeinsQuery() {
        return new StringBuilder("select gpk.key from global_profile_keys gpk where gpk.global_profile_oid = ( ")
                .append(" select gp.oid from global_profile_keys gpk join global_profile gp on gp.oid = gpk.global_profile_oid ")
                .append(" where gpk.key = ? and (gp.expiration_date is null or gp.expiration_date >= cast(now() as date)) and gp.deleted is false )");
    }

    /**
     * fetch Users Count Query
     * @return
     */
    public StringBuilder fetchUsersCountQuery() {
        return new StringBuilder(
                " select count(distinct ud.user_oid) filter (where ud.paid_user = true) as \"paidUser\", count(distinct ud.user_oid) filter (where ud.paid_user = false) as \"unpaidUser\" , vd.ein_or_ssn as fein ")
                        .append(" from vision.vendor_detail vd left join vision.user_vm u on vd.vendor_oid = u.org_oid left join vision.user_detail ud on u.oid = ud.user_oid where vd.enable_program_change is true and u.user_status_code = 'ACT'")
                        .append(" and vd.ein_or_ssn in (:fein ) and ud.created_by = 'Supplier Migration Job' group by vd.ein_or_ssn");
    }
    
    public StringBuilder getIDNCount(boolean isPaid) {
    	StringBuilder query = new StringBuilder(
				"select count(distinct vc.customer_oid) as idncount from vision.user_vm uv join vision.vendor_detail vd on uv.org_oid = vd.vendor_oid ")
						.append("join vision.user_detail ud on uv.oid = ud.user_oid and uv.user_status_code = 'ACT' join vision.actor ac on uv.oid = ac.user_oid  ")
						.append("join vision.vendor_rep vr on ac.oid = vr.oid join vision.vcrelation vc on vr.vcrelation_oid = vc.oid and vc.customer_oid != 'vendormate' ")
						.append("and vc.status_code in ('ACT','ACTCR') and vc.express_registered is false where vd.vendor_oid = (:vendorOid) ");
    	if(BooleanUtils.isTrue(isPaid)) {
    		query.append("and ud.paid_user is true");
    	}
    	return query;
    }

    /**
     * fetch User Details
     * @return
     */
    public StringBuilder fetchUserDetails() {
        StringBuilder query = new StringBuilder();
        query.append("select uv.first_name as \"firstName\", uv.last_name as \"lastName\",uv.user_id as \"userId\", uv.oid as \"userOid\",ud.paid_user as \"paidUser\", vd.ein_or_ssn as fein,vd.legal_name as \"vendorName\" ")
                .append("from user_vm uv join user_detail ud on ud.user_oid = uv.oid join vendor_detail vd on vd.vendor_oid = uv.org_oid ")
                .append("where uv.user_id in :userId and uv.user_id not like '\\__del%' and uv.user_id not like '\\_DEL' ");
        return query;
    }
    
}