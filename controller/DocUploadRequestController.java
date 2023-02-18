package com.ghx.api.operations.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

import com.ghx.api.operations.dto.DocUploadRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.DocUploadRequest;
import com.ghx.api.operations.service.DocUploadRequestService;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * @author Manoharan
 * 
 * DocUploadRequestController - The class DocUploadRequestController which holds all the
 * doc upload api requests
 */

@RestController
@Validated
@RequestMapping("/v1/documents/actionrequest")
public class DocUploadRequestController {

	/** The Constant LOGGER. */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(DocUploadRequestController.class);
    
    /** docUploadRequestService instance */
    @Autowired
    private transient DocUploadRequestService docUploadRequestService;
    
    /**
     * save docUpload request
     * @param docUploadRequestDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<DocUploadRequest> saveDocUploadRequest(@RequestBody DocUploadRequestDTO docUploadRequestDTO) {
        LOGGER.info("Doc Upload Request creation started {}");
        return new ResponseEntity<>(docUploadRequestService.saveDocUploadRequest(docUploadRequestDTO), HttpStatus.CREATED);
    }

	/**
	 * get all docUpload requests
	 * @param page: example page = 0, size = 5, sort = updatedOn,desc
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Map<String,Object>> getAllDocUploadRequests(Pageable page) {
		return new ResponseEntity<>(docUploadRequestService.getAllDocUploadRequests(page), HttpStatus.OK);

	}

    /**
     * delete Doc Upload Request
     * @param mongoKey
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDocUploadRequest(@PathVariable String id) {
    	docUploadRequestService.docUploadDeleteRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
	/**
	 * get upload doc details by DocUploadRequestId
	 * 
	 * @param id
	 * @param page
	 * @return
	 */
    @GetMapping("/{id}/details")
	public ResponseEntity<Map<String,Object>> getDocUploadRequestDetails(@PathVariable String id, Pageable page) {
		return new ResponseEntity<>(docUploadRequestService.getDocUploadRequestDetails(id, page),
				HttpStatus.OK);
	}
    
	/**
	 * export doc upload request
	 * 
	 * @param id
	 * @param exportType
	 * @param page
	 * @param response
	 * @throws IOException
	 */
	@GetMapping("/export")
	public <T> void exportDocUploadHistory(@RequestParam(required = true) String exportType,
			@RequestParam(required = false) String id, HttpServletResponse response, Pageable page) throws IOException {

		if (StringUtils.isNotBlank(id)) {
			SearchRequest searchRequest = SearchRequest.builder().oid(id).build();
			 docUploadRequestService.exportDocUploadRequestDetailsInfo(exportType, searchRequest, page,
					response);
		} else {
			docUploadRequestService.exportDocUploadHistory(exportType, page, response);
		}
	}

}
