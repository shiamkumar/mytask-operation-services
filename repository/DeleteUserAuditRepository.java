package com.ghx.api.operations.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.DeleteUserAudit;

/**
 * The Interface DeleteUserAuditRepository.
 */
@Repository
public interface DeleteUserAuditRepository extends MongoRepository<DeleteUserAudit, String> {

    /**
     * find Delete User Audit
     * @param userIdList
     * @param deletedOn
     * @return List<DeleteUserAudit>
     */
    @Query("{'userId': {$in: ?0}, 'deletedOn' : { $gt: ?1 },'isRecovery': false}")
    List<DeleteUserAudit> findDeleteUserAudit(Set<String> userIdList, Date deletedOn);

}
