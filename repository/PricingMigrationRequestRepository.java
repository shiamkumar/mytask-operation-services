package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.PricingMigrationRequest;

/**
 * 
 * @author Ajith
 *
 */
@Repository
public interface PricingMigrationRequestRepository extends MongoRepository<PricingMigrationRequest, String> {
    /**
     * 
     * @param fein
     * @return
     */
    PricingMigrationRequest findByFein(String fein);

    /**
     * 
     * @param statusList
     * @return List<PricingMigrationRequest>
     */
    @Query("{'status' : {$in:?0} }")
    List<PricingMigrationRequest> findByStatus(List<String> statusList);

}
