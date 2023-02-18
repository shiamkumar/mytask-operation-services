package com.ghx.api.operations.controller;

import static com.ghx.api.operations.util.ConstantUtils.TIER_MANAGEMENT_REQUEST_EXPORT_FILE;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.IdnsResponseDTO;
import com.ghx.api.operations.dto.TierChangeRequesFetchAllResponseDTO;
import com.ghx.api.operations.dto.TierChangeRequestDTO;
import com.ghx.api.operations.dto.TierChangeRequestDetailsDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;
import com.ghx.api.operations.dto.TierChangeRequestUpdateDTO;
import com.ghx.api.operations.dto.TierChangeRequestValidationResponseDTO;
import com.ghx.api.operations.enums.TierChangeRequestStatus;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.service.TierChangeRequestService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ReportUtils;


/**
 * The Class TierChangeRequestController.
 * @category controller
 * @author Krishnan M
 */
@RestController
@Validated
@RequestMapping("/v1/suppliers/tierchangerequest")
public class TierChangeRequestController {

    /** Tier Change request service */
    @Autowired
    private transient TierChangeRequestService tierChangeRequestService;
    
    /** Resource Loader Instance*/
    @Autowired
    private transient ResourceLoader resourceLoader;
    
    /** Report Util Instance */
    @Autowired
    private transient ReportUtils reportUtils;

    /**
     * This API will create a new Tier change request
     *
     * @param TierChangeRequestDTO
     * @return the response entity
     */
    @PostMapping
    @LogExecutionTime
    public ResponseEntity<HttpStatus> create(@RequestBody TierChangeRequestDTO tierChangeRequestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ConstantUtils.TIER_CHANGE_REQUEST_ID, tierChangeRequestService.create(tierChangeRequestDTO));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    /**
     * Get tier change request details by Id
     * @param id
     * @return
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<TierChangeRequestDetailsDTO> getTierChangeRequestById(@PathVariable String id) {
        return new ResponseEntity<TierChangeRequestDetailsDTO>(tierChangeRequestService.getTierChangeRequestById(id), HttpStatus.OK);
    }

    /**
     * Delete controller for Tier change Request
     * @param id
     * @return
     */
    @LogExecutionTime
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String id) {
        tierChangeRequestService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update a Tier Change Request status along with notes
     * @param id
     * @param updateDTO
     * @return
     */
    @PatchMapping(path = "/{id}")
    public ResponseEntity<TierChangeRequestDTO> updateTierChangeRequest(@PathVariable String id, @Valid @RequestBody TierChangeRequestUpdateDTO updateDTO) {
        return new ResponseEntity<TierChangeRequestDTO>(tierChangeRequestService.updateById(id, updateDTO), HttpStatus.OK);
    }

    /**
     * Validates the tier change request
     * @param id
     * @return
     */
    @GetMapping(path = "/{id}/validate")
    public ResponseEntity<TierChangeRequestValidationResponseDTO> validateTierChangeRequest(@PathVariable String id) {
        return new ResponseEntity<TierChangeRequestValidationResponseDTO>(tierChangeRequestService.validateTierChangeRequest(id), HttpStatus.OK);
    }

    /**
     * Get list of tier change requests
     * @param status
     * @param fein
     * @param legalName
     * @param currentTierCode
     * @param requestedTierCode
     * @param requestedBy
     * @param requestedOnFromDate
     * @param requestedOnToDate
     * @param reviewedBy
     * @param reviewedOnFromDate
     * @param reviewedOnToDate
     * @param processedOnFromDate
     * @param processedOnToDate
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<TierChangeRequesFetchAllResponseDTO> getAllTierChangeRequest(@RequestParam(required = false) String status,
            @RequestParam(required = false) String fein, @RequestParam(required = false) String legalName,
            @RequestParam(required = false) String currentTierCode, @RequestParam(required = false) String requestedTierCode,
            @RequestParam(required = false) String requestedBy, @RequestParam(required = false) String reviewedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedOnToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date reviewedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date reviewedOnToDate, Pageable pageable) {
        TierChangeRequestSearchDTO searchRequest = TierChangeRequestSearchDTO.builder()
                .status(StringUtils.isNotBlank(status) ? TierChangeRequestStatus.valueOf(status) : null).fein(fein).legalName(legalName)
                .currentTierCode(currentTierCode).requestedTierCode(requestedTierCode).requestedBy(requestedBy).reviewedBy(reviewedBy)
                .requestedOnFromDate(requestedOnFromDate).requestedOnToDate(requestedOnToDate).processedOnFromDate(processedOnFromDate)
                .processedOnToDate(processedOnToDate).reviewedOnFromDate(reviewedOnFromDate).reviewedOnToDate(reviewedOnToDate).build();
        return new ResponseEntity<>(tierChangeRequestService.getAllTierChangeRequest(searchRequest, pageable, false), HttpStatus.OK);
    }

    /**
     * Get eliminated idns for tier change request
     * @param id
     * @param pageable
     * @return
     */
    @GetMapping(path = "/{id}/eliminatedidns")
    public ResponseEntity<IdnsResponseDTO> getEliminatedIdns(@PathVariable String id, Pageable pageable) {
        return new ResponseEntity<IdnsResponseDTO>(tierChangeRequestService.getEliminatedIdns(id, pageable), HttpStatus.OK);
    }
    

    /**
     * Export Tier Change Requests 
     * @param exportType
     * @param response
     * @param status
     * @param fein
     * @param legalName
     * @param currentTierCode
     * @param requestedTierCode
     * @param requestedBy
     * @param reviewedBy
     * @param requestedOnFromDate
     * @param requestedOnToDate
     * @param processedOnFromDate
     * @param processedOnToDate
     * @param reviewedOnFromDate
     * @param reviewedOnToDate
     * @param pageable
     * @throws IOException
     */
    @GetMapping(path = "/export/{exportType}")
    public void exportTierChangeRequests(@PathVariable String exportType, HttpServletResponse response, @RequestParam(required = false) String status,
            @RequestParam(required = false) String fein, @RequestParam(required = false) String legalName,
            @RequestParam(required = false) String currentTierCode, @RequestParam(required = false) String requestedTierCode,
            @RequestParam(required = false) String requestedBy, @RequestParam(required = false) String reviewedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedOnToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedOnToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date reviewedOnFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date reviewedOnToDate, Pageable pageable)
            throws IOException {
        TierChangeRequestSearchDTO searchRequest = TierChangeRequestSearchDTO.builder()
                .status(StringUtils.isNotBlank(status) ? TierChangeRequestStatus.valueOf(status) : null).fein(fein).legalName(legalName)
                .currentTierCode(currentTierCode).requestedTierCode(requestedTierCode).requestedBy(requestedBy).reviewedBy(reviewedBy)
                .requestedOnFromDate(requestedOnFromDate).requestedOnToDate(requestedOnToDate).processedOnFromDate(processedOnFromDate)
                .processedOnToDate(processedOnToDate).reviewedOnFromDate(reviewedOnFromDate).reviewedOnToDate(reviewedOnToDate).build();
        TierChangeRequesFetchAllResponseDTO fetchAllResponse = tierChangeRequestService.getAllTierChangeRequest(searchRequest, pageable, true);
        reportUtils.generateTierChangeRequestsReport(exportType, fetchAllResponse.getRequestList(), response, this.resourceLoader,
                TIER_MANAGEMENT_REQUEST_EXPORT_FILE, null);
    }
}
