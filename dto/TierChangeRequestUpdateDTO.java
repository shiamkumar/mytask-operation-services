package com.ghx.api.operations.dto;

import com.ghx.api.operations.enums.TierChangeRequestStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category DTO
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class TierChangeRequestUpdateDTO {

    /** Tier Change Request Type */
    private TierChangeRequestStatus status;
    
    /** Request Notes */
    private String notes;

}
