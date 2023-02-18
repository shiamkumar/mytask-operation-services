package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.DESTINATION_SUPPLIER;
import static com.ghx.api.operations.util.ConstantUtils.DESTINATION_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.DESTINATION_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL_ID;
import static com.ghx.api.operations.util.ConstantUtils.ERROR_MSG;
import static com.ghx.api.operations.util.ConstantUtils.MOVE_USER_COLLECTION;
import static com.ghx.api.operations.util.ConstantUtils.NAME;
import static com.ghx.api.operations.util.ConstantUtils.NOTES;
import static com.ghx.api.operations.util.ConstantUtils.SALESFORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.SOURCE_SUPPLIER;
import static com.ghx.api.operations.util.ConstantUtils.SOURCE_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.SOURCE_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.UPDATED_EMAIL_ID;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.MoveUserRequest;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * This class MoveUserRepositoryImpl
 * @author Ananth kandasamy
 *
 */
@Repository
@SuppressWarnings({ "unchecked", "deprecation" })
public class MoveUserRepositoryImpl implements MoveUserRepositoryCustom {

    /** initialize EntityManager */
    @PersistenceContext
    private transient EntityManager entityManager;

    /** initialize Mongo Template */
    @Autowired
    private MongoTemplate mongoTemplate;

    /** initialize constant id */
    private static final String ID = "id";

    @Override
    public List<String> findDomainByVendorOid(String vendorOid) {
        return entityManager.createNativeQuery("SELECT d.domain FROM Domain d WHERE d.vendor_oid = ?").setParameter(1, vendorOid)
                .unwrap(org.hibernate.query.Query.class).getResultList();
    }

    /**
     * Query to fetch all move requests with pagination
     * @param searchRequest
     * @param pageable
     * @return List
     */
    @Override
    public List<MoveUserRequestDTO> findAllMoveRequests(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregation = populateAggregationForMoveRequest(searchRequest, pageable);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), MOVE_USER_COLLECTION, MoveUserRequestDTO.class)
                .getMappedResults();
    }

    private List<AggregationOperation> populateAggregationForMoveRequest(SearchRequest searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();

        aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForMoveRequests(searchRequest)));

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
                case "sourceSupplierFein":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), SOURCE_SUPPLIER_FEIN)));
                    break;
                case "sourceSupplierLegalName":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), SOURCE_SUPPLIER_NAME)));
                    break;
                case "destinationSupplierFein":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), DESTINATION_SUPPLIER_FEIN)));
                    break;
                case "destinationSupplierLegalName":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), DESTINATION_SUPPLIER_NAME)));
                    break;
                default:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
                    break;
            }
        });
    }

    private String[] getProjectionFields() {
        return new String[]{ ID, SALESFORCE_ID, STATUS, NOTES, CREATED_ON, CREATED_BY, SOURCE_SUPPLIER, DESTINATION_SUPPLIER, ERROR_MSG, EMAIL_ID, NAME, UPDATED_EMAIL_ID };
    }

    /**
     * Query to find count of move requests
     * @param searchRequest
     * @return long
     */
    @Override
    public long findMoveRequestsCount(SearchRequest searchRequest) {
        Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForMoveRequests(searchRequest));
        return mongoTemplate.count(query, MoveUserRequest.class);
    }
}
