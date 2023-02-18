package com.ghx.api.operations.dto;

/**
 * Holds the possible status for Prepaid Contract
 * @author Sundari
 *
 */
public enum ContractStatus {

    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED");

    /** The status. */
    private String status;

    public String getStatus() {
        return status;
    }

    ContractStatus(String status) {
        this.status = status;
    }
}
