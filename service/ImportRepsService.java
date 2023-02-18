package com.ghx.api.operations.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.ImportRepsDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierDetailsDTO;

/**
 * 
 * @author Ajith
 *
 */
public interface ImportRepsService {

    /**
     * 
     * @param id
     * @param importRepsDTO
     * @return Map<String, Object>
     * 
     */
    SupplierDetailsDTO importReps(String idnOid, ImportRepsDTO importRepsDTO);

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    Map<String, Object> getImportRepRequests(SearchRequest searchRequest, Pageable pageable);

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    Map<String, Object> getImportRequestUserDetails(SearchRequest searchRequest, Pageable pageable);
}
