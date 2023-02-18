package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.ImportRepRequestDTO;
import com.ghx.api.operations.dto.RepDetailsDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * The interface ImportRepRequestRepositoryCustom
 * @author Krishnan
 *
 */
public interface ImportRepRequestRepositoryCustom {

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    List<ImportRepRequestDTO> findAllUploadHistory(SearchRequest searchRequest, Pageable pageable);

    /**
     * 
     * @param searchRequest
     * @return
     */
    long findUploadImportRequestCount(SearchRequest searchRequest);

    /**
     * 
     * @param searchRequest
     * @return
     */
    long findImportRequestUserCount(SearchRequest searchRequest);

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    List<RepDetailsDTO> findAllImportRequestUserDetails(SearchRequest searchRequest, Pageable pageable);

}
