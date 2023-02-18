package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.EMAIL;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL_ID;
import static com.ghx.api.operations.util.ConstantUtils.FEIN;
import static com.ghx.api.operations.util.ConstantUtils.FILE_NAME;
import static com.ghx.api.operations.util.ConstantUtils.FIRST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.IMPORT_REP_REQUEST_COLLECTION;
import static com.ghx.api.operations.util.ConstantUtils.LAST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.MONGO_KEY;
import static com.ghx.api.operations.util.ConstantUtils.OID;
import static com.ghx.api.operations.util.ConstantUtils.SALESFORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.UPLOADED_BY;
import static com.ghx.api.operations.util.ConstantUtils.UPLOADED_DATE;
import static com.ghx.api.operations.util.ConstantUtils.USER_DETAILS;
import static com.ghx.api.operations.util.ConstantUtils.USER_DETAILS_DOT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.dto.ImportRepRequestDTO;
import com.ghx.api.operations.dto.RepDetailsDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.ImportRepRequest;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * @author Krishnan M
 * The Class ImportRepRequestRepositoryImpl.
 */
@Repository
public class ImportRepRequestRepositoryImpl implements ImportRepRequestRepositoryCustom {

    /** Mongo Template */
    @Autowired
    private MongoTemplate mongoTemplate;

    /** mongo id */
    private static final String ID = "id";

    /**
     * Query to fetch all import rep requests with pagination
     * @param searchRequest
     * @param pageable
     * @return List
     */
    @Override
    public List<ImportRepRequestDTO> findAllUploadHistory(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregation = populateAggregationForImportRepRequests(searchRequest, pageable);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), IMPORT_REP_REQUEST_COLLECTION, ImportRepRequestDTO.class)
                .getMappedResults();
    }

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @return
     */
    @SuppressWarnings("deprecation")
    private List<AggregationOperation> populateAggregationForImportRepRequests(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();

        aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForImportRepRequests(searchRequest)));

        aggregateOperations.add(Aggregation.project(getProjectionFields()));

        if (pageable.getSort().isUnsorted()) {
            aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, UPLOADED_DATE)));
        } else {
            populateSortFields(pageable, aggregateOperations);
        }
        aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
        aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));

        return aggregateOperations;
    }

    /**
     * 
     * @param pageable
     * @param aggregateOperations
     */
    private void populateSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
        pageable.getSort().get().forEach(order -> {
            String sortField = order.getProperty();
            switch (sortField) {
                case EMAIL_ID:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), EMAIL_ID)));
                    break;
                case UPLOADED_DATE:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), UPLOADED_DATE)));
                    break;
                default:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
                    break;
            }
        });
    }

    /**
     * 
     * @return string array 
     */
    private String[] getProjectionFields() {
        return new String[]{ ID, OID, SALESFORCE_ID, STATUS, EMAIL_ID, UPLOADED_DATE, UPLOADED_BY, FILE_NAME, MONGO_KEY, FEIN };
    }


    /**
     * Query to find count of import rep requests
     * @param searchRequest
     * @return long
     */
    @Override
    public long findUploadImportRequestCount(SearchRequest searchRequest) {
        Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForImportRepRequests(searchRequest));
        return mongoTemplate.count(query, ImportRepRequest.class);
    }
    
    /**
     * Query to find count of import rep request user count
     * @param searchRequest
     * @return long
     */
    @SuppressWarnings("rawtypes")
    @Override
    public long findImportRequestUserCount(SearchRequest searchRequest) {

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.unwind(USER_DETAILS),
                Aggregation.match(MongoQueryHelper.mainCreteriaForImportRepUserDetails(searchRequest)), Aggregation.count().as("count"));
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, IMPORT_REP_REQUEST_COLLECTION, Map.class);
        return Long.parseLong(results.getMappedResults().get(0).get("count").toString());
    }
 

    /**
     * Query to fetch all import rep requests with pagination
     * @param searchRequest
     * @param pageable
     * @return List
     */
    @Override
    public List<RepDetailsDTO> findAllImportRequestUserDetails(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregation = populateAggregationForImportRequestUserDetails(searchRequest, pageable);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), IMPORT_REP_REQUEST_COLLECTION, RepDetailsDTO.class)
                .getMappedResults();
    }

    /**
     * populateAggregationForImportRequestUserDetails
     * @param searchRequest
     * @param pageable
     * @return
     */
    @SuppressWarnings("deprecation")
    private List<AggregationOperation> populateAggregationForImportRequestUserDetails(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();

        aggregateOperations.add(Aggregation.unwind(USER_DETAILS));
        aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForImportRepUserDetails(searchRequest)));
        aggregateOperations.add(Aggregation.project(getImportRequestUserDetailsProjectionFields()));
        if (!pageable.getSort().isUnsorted()) {
            populateUserSortFields(pageable, aggregateOperations);
        }
        aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
        aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));

        return aggregateOperations;
    }
    /**
     * 
     * @param pageable
     * @param aggregateOperations
     */
    private void populateUserSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
        pageable.getSort().get().forEach(order -> {
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
        });
    }
    /**
     * getImportRequestUserDetailsProjectionFields
     * @return
     */
    private String[] getImportRequestUserDetailsProjectionFields() {
        return new String[]{ USER_DETAILS_DOT + STATUS, USER_DETAILS_DOT + EMAIL, USER_DETAILS_DOT + FIRST_NAME, USER_DETAILS_DOT + LAST_NAME };
    }

}
