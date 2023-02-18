package com.ghx.api.operations.repository;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.SearchRequest;

/**
 * 
 * @author Ajith
 *
 */
public interface PricingMigrationRequestRepositoryCustom {
    
    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    Map<String, Object> getMigrationRequests(SearchRequest searchRequest, Pageable pageable);

}
