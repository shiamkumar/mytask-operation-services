package com.ghx.api.operations.service;

import com.ghx.api.operations.dto.ExportDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.UserDeleteRequestDTO;
import com.ghx.api.operations.dto.UserDeleteRequestReasonDTO;
import com.ghx.api.operations.model.UserDeleteRequest;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import com.ghx.api.operations.dto.UserValidationResponseDTO;

/**
 * 
 * @author Ajith
 *
 */
public interface UserDeleteRequestService {
	
	/**
	 * get all delete users request
	 * @param pageable
	 * @return
	 */
	 Map<String, Object> getAllDeleteRequests(Pageable pageable);

	/**
	 * @param deleteRequestDTO
	 * @return
	 */
	UserDeleteRequest saveUserDeleteRequest(UserDeleteRequestDTO deleteRequestDTO);
    
	/**
     * 
     * @param mongoKey
     * @return
     */
    UserValidationResponseDTO precheckUserDeleteRequest(String mongoKey);

    
    /**
     * Publish SQS event to process retry delete user request on real time.
     * @param id
     * @return
     */
    String publishEventToRetryRequest(String id);
	/**
	 * get user details by id
	 * @param searchRequest
	 * @return
	 */
	Map<String, Object> getUserDetailRequestsById(SearchRequest searchRequest, Pageable pageable);

    /**
     * 
     * @param id
     * @return
     */
    String deleteUserDeleteRequest(String id, UserDeleteRequestReasonDTO deleteRequestReason);

	/**
	 * @param exportType
	 * @param searchRequest
	 * @param page
	 * @param response
	 * @return
	 * @throws IOException
	 */
	ExportDTO exportUserDeleteUserDetailsInfo(String exportType, SearchRequest searchRequest, Pageable page,
			HttpServletResponse response) throws IOException;

}
