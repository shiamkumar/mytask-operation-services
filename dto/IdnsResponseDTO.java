package com.ghx.api.operations.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * The Class IdnsResponseDTO.
 * @author Sundari V
 */

@Data
@Builder
public class IdnsResponseDTO {

    /** Total Number of Records */
    private int totalNoOfRecords;

    /** IDNs List */
    private List<ProviderDetailsDTO> idnsList;
}
