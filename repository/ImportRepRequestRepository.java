package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.ImportRepRequest;

/**
 * 
 * @author Ajith
 *
 */
@Repository
public interface ImportRepRequestRepository extends MongoRepository<ImportRepRequest, String>, ImportRepRequestRepositoryCustom {
        
}
