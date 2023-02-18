package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.enums.DocUploadRequestStatus;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * document uploaded request details for collection to encapsulate the business data
 * @author Manoharan.R
 */
@Data
@Document(collection = "doc_upload_request")
public class DocUploadRequest {
	/** The _id */
	private String id;
	
    /** The salesforceId */
    private String salesForceId;
    
	/**document template mongoKey */
	private String templateMongoKey;
	
	/** mapping Reps mongoKey */
	private String repsMongoKey;
		
	/** fein */
	private String fein;
	
	/** allReps check box*/
	private boolean allReps;
	
	/** Template Oids */
	private List<String> templateOids;
	
	/** Document Status*/
	private List<String> docStatus; 

    /** The createdBy */
    private String createdBy;
    
    /** The createdOn */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date createdOn;
    
    /** The updatedOn */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date updatedOn;
    
    /** updated By */
    private String updatedBy;
    
    /** The status */
	private DocUploadRequestStatus status  = DocUploadRequestStatus.valueOf(ConstantUtils.CREATED);
    
    /** valid user Total uploaded count */
    private long totalUserCount;
    
    /** user deletion success count */
    private long successUserCount;
    
    /** user successfully failure count */
	private long failedUserCount;
	
	/** mailSent */
    private boolean notification;

}
