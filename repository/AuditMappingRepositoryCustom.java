package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.AuditTypeDTO;

/**
 * The interface AuditMappingRepositoryCustom
 */
public interface AuditMappingRepositoryCustom {

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
	
	List<AuditTypeDTO> findAllAuditTypes(AuditTypeDTO searchRequest, Pageable pageable);

    /**
     * 
     * @param searchRequest
     * @return
     */
	long findAuditTypesCount(AuditTypeDTO searchRequest);
}
