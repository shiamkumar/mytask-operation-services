package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.CURRENT_TIER;
import static com.ghx.api.operations.util.ConstantUtils.ERROR_MSG;
import static com.ghx.api.operations.util.ConstantUtils.FEIN;
import static com.ghx.api.operations.util.ConstantUtils.LEGALNAME;
import static com.ghx.api.operations.util.ConstantUtils.NOTES;
import static com.ghx.api.operations.util.ConstantUtils.PROCESSED_ON;
import static com.ghx.api.operations.util.ConstantUtils.REQUESTED_BY;
import static com.ghx.api.operations.util.ConstantUtils.REQUESTED_ON;
import static com.ghx.api.operations.util.ConstantUtils.REQUESTED_TIER;
import static com.ghx.api.operations.util.ConstantUtils.REVIEWED_BY;
import static com.ghx.api.operations.util.ConstantUtils.REVIEWED_ON;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.SUPPLIER;
import static com.ghx.api.operations.util.ConstantUtils.SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.SUPPLIER_LEGALNAME;
import static com.ghx.api.operations.util.ConstantUtils.TIER_CHANGE_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.dto.TierChangeRequestDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;
import com.ghx.api.operations.enums.PricingTierCode;
import com.ghx.api.operations.model.TierChangeRequest;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * This class TierChangeRepositoryImpl
 * @author Sundari V
 *
 */

@Repository
@SuppressWarnings({ "deprecation" })
public class TierChangeRepositoryImpl implements TierChangeRepositoryCustom {

    /** initialize Mongo Template */
    @Autowired
    private MongoTemplate mongoTemplate;

    /** initialize constant id */
    private static final String ID = "id";

    /**
     * Query to fetch all move requests with pagination
     * @param searchRequest
     * @param pageable
     * @param export
     * @return List
     */
    @Override
    public List<TierChangeRequestDTO> findAllTierChangeRequest(TierChangeRequestSearchDTO searchRequest, Pageable pageable, boolean export,
            Map<String, String> tierConfig) {
        List<AggregationOperation> aggregation = populateAggregationForTierChangeRequest(searchRequest, pageable, export, tierConfig);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), TIER_CHANGE_REQUEST, TierChangeRequestDTO.class).getMappedResults();
    }

    /**
     * 
     * @param searchRequest
     * @param pageable
     * @param export
     * @return
     */
    private List<AggregationOperation> populateAggregationForTierChangeRequest(TierChangeRequestSearchDTO searchRequest, Pageable pageable,
            boolean export, Map<String, String> tierConfig) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();

        aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForTierChangeRequests(searchRequest)));

        addProjectedFields(aggregateOperations, export, tierConfig);

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
                case FEIN:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), SUPPLIER_FEIN)));
                    break;
                case LEGALNAME:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), SUPPLIER_LEGALNAME)));
                    break;
                case REQUESTED_BY:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), CREATED_BY)));
                    break;
                case REQUESTED_ON:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), CREATED_ON)));
                    break;
                default:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
                    break;
            }
        });
    }

    private String[] getProjectionFields() {
        return new String[]{ ID, STATUS, SUPPLIER, CURRENT_TIER, REQUESTED_TIER, CREATED_ON, CREATED_BY, PROCESSED_ON, REVIEWED_BY, REVIEWED_ON,
                ERROR_MSG, NOTES, TYPE };
    }

    /**
     * 
     * @param aggregateOperations
     * @param export
     */
    private void addProjectedFields(List<AggregationOperation> aggregateOperations, boolean export, Map<String, String> tierConfig) {
        if (export && MapUtils.isNotEmpty(tierConfig)) {
            /* We have to add additional Computed Cases for requestedTierCode, currentTierCode,
             * and status fields    */
            ProjectionOperation projectionOperation = Aggregation.project(getProjectionFields())
                    .and(ConditionalOperators.switchCases(tierCodeComputedCondition(REQUESTED_TIER, tierConfig)).defaultTo(StringUtils.EMPTY)).as(REQUESTED_TIER)
                    .and(ConditionalOperators.switchCases(tierCodeComputedCondition(CURRENT_TIER, tierConfig)).defaultTo(StringUtils.EMPTY)).as(CURRENT_TIER);
            aggregateOperations.add(projectionOperation);
        } else {
            aggregateOperations.add(Aggregation.project(getProjectionFields()));
        }
    }

    /**
     * Query to find count of move requests
     * @param searchRequest
     * @return long
     */
    @Override
    public int findTierChangeRequestCount(TierChangeRequestSearchDTO searchRequest) {
        Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForTierChangeRequests(searchRequest));
        return (int) mongoTemplate.count(query, TierChangeRequest.class);
    }
    
    /**
     * 
     * @param tierCodeCaseOperators
     */
    private List<CaseOperator> tierCodeComputedCondition(String tierCodeColumn,  Map<String, String> tierConfig) {
        List<CaseOperator> tierCodeCaseOperators = new ArrayList<>();
        CaseOperator creditCardLocal = CaseOperator
                .when(ComparisonOperators.valueOf(tierCodeColumn).equalToValue(PricingTierCode.CREDIT_CARD_LOCAL.getCode()))
                .then(tierConfig.get(PricingTierCode.CREDIT_CARD_LOCAL.getCode()));
        CaseOperator creditCardState = CaseOperator
                .when(ComparisonOperators.valueOf(tierCodeColumn).equalToValue(PricingTierCode.CREDIT_CARD_STATE.getCode()))
                .then(tierConfig.get(PricingTierCode.CREDIT_CARD_STATE.getCode()));
        CaseOperator creditCardRegional = CaseOperator
                .when(ComparisonOperators.valueOf(tierCodeColumn).equalToValue(PricingTierCode.CREDIT_CARD_REGIONAL.getCode()))
                .then(tierConfig.get(PricingTierCode.CREDIT_CARD_REGIONAL.getCode()));
        CaseOperator creditCardNational = CaseOperator
                .when(ComparisonOperators.valueOf(tierCodeColumn).equalToValue(PricingTierCode.CREDIT_CARD_NATIONAL.getCode()))
                .then(tierConfig.get(PricingTierCode.CREDIT_CARD_NATIONAL.getCode()));
        tierCodeCaseOperators.add(creditCardLocal);
        tierCodeCaseOperators.add(creditCardRegional);
        tierCodeCaseOperators.add(creditCardState);
        tierCodeCaseOperators.add(creditCardNational);
        return tierCodeCaseOperators;
    }
}

