package com.ghx.api.operations.dto;

import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * @author jeyanthilal.g
 * The DTO class contains the userDetails of userDelete request
 *
 */
@Data
public class UserDetailsInfo {
	
	/** userDetails userOid */
	private String userOid;
	
	/** userDetails userId */
	private String userId;
	
	/** userDetails users firstName */
	private String firstName;
	
	/** userDetails users lastName */
	private String lastName;
	
	/** userDetails success or failure reason */
	private String reason = ConstantUtils.EMPTY;
	
	/** userDetails request status */
	private String status = ConstantUtils.PENDING_CAPS;
}
