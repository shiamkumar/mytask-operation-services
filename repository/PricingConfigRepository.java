package com.ghx.api.operations.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ghx.api.operations.model.PricingTierConfig;

/**
 * 
 * @author Rajasekar Jayakumar
 *
 */
public interface PricingConfigRepository extends MongoRepository<PricingTierConfig, String> {

    @Query("{'$and' : [{'tierType' : ?0}, {'$or' : [{'expiredOn' : {$gte : ?1}}, {'expiredOn' : null} ]}]} ")
    Page<PricingTierConfig> findByTierType(String tierType, Date expiredOn, Pageable pageable);

    @Query("{'$and' : [{'tierCode' : ?0}, {'$or' : [{'expiredOn' : {$gte : ?1}}, {'expiredOn' : null} ]}]}")
    List<PricingTierConfig> findByTierCode(String tierCode, Date expiredOn);

    @Query("{'$and' : [{'tierType' : ?0}, {'effectiveFrom' :{$lte: ?1}},{'$or' : [{'expiredOn' : {$gte : ?1}}, {'expiredOn' : null} ]}]} ")
    Page<PricingTierConfig> findActiveByType(String tierType, Date currentDate, Pageable pageable);

    /**
     * Find active by tier code
     * @param tierCode
     * @param expiredOn
     * @return
     */
    @Query("{'$and' : [{'tierCode' : ?0}, {'effectiveFrom' :{$lte: ?1}}, {'$or' : [{'expiredOn' : {$gte : ?1}}, {'expiredOn' : null} ]}]}")
    PricingTierConfig findActiveByTierCode(String tierCode, Date expiredOn);

}
