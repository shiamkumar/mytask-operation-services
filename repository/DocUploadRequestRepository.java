package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.DocUploadRequest;

/**
 * The class docUploadRequest repository will contains of save and get request
 * @author Manoharan.R
 */
@Repository
public interface DocUploadRequestRepository extends MongoRepository<DocUploadRequest, String>{
    
    /**
     * 
     * @param id
     * @return
     */
    @Query("{'id': ?0, 'status' : 'PARTIALLY_COMPLETED'}")
    DocUploadRequest findByIdAndStatus(String id);

}
