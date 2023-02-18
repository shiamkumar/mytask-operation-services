package com.ghx.api.operations.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.enums.DocUploadRequestStatus;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * Class DocUploadRequestDTO
 * 
 * @author Manoharan.R
 *
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocUploadRequestDTO {
	/**id */
	@Id
	private String id;

	/**document template mongoKey */
	private String templateMongoKey;
	
	/** mapping reps mongoKey */
	private String repsMongoKey;
		
	/** fein */
	private String fein;
	
	/** status*/
	private DocUploadRequestStatus status  = DocUploadRequestStatus.valueOf(ConstantUtils.CREATED);
	
	/** allReps check box*/
	private boolean allReps;
	
	/** Template Oids */
	private List<String> templateOids;
	
	/** Document Status*/
	private List<String> docStatus; 

	/** salesForceId*/
	private String salesForceId;

	/**createdBy */
	private String createdBy;

	/**createdOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.EST_DATE_TIME_FORMAT, timezone = ConstantUtils.EST)
	private Date createdOn;

	/** total User Count*/
	private long totalUserCount;

	/**success User Count */
	private long successUserCount;

	/** failed User Count*/
	private long failedUserCount;

	/**updatedBy */
	private String updatedBy;

	/**updatedOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.EST_DATE_TIME_FORMAT, timezone = ConstantUtils.EST)
	private Date updatedOn;
	
	/** mailSent */
    private boolean notification;
	
}
