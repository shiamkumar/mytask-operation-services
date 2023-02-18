package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.DocUploadRequestDetails;

/**
 * Interface DocUploadRequestDetailsRepository
 * @author Manoharan.R
 *
 */

@Repository
public interface DocUploadRequestDetailsRepository extends MongoRepository<DocUploadRequestDetails, String>
{
	 /**
     * get doc upload details by docUploadRequestId
     * @param docUploadRequestId
     * @return
     */
	
	@Query("{'docUploadRequestId': ?0 }")
	List<DocUploadRequestDetails> findByDocUploadRequestId(String docUploadRequestId);
	
}
