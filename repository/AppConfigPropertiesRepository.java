package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.AppConfigProperties;

/**
 * 
 * @author Subamathi
 *
 */
@Repository
public interface AppConfigPropertiesRepository extends MongoRepository<AppConfigProperties, String> {

    /**
     * Find AppConfigProperties by name
     * @param propertyName
     * @return
     */
    AppConfigProperties findByPropertyName(String propertyName);

}
