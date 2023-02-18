package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.SupplierStatistics;

/**
 * The SupplierStatistics Repository
 */
@Repository
public interface SupplierStatisticsRepository extends MongoRepository<SupplierStatistics, String> {

}
