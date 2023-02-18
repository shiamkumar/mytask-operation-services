package com.ghx.api.operations.enums;

/**
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category enum
 * @version 1.1
 * 
 *          Possible Tier Change Request Status
 */
public enum TierChangeRequestStatus {

    PENDING("PENDING"), APPROVED("APPROVED"), REJECTED("REJECTED"), COMPLETED("COMPLETED"), FAILED("FAILED");

    /** Tier Change Request Status */
    private String status;

    /**
     * Returns the TierChangeRequest Value
     * @return
     */
    public String getStatus() {
        return status;
    }

    TierChangeRequestStatus(String status) {
        this.status = status;
    }
}
