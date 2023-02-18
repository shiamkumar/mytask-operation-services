package com.ghx.api.operations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.UserSecurityDTO;
import com.ghx.api.operations.repository.UserSecurityRepository;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Loganathan.M
 *
 */
@Component
public class UserSecurityServiceImpl implements UserSecurityService {
	
	@Autowired
	private MapperFacade mapper;
	
	@Autowired
	private UserSecurityRepository userSecurityRepository;

	@Override
	public UserSecurityDTO getUserByUserIdAndStatus(String userId, String status) {
		return mapper.map(userSecurityRepository.findByUserIdAndUserStatusCode(userId, status), UserSecurityDTO.class);
	}

}
