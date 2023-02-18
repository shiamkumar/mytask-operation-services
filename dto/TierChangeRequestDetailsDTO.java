package com.ghx.api.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Sundari V
 * @since 08/23/2021
 * @category DTO
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TierChangeRequestDetailsDTO extends TierChangeRequestDTO {

    /** Idn count before (Downgrade) */
    private int idnCountBefore;

    /** Idn count after (Downgrade) */
    private int idnCountAfter;

    /** Paid Rep count */
    private int repCount;
    
    /** total paid and unpaid Idn count before (Downgrade) */
    private int totalIDNCountBefore;

    /** total paid and unpaid Idn count after (Downgrade) */
    private int totalIDNCountAfter;

    /** Supplier current tier */
    private String supplierCurrentTierCode;

    /** Supplier status */
    private String supplierStatus;
}
