package com.ghx.api.operations.controller;

import java.util.Date;
import java.util.Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.MoveUserRequest;
import com.ghx.api.operations.repository.MoveUserRepository;
import com.ghx.api.operations.service.MoveUserService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.validation.business.MoveUserBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * This class MoveUserRequestController
 * 
 * @author Ananth Kandasamy
 *
 */

@RestController
@Validated
@RequestMapping("/v1/users")
public class MoveUserRequestController {

    /** The Constant LOGGER. */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(MoveUserRequestController.class);

    /** initialize move user service */
    @Autowired
    private transient MoveUserService moveUserService;

    /** initalize MoveUserRepository */
    @Autowired
    private transient MoveUserRepository moveUserRepository;

    /** initalize ModelMapper */
    private final transient ModelMapper modelMapper = new ModelMapper();

    /** initalize ResourceLoader */
    @Autowired
    private transient ResourceLoader resourceLoader;

    /** initialize MoveUserBusinessValidator */
    @Autowired
    private MoveUserBusinessValidator moveUserBuisnessValidator;

    /**
     * This API will fetch all non deleted merge supplier requests
     *
     * @param salesforceId
     * @param emailId
     * @param updatedEmailId
     * @param name
     * @param sourceSupplierFein
     * @param sourceSupplierLegalName
     * @param destinationSupplierFein
     * @param destinationSupplierLegalName
     * @param status
     * @param submittedDateFrom
     * @param submittedDateTo
     * @param submittedBy
     * @param pageable
     * @return the response entity
     */
    @GetMapping(path = "/move")
    public ResponseEntity<Map<String, Object>> getAllMoveRequests(@RequestParam(required = false) String salesforceId,
            @RequestParam(required = false) String status, @RequestParam(required = false) String submittedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateTo,
            @RequestParam(required = false) String sourceSupplierFein, @RequestParam(required = false) String sourceSupplierLegalName,
            @RequestParam(required = false) String destinationSupplierFein, @RequestParam(required = false) String destinationSupplierLegalName,
            @RequestParam(required = false) String emailId, @RequestParam(required = false) String updatedEmailId,
            @RequestParam(required = false) String name, Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.builder().salesforceId(salesforceId).status(status).submittedBy(submittedBy)
                .submittedDateFrom(submittedDateFrom).submittedDateTo(submittedDateTo).sourceSupplierFein(sourceSupplierFein)
                .sourceSupplierLegalName(sourceSupplierLegalName).emailId(emailId).updatedEmailId(updatedEmailId)
                .destinationSupplierFein(destinationSupplierFein).name(name).destinationSupplierLegalName(destinationSupplierLegalName).build();
        return new ResponseEntity<>(moveUserService.getAllMoveRequests(searchRequest, pageable), HttpStatus.OK);
    }
	

    /**
     * create new move request
     * 
     * @param id
     * @param moveUserRequestDTO
     * @return
     */
    @PostMapping(path = "/{id}/moverequest")
    public ResponseEntity<HttpStatus> createMoveUserRequest(@PathVariable String id, @Valid @RequestBody MoveUserRequestDTO moveUserRequestDTO) {
        LOGGER.info("Move user Request creation started {}");
        HttpHeaders headers = new HttpHeaders();
        headers.add(ConstantUtils.MERGE_REQUEST_ID, moveUserService.createMoveUserRequest(id, moveUserRequestDTO));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * export move user request in csv format
     * @param requestId
     * @param exportType
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @GetMapping(path = "/move/{requestId}/export/{exportType}")
    public void exportMoveUser(@PathVariable String requestId, @PathVariable String exportType, HttpServletResponse response) throws IOException {
        LOGGER.info("Merge Supplier Request export started {}");
        Optional<MoveUserRequest> optMoveUserRequest = moveUserRepository.findById(requestId);
        if (optMoveUserRequest.isPresent()) {
            MoveUserRequestDTO moveUserRequestDTO = modelMapper.map(optMoveUserRequest.get(), MoveUserRequestDTO.class);
            moveUserBuisnessValidator.populateExportMoveDetails(moveUserRequestDTO);
            List<MoveUserRequestDTO> moveUserRequestList = new ArrayList<>();
            moveUserRequestList.add(moveUserRequestDTO);
            ReportUtils.generateMergeAndMoveReport(exportType, moveUserRequestList, response, this.resourceLoader,
                    ConstantUtils.MOVEUSER_REQUEST_FILE, null);
            LOGGER.info("Move User Request exported successfully {} ", requestId);
        } else {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.MOVE_REQUESTID_NOT_FOUND));
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.MOVE_REQUESTID_NOT_FOUND));
        }
    }
    
    /**
     * Export Move User requests, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param response
     * @param salesforceId
     * @param status
     * @param submittedBy
     * @param submittedDateFrom
     * @param submittedDateTo
     * @param sourceSupplierFein
     * @param sourceSupplierLegalName
     * @param destinationSupplierFein
     * @param destinationSupplierLegalName
     * @param emailId
     * @param updatedEmailId
     * @param name
     * @param pageable
     * @throws IOException
     */
    @GetMapping(path = "/move/export/{exportType}")
    public void exportMoveUserRequests(@PathVariable String exportType, HttpServletResponse response,
            @RequestParam(required = false) String salesforceId, @RequestParam(required = false) String status,
            @RequestParam(required = false) String submittedBy,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date submittedDateTo,
            @RequestParam(required = false) String sourceSupplierFein, @RequestParam(required = false) String sourceSupplierLegalName,
            @RequestParam(required = false) String destinationSupplierFein, @RequestParam(required = false) String destinationSupplierLegalName,
            @RequestParam(required = false) String emailId, @RequestParam(required = false) String updatedEmailId,
            @RequestParam(required = false) String name, Pageable pageable) throws IOException {
        SearchRequest searchRequest = SearchRequest.builder().salesforceId(salesforceId).status(status).submittedBy(submittedBy)
                .submittedDateFrom(submittedDateFrom).submittedDateTo(submittedDateTo).sourceSupplierFein(sourceSupplierFein)
                .sourceSupplierLegalName(sourceSupplierLegalName).emailId(emailId).updatedEmailId(updatedEmailId)
                .destinationSupplierFein(destinationSupplierFein).name(name).destinationSupplierLegalName(destinationSupplierLegalName)
                .pageable(pageable).build();
        moveUserService.exportMoveUserRequests(exportType, searchRequest, response);
    }
}
