package com.ghx.api.operations.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.PricingMigrationDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierStatisticsDTO;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.service.SupplierMigrationService;
import com.ghx.api.operations.util.ConstantUtils;

/**
*
* @author Ajith
*
*/
@RestController
@Validated
@RequestMapping("/v1/suppliers")
public class SupplierMigrationController {
    
    
    /** SupplierMigrationService instance */
    @Autowired
    private transient SupplierMigrationService supplierMigrationService;

    /**
     * 
     * @param fein
     * @param legalName
     * @param currentPlan
     * @param status
     * @param pricingPlan
     * @param pageable
     * @return
     */
    @GetMapping(path = "/migrationrequest")
    @LogExecutionTime
    public ResponseEntity<Map<String, Object>> getMigrationRequests(@RequestParam(required = false) String fein, @RequestParam(required = false) String legalName,
            @RequestParam(required = false) String currentPlan, @RequestParam String status, @RequestParam(required = false) String pricingPlanCode,
            Pageable pageable, @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnToDate) {
        SearchRequest searchRequest = SearchRequest.builder().legalName(legalName).fein(fein).status(status).currentPlan(currentPlan)
                .pricingPlanCode(pricingPlanCode).processedOnFromDate(processedOnFromDate).processedOnToDate(processedOnToDate).build();
        return new ResponseEntity<>(supplierMigrationService.getMigrationRequests(searchRequest, pageable), HttpStatus.OK);
    }

    /**
     * 
     * @param pricingMigrationRequestDTO
     * @return
     */
    @PostMapping(path = "/migrationrequest")
    @LogExecutionTime
    public ResponseEntity<Map<String, Object>> saveMigrationRequest(@RequestBody PricingMigrationDTO pricingMigrationRequestDTO) {
        return new ResponseEntity<>(supplierMigrationService.saveMigrationRequest(pricingMigrationRequestDTO), HttpStatus.CREATED);
    }
    
    /**
     * Fetch supplier migration statistics
     * @return
     */
    @GetMapping(path = "/migration/statistics")
    public ResponseEntity<SupplierStatisticsDTO> getSupplierMigrationStatistics() {
        return new ResponseEntity<>(supplierMigrationService.getSupplierMigrationStatistics(), HttpStatus.OK);
    }

}
