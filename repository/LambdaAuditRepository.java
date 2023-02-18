package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.LambdaAudit;

@Repository
public interface LambdaAuditRepository extends MongoRepository<LambdaAudit, String> {

}
