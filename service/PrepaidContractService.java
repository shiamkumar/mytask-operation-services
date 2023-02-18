package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.PrepaidContractDTO;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * The Interface PrepaidContractService.
 */
public interface PrepaidContractService {

    /**
     * Fetch prepaid grid contracts.
     *
     * @param prepaidDetails
     *            the prepaid details
     * @param pageable
     *            the pageable
     * @return the map
     */
    Map<String, Object> fetchPrepaidGridContracts(PrepaidContractDTO prepaidDetails, Pageable pageable, String requestType);

    PrepaidContractDTO getById(String id);

    PrepaidContractDTO save(PrepaidContractDTO prepaidContractConfigDTO);

    PrepaidContractDTO update(String id, PrepaidContractDTO prepaidContractConfigDTO);

    /**
     * get by fein
     * @param fein
     * @return
     */
    PrepaidContractDTO getByFein(String fein);

    /**
     * delete contract and mark users unpaid
     * @param id
     * @return the DTO
     */
    PrepaidContractDTO delete(String id);

    /**
     * search suppliers
     * @param searchRequest
     * @return
     */
    Map<String, Object> searchSuppliers(SearchRequest searchRequest);

    /**
     * Fetch all suppliers associated with a prepaid contract
     * @param id
     * @param pageable
     * @return the response entity
     */
    Map<String, Object> getSupplierDetails(String id, Pageable pageable);
    
    /**
     * 
     * @param oid
     * @param pageable
     * @return
     */
    Page<PrepaidContractDTO> getAuditsByPrepaidContractId(String oid, Pageable pageable);
    
    /**
     * mark users unpaid for expired prepaid contract
     * @param id
     * @return DTO
     */
    PrepaidContractDTO updateExpiredContract(String id);
    
    /**
     * Add Suppliers to a Prepaid Contract
     * @param suppliers
     * @param id
     * @return
     */
    PrepaidContractDTO addPrepaidContractSuppliers(List<PrepaidContractSupplierDTO> suppliers, String id);
    
    /**
     * 
     * @param suppliers
     * @param id
     */
    PrepaidContractDTO deletePrepaidContractSuppliers(List<PrepaidContractSupplierDTO> suppliers, String id);

    /**
     *
     * @param prepaidDetails
     * @param resourceLoader
     * @param filename
     * @param pageable
     * @param response
     * @param exportType
     * @throws IOException
     */
    void exportPrepaidContracts(PrepaidContractDTO prepaidDetails, ResourceLoader resourceLoader, String filename, Pageable pageable,
            String exportType, HttpServletResponse response) throws IOException;

}
