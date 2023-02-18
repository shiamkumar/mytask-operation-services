package com.ghx.api.operations.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.TierChangeRequestDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;

/**
 * This Interface TierChangeRepositoryCustom
 * @author Sundari V
 *
 */
public interface TierChangeRepositoryCustom {

    /**
     * find all tier change requests
     * @param searchRequest
     * @param pageable
     * @param export
     * @param tierConfig
     * @return
     */
    List<TierChangeRequestDTO> findAllTierChangeRequest(TierChangeRequestSearchDTO searchRequest, Pageable pageable, boolean export,
            Map<String, String> tierConfig);

    /**
     * find count of tier change requests
     * @param searchRequest
     * @return
     */
    int findTierChangeRequestCount(TierChangeRequestSearchDTO searchRequest);
}
