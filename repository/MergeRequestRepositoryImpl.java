package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.DELETE_SUPPLIER;
import static com.ghx.api.operations.util.ConstantUtils.DELETE_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.DELETE_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.ERROR_MSG;
import static com.ghx.api.operations.util.ConstantUtils.MERGE_SUPPLIER_COLLECTION;
import static com.ghx.api.operations.util.ConstantUtils.NOTES;
import static com.ghx.api.operations.util.ConstantUtils.RETAIN_SUPPLIER;
import static com.ghx.api.operations.util.ConstantUtils.RETAIN_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.RETAIN_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.SALESFORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.MergeSupplierRequest;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * 
 * @author Sundari V
 * @since 03/05/2021
 */
@Repository
public class MergeRequestRepositoryImpl implements MergeRequestRepositoryCustom {

    private static final String ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Query to fetch all merge requests with pagination
     * @param searchRequest
     * @param pageable
     * @return List
     */
    @Override
    public List<MergeSupplierRequestDTO> findAllMergeRequests(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregation = populateAggregationForMergeRequest(searchRequest, pageable);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), MERGE_SUPPLIER_COLLECTION, MergeSupplierRequestDTO.class)
                .getMappedResults();
    }

    @SuppressWarnings("deprecation")
    private List<AggregationOperation> populateAggregationForMergeRequest(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();

        aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForMergeRequests(searchRequest)));

        aggregateOperations.add(Aggregation.project(getProjectionFields()));

        if (pageable.getSort().isUnsorted()) {
            aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, CREATED_ON)));
        } else {
            populateSortFields(pageable, aggregateOperations);
        }
        aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
        aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));

        return aggregateOperations;
    }

    private void populateSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
        pageable.getSort().get().forEach(order -> {
            String sortField = order.getProperty();
            switch (sortField) {
                case "deletedSupplierFein":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), DELETE_SUPPLIER_FEIN)));
                    break;
                case "deletedSupplierLegalName":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), DELETE_SUPPLIER_NAME)));
                    break;
                case "retainedSupplierFein":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), RETAIN_SUPPLIER_FEIN)));
                    break;
                case "retainedSupplierLegalName":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), RETAIN_SUPPLIER_NAME)));
                    break;
                default:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
                    break;
            }
        });
    }

    private String[] getProjectionFields() {
        return new String[]{ ID, SALESFORCE_ID, STATUS, NOTES, CREATED_ON, CREATED_BY, RETAIN_SUPPLIER, DELETE_SUPPLIER, ERROR_MSG };
    }

    /**
     * Query to find count of merge requests
     * @param searchRequest
     * @return long
     */
    @Override
    public long findMergeRequestsCount(SearchRequest searchRequest) {
        Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForMergeRequests(searchRequest));
        return mongoTemplate.count(query, MergeSupplierRequest.class);
    }
}
