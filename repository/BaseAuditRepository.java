package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ghx.api.operations.model.BaseAudit;

public interface BaseAuditRepository extends MongoRepository<BaseAudit, String> {

}
