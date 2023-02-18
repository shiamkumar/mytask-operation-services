package com.ghx.api.operations.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.PrepaidContractDTO;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;

/**
 * The Interface PrepaidContractRepositoryCustom
 */
public interface PrepaidContractRepositoryCustom {

    List<Map<String, Object>> searchVendorByFein(String fein);

    PrepaidContractDTO findDetailsByOid(String oid);

    PrepaidContractDTO getActualUserAndIDNCountByFein(String fein);

    PrepaidContractDTO findDetailsByFein(String fein);

    /**
     * Fetch prepaid grid contracts.
     *
     * @param prepaidDetails
     *            the prepaid details
     * @param pageable
     *            the pageable
     * @return the list
     */
    List<PrepaidContractDTO> findAllPrepaidContracts(PrepaidContractDTO prepaidDetails, Pageable pageable, String requestType);

    /**
     * Fetch prepaid contract count.
     *
     * @param prepaidDetails
     *            the prepaid details
     * @return the long
     */
    String findPrepaidContractCount(PrepaidContractDTO prepaidDetails);

    String getLookupNameByCode(String code);

    /**
     * find prepaid oid by fein
     * @param feinList
     * @return
     */
    List<Map<String, Object>> findPrepadiOidByFein(List<String> feinList);

    /**
     * Fetch list of associated feins for prepaid contract
     *
     * @param prepaidContractOid
     * @return list
     */
    List<String> getPrepaidFeins(String prepaidContractOid);

    /**
     * Fetch list of supplier details for prepaid contract
     * @param feins
     * @param oid
     * @param pageable
     * @return list
     */
    List<PrepaidContractSupplierDTO> fetchSupplierDetails(List<String> feins, String oid, Pageable pageable);
    
    /**
     * Returns number of users marked as unpaid
     *
     * @param feins
     * @param updatedBy
     * @return int
     */
    int markUsersUnpaid(List<String> feins, String updatedBy);

    /**
     * Returns number of users marked as paid
     *
     * @param feins
     * @param updatedBy
     * @param paidTierPlanCode
     * @return int
     */
    int markUsersPaid(List<String> feins, String updatedBy, String paidTierPlanCode);
}
