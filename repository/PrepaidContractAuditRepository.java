package com.ghx.api.operations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ghx.api.operations.model.PrepaidContractAudit;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * Prepaid Contract Audit Mongo Repository
 *
 */
public interface PrepaidContractAuditRepository extends MongoRepository<PrepaidContractAudit, String> {

    /**
     * Retrieves the List of Audits for a Prepaid Contract Oid
     * @param prepaidContractOid
     * @param pageable
     * @return
     */
    Page<PrepaidContractAudit> findByPrepaidContractOid(String prepaidContractOid, Pageable pageable);

}
