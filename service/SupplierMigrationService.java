package com.ghx.api.operations.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.PricingMigrationDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierStatisticsDTO;

/**
 * SupplierMigrationService interface holds all the migration request, statistics declaration
 * @author Ajith
 *
 */
public interface SupplierMigrationService {

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    Map<String, Object> getMigrationRequests(SearchRequest searchRequest, Pageable pageable);

    /**
     * 
     * @param pricingMigrationRequestDTO
     * @return
     */
    Map<String, Object> saveMigrationRequest(PricingMigrationDTO pricingMigrationRequestDTO);
    
    /**
     * Fetch supplier migration statistics
     * @return
     */
    SupplierStatisticsDTO getSupplierMigrationStatistics();

}
