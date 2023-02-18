package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 */
public interface MergeSupplierService {

    /**
     * Saves the given merge Supplier request Details
     * @param mergeSupplierRequest
     * @return
     */
    String createMergeSupplierRequest(MergeSupplierRequestDTO mergeSupplierRequest);
    
    /**
     * Mark the Merge Supplier Request as Deleted
     * @param mergeRequestId
     */
    void delete(String mergeRequestId);
    
    /**
     * Returns the List of Merge Supplier Requests matching the Search Criteria
     * @param searchRequest
     * @param pageable
     * @return
     */
    Map<String, Object> getAllMergeRequests(SearchRequest searchRequest, Pageable pageable);

    /**
     * 
     * @param searchRequest
     * @param resourceLoader
     * @param exportType
     * @param response
     * @throws IOException
     */
    void exportMergeSupplierRequests(SearchRequest searchRequest, ResourceLoader resourceLoader, String exportType, HttpServletResponse response)
            throws IOException;

}
