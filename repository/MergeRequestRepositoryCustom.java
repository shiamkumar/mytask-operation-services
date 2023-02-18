package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * @author Sundari V
 */
public interface MergeRequestRepositoryCustom {

    /**
     * find all merge requests
     * @param searchRequest
     * @param pageable
     * @return
     */
    List<MergeSupplierRequestDTO> findAllMergeRequests(SearchRequest searchRequest, Pageable pageable);

    /**
     * find count of merge requests
     * @param searchRequest
     * @return
     */
    long findMergeRequestsCount(SearchRequest searchRequest);
}
