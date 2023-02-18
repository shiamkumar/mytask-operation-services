package com.ghx.api.operations.enums;
/**
 * enum: DocUploadRequestStatus
 * @author Manoharan.R
 *
 */
public enum DocUploadRequestStatus {
	
	CREATED("CREATED"), SUCCESS("SUCCESS"), FAILED("FAILED"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED"),
	DELETED("DELETED"), REPROCESS("REPROCESS");

    /** status */
    private String status;

    public String getStatus() {
        return status;
    }

    DocUploadRequestStatus(String status) {
        this.status = status;
    }
}
