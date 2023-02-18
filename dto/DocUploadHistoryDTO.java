package com.ghx.api.operations.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * Holds the Upload History of Document upload request
 * 
 * @author Manoharan.R
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocUploadHistoryDTO {
	
	/** fein */
	private String fein;
	
	/** salesForceId*/
	private String salesForceId;
	
	/** status*/
	private String status;

	/** total User Count*/
	private long totalUserCount;

	/**success User Count */
	private long successUserCount;

	/** failed User Count*/
	private long failedUserCount;	
	
	/**Template Name for each templateOid */
	private transient String templateNames;
	
	/** Document Status*/
	private String docStatus; 
	
	/**updatedBy */
	private String updatedBy;

	/**updatedOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.EST_DATE_TIME_FORMAT, timezone = ConstantUtils.EST)
	private Date updatedOn;
	
}
