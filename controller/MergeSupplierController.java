package com.ghx.api.operations.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.MergeSupplierRequest;
import com.ghx.api.operations.repository.MergeSupplierRepository;
import com.ghx.api.operations.service.MergeSupplierService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;


/**
 * The Class MergeSupplierController.
 * @author Sundari V
 */
@RestController
@Validated
@RequestMapping("/v1/suppliers/mergerequest")
public class MergeSupplierController {
	
	private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(MergeSupplierController.class);

    @Autowired
    private transient MergeSupplierService mergeSupplierService;
    
	@Autowired
	private transient MergeSupplierRepository mergeSupplierRepository;

	@Autowired
	private transient ResourceLoader resourceLoader;
	
	private final transient ModelMapper modelMapper = new ModelMapper();

	/**
	 * Delete controller for Merge Supplier Request
	 * @param id
	 * @return
	 */
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<HttpStatus> delete(@PathVariable String id) {
		mergeSupplierService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

    /**
     * This API will fetch all non deleted merge supplier requests
     *
     * @param salesforceId
     * @param deletedSupplierFein
     * @param deletedSupplierLegalName
     * @param retainedSupplierFein
     * @param retainedSupplierLegalName
     * @param status
     * @param submittedDateFrom
     * @param submittedDateTo
     * @param submittedBy
     * @param pageable
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMergeRequests(@RequestParam(required = false) String salesforceId,
            @RequestParam(required = false) String status, @RequestParam(required = false) String submittedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateTo,
            @RequestParam(required = false) String deletedSupplierFein, @RequestParam(required = false) String deletedSupplierLegalName,
            @RequestParam(required = false) String retainedSupplierFein, @RequestParam(required = false) String retainedSupplierLegalName,
            Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.builder().salesforceId(salesforceId)
                .status(status).submittedBy(submittedBy).submittedDateFrom(submittedDateFrom).submittedDateTo(submittedDateTo)
                .deletedSupplierFein(deletedSupplierFein).deletedSupplierLegalName(deletedSupplierLegalName)
                .retainedSupplierFein(retainedSupplierFein).retainedSupplierLegalName(retainedSupplierLegalName).build();

        return new ResponseEntity<>(mergeSupplierService.getAllMergeRequests(searchRequest, pageable), HttpStatus.OK);
    }

    /**
     * This API will export all non deleted merge supplier requests
     * 
     * @param salesforceId
     * @param status
     * @param submittedBy
     * @param submittedDateFrom
     * @param submittedDateTo
     * @param deletedSupplierFein
     * @param deletedSupplierLegalName
     * @param retainedSupplierFein
     * @param retainedSupplierLegalName
     * @param pageable
     * @param exportType
     * @param response
     * @throws IOException
     *
     */
    @GetMapping(path = "/export/{exportType}")
    public void exportAllMergeRequests(@RequestParam(required = false) String salesforceId,
            @RequestParam(required = false) String status, @RequestParam(required = false) String submittedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateTo,
            @RequestParam(required = false) String deletedSupplierFein, @RequestParam(required = false) String deletedSupplierLegalName,
            @RequestParam(required = false) String retainedSupplierFein, @RequestParam(required = false) String retainedSupplierLegalName,
            Pageable pageable, @PathVariable String exportType ,HttpServletResponse response) throws IOException {
        SearchRequest searchRequest = SearchRequest.builder().salesforceId(salesforceId).status(status).submittedBy(submittedBy)
                .submittedDateFrom(submittedDateFrom).submittedDateTo(submittedDateTo).deletedSupplierFein(deletedSupplierFein)
                .deletedSupplierLegalName(deletedSupplierLegalName).retainedSupplierFein(retainedSupplierFein)
                .retainedSupplierLegalName(retainedSupplierLegalName).pageable(pageable).build();
        mergeSupplierService.exportMergeSupplierRequests(searchRequest, resourceLoader, exportType, response);
    }

    /**
     * This API will create a new merge supplier request
     *
     * @param mergeSupplierRequestDTO
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<HttpStatus> createMergeSupplierRequest(@RequestBody MergeSupplierRequestDTO mergeSupplierRequestDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ConstantUtils.MERGE_REQUEST_ID, mergeSupplierService.createMergeSupplierRequest(mergeSupplierRequestDTO));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
    
    /**
     * export merge details
     * @param id
     * @param exportType
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/{id}/export/{exportType}")
    public void exportMergeSupplier(@PathVariable String id, @PathVariable String exportType, HttpServletResponse response) throws IOException {
        LOGGER.info("Merge Supplier Request export started {}");
        Optional<MergeSupplierRequest> optMergeSupplierRequest = mergeSupplierRepository.findById(id);
        if (optMergeSupplierRequest.isPresent()) {
            MergeSupplierRequestDTO mergeSupplierRequestDTO = modelMapper.map(optMergeSupplierRequest.get(), MergeSupplierRequestDTO.class);
            List<MergeSupplierRequestDTO> mergeSupplierRequestList = new ArrayList<>();
            mergeSupplierRequestList.add(mergeSupplierRequestDTO);
            ReportUtils.generateMergeAndMoveReport(exportType, mergeSupplierRequestList, response, this.resourceLoader,
                    ConstantUtils.SUPPLIER_MERGEREQUEST_FILE, null);
            LOGGER.info("Merge Supplier Request exported successfully {}", id);
        } else {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUESTID_NOT_FOUND));
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUESTID_NOT_FOUND));
        }

    }
}
