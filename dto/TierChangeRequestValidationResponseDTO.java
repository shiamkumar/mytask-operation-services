package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * The TierChangeRequestValidationResponseDTO
 * @author Sundari V
 *
 */
@Data
public class TierChangeRequestValidationResponseDTO {

    /** Id */
    private String id;

    /** Summary */
    private String summary;

    /** Message */
    private String message;

    /** Code */
    private String code;

    /** Reason */
    private String reason;

    /** Details */
    private TierChangeRequestDTO details;

}
