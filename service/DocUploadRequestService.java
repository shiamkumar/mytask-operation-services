package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.DocUploadRequestDTO;
import com.ghx.api.operations.dto.ExportDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.DocUploadRequest;

/**
 * 
 * @author Manoharan.R
 *
 */
public interface DocUploadRequestService {
	
	/**
	 * get all DocUpload request
	 * @param pageable
	 * @return
	 */
	Map<String,Object> getAllDocUploadRequests(Pageable pageable);

	/**
	 * @param docUploadRequestDTO
	 * @return
	 */
	DocUploadRequest saveDocUploadRequest(DocUploadRequestDTO docUploadRequestDTO);

    /**
     * 
     * @param id
     * @return
     */
    String docUploadDeleteRequest(String id);
    
    /**
	 * get Doc Upload Request Deatils by DocUploadRequestId
	 * 
	 * @param id 
	 * @param pageable      Example: page = 0, size = 1
	 */
    Map<String,Object> getDocUploadRequestDetails(String docUploadRequestId, Pageable pageable);
    
	/**
	 * get export upload history details by id and exportType
	 * 
	 * @param exportType
	 * @param pageable   Example: page = 0, size = 1
	 * @param response
	 * @throws IOException
	 */
    void exportDocUploadHistory(String exportType, Pageable pageable, HttpServletResponse response)
			throws IOException;

	/**
	 * @param exportType
	 * @param searchRequest
	 * @param page Example: page = 0, size = 1
	 * @param response
	 * @return
	 * @throws IOException
	 */
    void exportDocUploadRequestDetailsInfo(String exportType, SearchRequest searchRequest, Pageable page,
			HttpServletResponse response) throws IOException;

}
