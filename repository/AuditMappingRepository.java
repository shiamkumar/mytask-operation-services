package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ghx.api.operations.model.AuditMappingVO;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 09/JUNE/2022
 * 
 * Repository for {@link AuditMappingVO}
 * 
 */
public interface AuditMappingRepository extends MongoRepository<AuditMappingVO, String>{
	/**
	 * Get {@link AuditMappingVO} by type
	 * 
	 * @param type
	 * @return {@link AuditMappingVO}
	 */
	AuditMappingVO findByType(String type);
}
