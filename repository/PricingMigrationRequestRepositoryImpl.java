package com.ghx.api.operations.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.ghx.api.operations.util.ConstantUtils.AFTER_MIGRATION_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.AS;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_DETAILS;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_DETAILS_EVENTID;
import static com.ghx.api.operations.util.ConstantUtils.COMPLETED;
import static com.ghx.api.operations.util.ConstantUtils.AUTO_VERIFICATION_STATUS;
import static com.ghx.api.operations.util.ConstantUtils.BEFORE_MIGRATION_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.CONDITION;
import static com.ghx.api.operations.util.ConstantUtils.CURRENT_PLAN;
import static com.ghx.api.operations.util.ConstantUtils.DOLLAR;
import static com.ghx.api.operations.util.ConstantUtils.EQUAL;
import static com.ghx.api.operations.util.ConstantUtils.FEIN;
import static com.ghx.api.operations.util.ConstantUtils.FILTER;
import static com.ghx.api.operations.util.ConstantUtils.INPUT;
import static com.ghx.api.operations.util.ConstantUtils.LEGALNAME;
import static com.ghx.api.operations.util.ConstantUtils.NOTES;
import static com.ghx.api.operations.util.ConstantUtils.PRICING_PLAN;
import static com.ghx.api.operations.util.ConstantUtils.PRICING_PLAN_CODE;
import static com.ghx.api.operations.util.ConstantUtils.PRICING_SUPPLIER_MIGRATION;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.UPDATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.VERIFICATION_MESSAGE;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.ghx.api.operations.dto.PricingMigrationRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.MongoUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * @author Ajith
 *
 */
public class PricingMigrationRequestRepositoryImpl implements PricingMigrationRequestRepositoryCustom {

    /** Logger instance */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(PricingMigrationRequestRepositoryImpl.class);

    /**
     * MongoUtils instance
     */
    @Autowired
    private transient MongoUtils mongoUtils;

    /**
     * MongoTemplate instance
     */
    @Autowired
    private transient MongoTemplate mongoTemplate;

    /**
     * get Migration Requests
     */
    @Override
    public Map<String, Object> getMigrationRequests(SearchRequest searchRequest, Pageable pageable) {
        LOGGER.info("Fetching Migration Requests starts {}", System.currentTimeMillis());
        List<Direction> direction = pageable.getSort().get().map(Order::getDirection).collect(Collectors.toList());
        Sort sort = pageable.getSort().isSorted() ? Sort.by(direction.get(0), pageable.getSort().get().iterator().next().getProperty())
                : Sort.by(Direction.ASC, ConstantUtils.LEGALNAME);
        List<AggregationOperation> aggregateOperations = populateAggregation(searchRequest);
        int totalCount = mongoTemplate.aggregate(Aggregation.newAggregation(aggregateOperations), ConstantUtils.PRICING_MIGRATION_REQUEST_COLLECTION,
                PricingMigrationRequestDTO.class).getMappedResults().size();

        List<PricingMigrationRequestDTO> resourceList = new ArrayList<>();
        if (totalCount > 0) {
            mongoUtils.prepareSortAndSkip(sort, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize(), aggregateOperations);
            resourceList = mongoTemplate.aggregate(Aggregation.newAggregation(aggregateOperations),
                    ConstantUtils.PRICING_MIGRATION_REQUEST_COLLECTION, PricingMigrationRequestDTO.class).getMappedResults();
        }
        Map<String, Object> result = new HashMap<>();
        result.put(ConstantUtils.TOTAL_NO_OF_RECORDS, totalCount);
        result.put(ConstantUtils.MIGRATION_REQUESTS, resourceList);
        return result;
    }

    /**
     * 
     * @param searchRequest
     * @return
     */
    private List<AggregationOperation> populateAggregation(SearchRequest searchRequest) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();
        mongoUtils.prepareLikeMatch(populateLikeFields(escapeSpecialCharacters(searchRequest.getLegalName())), aggregateOperations);
        mongoUtils.prepareExactMatch(populateExactFields(searchRequest.getFein(), searchRequest.getPricingPlanCode()), aggregateOperations);
        mongoUtils.prepareINMatch(populateINFields(searchRequest.getStatus(), searchRequest.getCurrentPlan()), aggregateOperations);
        mongoUtils.prepareProcessedOnFilter(searchRequest.getProcessedOnFromDate(), searchRequest.getProcessedOnToDate(), aggregateOperations);
        mongoUtils.prepareAddFieldForCompletedOn(aggregateOperations, populateCompletedOn());
        mongoUtils.prepareProjectionFields(aggregateOperations, getProjectionFields());
        return aggregateOperations;
    }

    /**
     * get projection fields for aggregation
     */
    private String[] getProjectionFields() {
        return new String[]{ LEGALNAME, FEIN, STATUS, NOTES, AUTO_VERIFICATION_STATUS, VERIFICATION_MESSAGE, UPDATED_ON, BEFORE_MIGRATION_REQUEST,
                AFTER_MIGRATION_REQUEST, CURRENT_PLAN, PRICING_PLAN, PRICING_PLAN_CODE };
    }

    /**
     * filter to populate completedOn in addFields
     */
    private AggregationExpression populateCompletedOn() {
        return new AggregationExpression() {
            @Override
            public Document toDocument(AggregationOperationContext aoc) {
                Document filter = new Document();
                filter.put(INPUT, StringUtils.join(DOLLAR, AUDIT_DETAILS));
                filter.put(AS, AUDIT_DETAILS);
                filter.put(CONDITION, new Document(EQUAL, Arrays.<Object> asList(StringUtils.join(DOLLAR, STATUS), COMPLETED)));

                Document statusFilter = new Document();
                statusFilter.put(INPUT, new Document(FILTER, filter));
                statusFilter.put(AS, AUDIT_DETAILS);
                statusFilter.put(CONDITION, new Document(EQUAL, Arrays.<Object> asList(AUDIT_DETAILS_EVENTID, PRICING_SUPPLIER_MIGRATION)));
                return new Document(FILTER, statusFilter);
            }
        };
    }

    /**
     * 
     * @param legalName
     * @return
     */
    private Map<String, String> populateLikeFields(String legalName) {
        Map<String, String> fields = new HashMap<>();
        if (StringUtils.isNotEmpty(legalName)) {
            Pattern pattern = Pattern.compile(ConstantUtils.REGEX);
            Matcher matcher = pattern.matcher(legalName);
            fields.put(ConstantUtils.LEGALNAME, matcher.replaceAll("\\\\$0"));
        }
        return fields;
    }

    /**
     * 
     * @param fein
     * @param pricingPlanCode
     * @return
     */
    private Map<String, Object> populateExactFields(String fein, String pricingPlanCode) {
        Map<String, Object> fields = new HashMap<>();
        if (StringUtils.isNotBlank(fein)) {
            fields.put(ConstantUtils.FEIN, fein);
        }
        if (StringUtils.isNotBlank(pricingPlanCode)) {
            fields.put(ConstantUtils.PRICING_PLAN_CODE, pricingPlanCode);
        }
        return fields;
    }

    /**
     * 
     * @param status
     * @param currentPlan
     * @param pricingPlan
     * @return
     */
    private Map<String, List<String>> populateINFields(String status, String currentPlan) {
        Map<String, List<String>> fields = new HashMap<>();
        if (StringUtils.isNotBlank(status)) {
            List<String> statusList = new ArrayList<>(Arrays.asList(status.split(ConstantUtils.COMMA)));
            if (statusList.contains(StringUtils.capitalize(ConstantUtils.FAILED))) {
                statusList.add(ConstantUtils.REPROCESS);
            }
            fields.put(ConstantUtils.STATUS, statusList);
        }
        List<String> currentPlanList = new ArrayList<>();
        if (StringUtils.isNotBlank(currentPlan)) {
            currentPlanList.add(currentPlan);
            fields.put(ConstantUtils.CURRENT_PLAN, currentPlanList);
        }
        return fields;
    }

    /**
     *
     * @param searchText
     * @return String
     */
    private String escapeSpecialCharacters(String searchText) {
        String[] searchList = { "{", "}", "[", "]", "\"", ":", "\\", "/", "`", "*", "?", "+" };
        String[] replacementList = { "\\{", "\\}", "\\[", "\\]", "\\\"", "\\:", "\\\\", "\\/", "\\`", "\\*", "\\?", "\\+" };
        return StringUtils.replaceEach(searchText, searchList, replacementList);
    }


}
