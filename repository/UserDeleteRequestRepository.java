package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;

import com.ghx.api.operations.model.UserDeleteRequest;

/**
 * The class userDeleteRequest repository will contains of save and get request
 * @author jeyanthilal.g
 */
@Repository
public interface UserDeleteRequestRepository extends MongoRepository<UserDeleteRequest, String>{
    
    /**
     * 
     * @param id
     * @return
     */
    @Query("{'id': ?0, 'status' : 'PARTIALLY_COMPLETED'}")
    UserDeleteRequest findByIdAndStatus(String id);

}
