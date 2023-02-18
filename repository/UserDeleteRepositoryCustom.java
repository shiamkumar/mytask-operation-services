package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.UserDeleteRequestDTO;
import com.ghx.api.operations.dto.UserDetailsInfo;
/**
 * interface UserDeleteRepositoryCustom
 * @author ananth.k
 *
 */
public interface UserDeleteRepositoryCustom {
	/**
	 * method for fetch all user delete requests
	 * @param pageable
	 * @return
	 */
	List<UserDeleteRequestDTO> findAllUserDeleteRequests(Pageable pageable);
	/**
	 * method for fetch user delete requests count
	 * @return
	 */
	long findUserDeleteRequestCount();
	
	/**
     * method fetch all user details from user delete request
     * @param searchRequest
     * @param pageable
     * @return
     */
    List<UserDetailsInfo> fetchAllUserDetails(SearchRequest searchRequest, Pageable pageable);

	/**
	 * get user details count
	 * 
	 * @param searchRequest
	 * @return
	 */
	long findUserDetailsCount(SearchRequest searchRequest);

}
