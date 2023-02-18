package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ghx.api.operations.model.TierChangeRequest;

/**
 * @author Mari Muthu Muthukrishnan
 * @since 08/19/2021
 * @category Repository
 *
 */
public interface TierChangeRequestRepository extends MongoRepository<TierChangeRequest, String> {

    /**
     * Find by SupplierOid
     * @param oid
     * @param type
     * @param status
     * @return
     */
    @Query("{'supplier.oid' : ?0, 'type' : ?1 , 'status': ?2}")
    TierChangeRequest findBySupplierOid(String oid, String type, String status);

}
