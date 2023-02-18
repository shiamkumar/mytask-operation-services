package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * 
 * @author Ajith
 *
 */
@Data
public class UserDeleteRequestValidationDTO {

    /** userExists */
    private boolean userExists;

    /** paidUser */
    private boolean paidUser;

    /** userId */
    private String userId;

    /** fein */
    private String fein;

    /** vendorName */
    private String vendorName;

}
