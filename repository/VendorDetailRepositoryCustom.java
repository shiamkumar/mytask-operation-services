package com.ghx.api.operations.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.dto.ProviderDetailsDTO;
import com.ghx.api.operations.dto.SupplierDetailsDTO;

/**
 * 
 * @author Ajith
 *
 */
public interface VendorDetailRepositoryCustom {
    /**
     * 
     * @param vendorOid
     * @return SupplierDetailsDTO
     */
    SupplierDetailsDTO getVendorDetailsByVendorOid(String vendorOid);
    
    /**
     * 
     * @param feins
     * @return
     */
    List<Map<String, Object>> getPrepaidContractVendorDetails(List<String> feins);
    
    
    /**
     * Get User and IDN count Details by Fein
     * @param feins
     * @return
     */
    Map<String, Object> getUserAndIdnCountByFein(List<String> feins);

    /**
     *
     * @param prepaidOid
     * @return
     */
    int activeUserCountForPrepaid(String prepaidOid);

    /**
     *
     * @param prepaidContractOid
     * @return
     */
    int getPaidUserCount(String vendorOid);
    
    /**
     * get vendor Details by feins
     * @param feins
     * @return
     */
    List<PrepaidContractSupplierDTO> getVendorDetailsByFein(List<String> feins);
    
    /**
     * update vendor detail table pricing tier code
     * @param feins
     * @param updatedBy
     * @param pricingTierCode
     */
    int updateVendorPricingTier(List<String> feins, String updatedBy, String pricingTierCode);

    /**
     * get vendor Details by oid
     * @param oid
     * @return
     */
    Map<String, Object> getDetailsByVendorOid(String oid);

    /** 
     * @param vendorOid
     * @return
     */
    SupplierDetailsDTO getVendorDetailsByOid(String vendorOid);

    /**
     * 
     * @param vendorOids
     * @param selectedIdns
     * @param userOid
     * @param currentVendorOid
     * @return
     */
    Map<String, Object> getPaidUserAndIdnCount(List<String> vendorOids, List<String> selectedIdns, String userOid, String currentVendorOid);

    /**
     * Get IDN details
     * @param eliminatedIdns list
     * @param pageable
     * @return
     */
    List<ProviderDetailsDTO> getIdnsDetails(List<String> eliminatedIdns, Pageable pageable);
    
    /**
     * Get vendoroid by feins
     * @param feins
     * @return
     */
    List<String> getVendorByFein(List<String> feins);
    
    /**
    * To get the active users for prepaid suppliers
    * @param feins
    * @return
    */
    List<String> activeUsersForPrepaid(List<String> feins);

    /**
     * 
     * @param fein
     * @return
     */
    Map<String, Object> getVendorDetailsByFein(String fein);

    /**
     * 
     * @param fein
     * @return
     */
    List<Map<String, Object>> fetchVendorInfo(String fein);

    /**
     * 
     * @param fein
     * @return
     */
    int fetchMergeSupplierRequest(String fein);

    /**
     * 
     * @param fein
     * @param paymentProfileGraceDate
     * @return
     */
    int getRfpmtCount(String fein, Timestamp paymentProfileGraceDate);

    /**
     * 
     * @param fein
     * @return
     */
    int checkGRPForFein(String fein);

    /**
     * 
     * @param fein
     * @return
     */
    int checkFeinHasNewPrepaidContract(String fein);

    /**
     * 
     * @param fein
     * @return
     */
    int getAllRfpmtCount(String fein);

    /**
     *
     * @param fein
     * @return
     */
    List<String> fetchAssociatedGrpFeins(String fein);

    /**
     * 
     * @param totalFeins
     * @return
     */
    List<Map<String, Object>> fetchUsersCount(List<String> totalFeins);

    /**
     * 
     * @param fein
     * @return inactive vc count
     */
    int getInactiveVcCount(String fein);

}
