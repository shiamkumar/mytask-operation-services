package com.ghx.api.operations.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Ajith
 * The class UserValidationResponseDTO - DTO which holds the validation response for precheck delete user request
 *
 */
@Data
public class UserValidationResponseDTO {

    /** availableUserCount */
    private int availableUserCount;

    /** totalUserCount */
    private int totalUserCount;

    /** userValidationResponseDTOList */
    private List<UserDeleteRequestValidationDTO> userValidationResponseDTOList;

}
