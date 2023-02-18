package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * @author jeyanthilal.g
 * On UserDeleteRequest the request deleted reason
 */
@Data
public class UserDeleteRequestReasonDTO {
	
	/** reason for delete the userDeleteRequest */
	private String reason;
}
