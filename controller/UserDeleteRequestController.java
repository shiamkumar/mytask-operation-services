package com.ghx.api.operations.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.ghx.api.operations.dto.ExportDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.UserDeleteRequestDTO;
import com.ghx.api.operations.dto.UserDeleteRequestReasonDTO;
import com.ghx.api.operations.dto.UserValidationResponseDTO;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.model.UserDeleteRequest;
import com.ghx.api.operations.service.UserDeleteRequestService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * @author Ajith
 * 
 * UserDeleteRequestController - The class UserDeleteRequestController which holds all the
 *
 */

@RestController
@Validated
@RequestMapping("/v1/users/deleterequest")
public class UserDeleteRequestController {

	/** The Constant LOGGER. */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(UserDeleteRequestController.class);
    
    /** userDeleteRequestService instance */
    @Autowired
    private transient UserDeleteRequestService userDeleteRequestService;
    
    /**
     * save delete request
     * @param deleteRequestDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<UserDeleteRequest> saveUserDeleteRequest(@RequestBody UserDeleteRequestDTO deleteRequestDTO) {
        LOGGER.info("User Delete Request creation started {}");
        return new ResponseEntity<>(userDeleteRequestService.saveUserDeleteRequest(deleteRequestDTO), HttpStatus.CREATED);
    }

    

	/**
	 * get all user delete requests
	 * @param page: example page = 0, size = 5, sort = uploadedOn,desc
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllDeleteRequests(Pageable page) {
		return new ResponseEntity<>(userDeleteRequestService.getAllDeleteRequests(page), HttpStatus.OK);

	}

    /**
     * precheck User Delete Request
     * @param mongoKey
     * @return
     */
    @GetMapping("/precheck")
    @LogExecutionTime
    public ResponseEntity<UserValidationResponseDTO> precheckUserDeleteRequest(@RequestParam String mongoKey) {
        return new ResponseEntity<>(userDeleteRequestService.precheckUserDeleteRequest(mongoKey), HttpStatus.OK);
    }
    
    /**
     * Publish SQS event to process retry delete user request on real time.
     * @param id - Mass User Delete Request ID
     * @return
     */
    @PatchMapping("/{id}/retry")
    @LogExecutionTime
    public ResponseEntity<HttpStatus> publishEventToRetryRequest(@PathVariable String id) {
        userDeleteRequestService.publishEventToRetryRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
	/**
	 * get users details by id
	 * 
	 * @param id
	 * @param status
	 * @param page
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getUserDetailsById(@PathVariable String id,
			@RequestParam(required = false) String status, Pageable page) {
		SearchRequest searchRequest = SearchRequest.builder().oid(id).status(status).build();
		return new ResponseEntity<>(userDeleteRequestService.getUserDetailRequestsById(searchRequest, page),
				HttpStatus.OK);
	}

    /**
     * delete User Delete Request
     * @param mongoKey
     * @return
     */
    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<HttpStatus> deleteUserDeleteRequest(@PathVariable String id, @RequestBody UserDeleteRequestReasonDTO deleteRequestReason) {
        userDeleteRequestService.deleteUserDeleteRequest(id, deleteRequestReason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * @param <T>
     * @param id
     * @param exportType
     * @param status
     * @param page
     * @param response
     * @throws IOException 
     */
	@GetMapping("/{id}/export")
	public ResponseEntity<byte[]> exportUserDetailsReports(@PathVariable String id,
			@RequestParam(required = true) String exportType, @RequestParam(required = false) String status,
			HttpServletResponse response, Pageable page) throws IOException {
		SearchRequest searchRequest = SearchRequest.builder().oid(id).status(status).build();
		ExportDTO export = userDeleteRequestService.exportUserDeleteUserDetailsInfo(exportType, searchRequest, page,
				response);
		response.setHeader(ConstantUtils.CONTENT_DISPOSITION,
				"attachment; filename=\"" + export.getContentDisposition() + "\"");
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(export.getContentType()))
				.body(export.getExportData());
	}

}
