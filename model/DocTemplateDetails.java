package com.ghx.api.operations.model;

import lombok.Data;

/**
 * @author Manoharan.R
 * The DTO class contains the DocUploadDetails of docUpload request
 *
 */
@Data
public class DocTemplateDetails {
	
    /** oid */
    private String oid;

    /** name */
    private String name;

    /** status */
    private String status;

    /** reason */
    private String reason;

    /** errorMessage */
    private String errorMessage;

    /** docStatus */
    private String docStatus;

    /** docOid */
    private String docOid;
    
    /** autoAttach */
    private boolean autoAttach;
	
}
