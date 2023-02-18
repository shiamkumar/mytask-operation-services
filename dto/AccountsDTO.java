package com.ghx.api.operations.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class AccountsDTO
 * @author Sundari V
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDTO {

    /**
     * VC Oid
     */
    private String vcOid;

    /**
     * Customer Name
     */
    private String customerName;

    /**
     * Customer Oid
     */
    private String customerOid;

    /** The reps count */
    private long repsCount;
    
    /** vc relation createdBy */
    private String createdBy;
    
    /** vc relation status */
    private String vcStatus;
}
