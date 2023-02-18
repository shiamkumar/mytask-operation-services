package com.ghx.api.operations.enums;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category enum
 *
 */
public enum TierChangeRequestType {

    DOWNGRADE("DOWNGRADE");

    /** Tier Change Request Type */
    private String type;

    /**
     * Returns the TierChangeRequest Type Value
     * @return
     */
    public String getType() {
        return type;
    }

    TierChangeRequestType(String type) {
        this.type = type;
    }
}
