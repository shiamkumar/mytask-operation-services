package com.ghx.api.operations.service;

import com.ghx.api.operations.dto.UserSecurityDTO;

/**
 * 
 * @author Loganathan.M
 *
 */
public interface UserSecurityService {
	
	UserSecurityDTO getUserByUserIdAndStatus(String userId, String status);

}