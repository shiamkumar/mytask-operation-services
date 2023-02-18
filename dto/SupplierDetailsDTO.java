package com.ghx.api.operations.dto;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
public class SupplierDetailsDTO {

    /** The fein */
    private String fein;

    /** The supplierName */
    private String supplierName;

    /** The tierCode */
    private String tierCode;

    /** The currentUsersCount */
    private int currentUsersCount;

    /** The cumulativeCurrentUsersCount */
    private int cumulativeCurrentUsersCount;

    /** The prepaidSupplier */
    private boolean prepaidSupplier;

    /** The userCountAfterImport */
    private int userCountAfterImport;

    /** The userCountAfterImport */
    private int cumulativeUserCountAfterImport;

    /** The tierExceeded */
    private boolean tierExceeded;

    /** The maxUserCount */
    private int maxUserCount;

    /** The prepaidContractOid */
    private String prepaidContractOid;

}
