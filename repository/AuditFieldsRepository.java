package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ghx.api.operations.model.AuditFieldsVO;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 09/JUNE/2022
 * 
 * Repository for {@link AuditFieldsVO}
 */
public interface AuditFieldsRepository extends MongoRepository<AuditFieldsVO, String>{
	/**
	 * Get {@link AuditFieldsVO} by fieldName
	 * 
	 * @param fieldName
	 * @return {@link AuditFieldsVO}
	 */
	AuditFieldsVO findByFieldName(String fieldName);
}
