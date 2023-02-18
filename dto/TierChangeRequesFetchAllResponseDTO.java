package com.ghx.api.operations.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category DTO
 *
 */
@Data
@Builder
public class TierChangeRequesFetchAllResponseDTO {

    /** Total Number of Records */
    private int totalNoOfRecords;
    
    /** Tier Change Request List */
    private List<TierChangeRequestDTO> requestList;

}
