package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.DocUploadRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.DocUploadRequest;
import com.ghx.api.operations.model.DocUploadRequestDetails;

/**
 * interface UserDeleteRepositoryCustom
 * @author Manoharan.R
 *
 */
public interface DocUploadRequestRepositoryCustom {
	/**
	 * method for fetch all DocUpload requests
	 * @param pageable
	 * @return
	 */
	List<DocUploadRequestDTO> findAllDocUploadRequests(Pageable pageable);

	/**
	 * find Doc Upload requests count
	 */
	long findDocUploadRequestCount();

	/**
	 * fetch Doc Upload Request details by docUploadRequestId
	 */
	List<DocUploadRequestDetails> fetchAllDocUploadRequestDetails(String docUploadRequestId, Pageable pageable);

	/**
	 * get Doc Upload Request details count
	 * 
	 * @param searchRequest: Example: status=ALL
	 */
	long findDocUploadRequestDetailsCount(String docUploadRequestId);
	
	/**
	 * method for fetch Upload History Details
	 * 
	 * @param pageable
	 * @return
	 */
	List<DocUploadRequest> fetchUploadHistoryDetails(Pageable pageable);

	/**
	 * method fetch all Doc Upload Request Details
	 * 
	 * @param searchRequest
	 * @param pageable
	 * @return
	 */
	List<DocUploadRequestDetails> fetchAllDocUploadRequestDetailsInfo(SearchRequest searchRequest, Pageable pageable);

}
