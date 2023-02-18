package com.ghx.api.operations.service;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.IdnsResponseDTO;
import com.ghx.api.operations.dto.TierChangeRequesFetchAllResponseDTO;
import com.ghx.api.operations.dto.TierChangeRequestDTO;
import com.ghx.api.operations.dto.TierChangeRequestDetailsDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;
import com.ghx.api.operations.dto.TierChangeRequestUpdateDTO;
import com.ghx.api.operations.dto.TierChangeRequestValidationResponseDTO;
/**
 * 
 * @author Mari Muthu Muthkrishnan
 * @since 08/18/2021
 * @category service
 *
 */
public interface TierChangeRequestService {

    /**
     * Retrieves the TierChange Request Details
     * @param tierChangeRequestId
     * @return
     */
    TierChangeRequestDetailsDTO getTierChangeRequestById(String tierChangeRequestId);

    /**
     * Save the given Tier change request Details
     * @param mergeSupplierRequest
     * @return
     */
    String create(TierChangeRequestDTO tierChangeRequestDTO);

    /**
     * Deletes the Tier Change Request by its ID
     * @param tierChangeRequestId
     */
    void deleteById(String tierChangeRequestId);

    /**
     * Fetches TierChange Requests Based on the search option
     * @param searchTierChangeRequestDTO
     * @param pageable
     * @param export
     * @return
     */
    TierChangeRequesFetchAllResponseDTO getAllTierChangeRequest(TierChangeRequestSearchDTO searchTierChangeRequestDTO, Pageable pageable, boolean export);

    /**
     * Updates the Given Tier Change Request by its Id
     * @param id
     * @param updateTierChangeRequestDTO
     * @return
     */
    TierChangeRequestDTO updateById(String id, TierChangeRequestUpdateDTO updateTierChangeRequestDTO);

    /**
     * Validates the Tier Change Request Details
     * @param id
     * @return
     */
    TierChangeRequestValidationResponseDTO validateTierChangeRequest(String id);

    /**
     * Get eliminated idns for tier change request
     * @param id
     * @param pageable
     * @return
     */
    IdnsResponseDTO getEliminatedIdns(String id, Pageable pageable);
}
