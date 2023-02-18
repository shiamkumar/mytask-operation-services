package com.ghx.api.operations.enums;

/**
 * enum: UserDeleteRequestStatus
 * @author ananth.k
 *
 */
public enum UserDeleteRequestStatus {

    UPLOADED("UPLOADED"), IN_PROGRESS("IN PROGRESS"), PROCESSED("PROCESSED"), RETRY("RETRY"), DELETED("DELETED"), COMPLETED(
            "COMPLETED"), PARTIALLY_COMPLETED("PARTIALLY_COMPLETED"), FAILED("FAILED"), PENDING("PENDING");

    /** status */
    private String status;

    public String getStatus() {
        return status;
    }

    UserDeleteRequestStatus(String status) {
        this.status = status;
    }
}
