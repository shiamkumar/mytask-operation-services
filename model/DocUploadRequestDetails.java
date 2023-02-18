package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;
/**
 * The class DocUploadRequestDetails
 * @author Manoharan.R
 */
@Data
@Document(collection = "doc_upload_request_details")
public class DocUploadRequestDetails {
	/**id */
	@Id
	private String id;

	/**document Upload Request Id */
	private String docUploadRequestId;
	
	/** user Oid */
	private String userOid;
		
	/** email Id */
	private String emailId;
	
	/** first Name*/
	private String firstName;
	
	/** last Name*/
	private String lastName;
	
    /** Mongo Key */
    private String mongoKey;

    /** Document Oid */
    private String documentOid;
	
	/** List of template */
	private List<DocTemplateDetails> template;
	
	/** Status*/
	private String status; 

	/**createdBy */
	private String createdBy;

	/**createdOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY_HH_MM_SS_A, timezone = ConstantUtils.EST)
	private Date createdOn;

	/** totalCount*/
	private long totalCount;

	/**successCount */
	private long successCount;

	/** failedCount*/
	private long failedCount;

	/**updatedBy */
	private String updatedBy;

	/**updatedOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY_HH_MM_SS, timezone = ConstantUtils.EST)
	private Date updatedOn;
	
	/**failure Reason */
	private String failureReason;
	
    /** errorMessage */
    private String errorMessage;

}
