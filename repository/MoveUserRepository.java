package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ghx.api.operations.model.MoveUserRequest;

/**
 * This interface MoveUserRepository
 * @author Ananth kandasamy
 *
 */
public interface MoveUserRepository extends MongoRepository<MoveUserRequest, String> {

    /**
     * get move user details
     * @param userOid
     * @return
     */
    @Query("{ 'userOid' : ?0, status : { $in :[ 'CREATED', 'IN_PROGRESS']} }")
    List<MoveUserRequest> getByUserOid(String userOid);
}
