package com.ghx.api.operations.util;

import static com.ghx.api.operations.util.ConstantUtils.COMPLETED_ON;
import static com.ghx.api.operations.util.ConstantUtils.COMPLETED_ON_END_TIME;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Ajith
 *
 */
@Component
public class MongoUtils {
    /**
     * 
     * @param fields
     * @param aggregateOperations
     */
    public void prepareExactMatch(Map<String, Object> fields, List<AggregationOperation> aggregateOperations) {
        fields.forEach((k, v) -> aggregateOperations.add(Aggregation.match(Criteria.where(k).is(v))));
    }

    /**
     *
     * @param fields
     * @param aggregateOperations
     */
    public void prepareLikeMatch(Map<String, String> fields, List<AggregationOperation> aggregateOperations) {
        fields.forEach((k, v) -> aggregateOperations.add(Aggregation.match(Criteria.where(k).regex(v, "i"))));
    }

    /**
     * 
     * @param sort
     * @param skip
     * @param limit
     * @param aggregateOperations
     */
    @SuppressWarnings("deprecation")
    public void prepareSortAndSkip(Sort sort, int skip, int limit, List<AggregationOperation> aggregateOperations) {
        aggregateOperations.add(Aggregation.sort(sort));
        aggregateOperations.add(Aggregation.skip(skip));
        aggregateOperations.add(Aggregation.limit(limit));
    }

    /**
     *
     * @param fields
     *            (key as mongo field name and value as list of values)
     * @param aggregateOperations
     */
    public void prepareINMatch(Map<String, List<String>> fields, List<AggregationOperation> aggregateOperations) {
        fields.forEach((k, v) -> aggregateOperations.add(Aggregation.match(Criteria.where(k).in(v))));
    }

    /**
     * @param processedOnFromDate
     * @param processedOnToDate
     * @param aggregateOperations
     */
    public void prepareProcessedOnFilter(Date processedOnFromDate, Date processedOnToDate, List<AggregationOperation> aggregateOperations) {
        if (Objects.nonNull(processedOnFromDate) && Objects.nonNull(processedOnToDate)) {
            aggregateOperations.add(Aggregation.match(Criteria.where("updatedOn").gte(DateUtils.getISODate(processedOnFromDate, ConstantUtils.EST))
                    .lt(DateUtils.getISODate(DateUtils.getNextDay(processedOnToDate), ConstantUtils.EST))));
        }
    }

    /**
     * @param aggregateOperations
     * @param aggregationExpression
     */
    public void prepareAddFieldForCompletedOn(List<AggregationOperation> aggregateOperations, AggregationExpression aggregationExpression) {
        aggregateOperations.add(Aggregation.addFields().addField(COMPLETED_ON).withValue(aggregationExpression).build());
    }

    /**
     * prepare projection fields for aggregation
     * @param aggregateOperations
     * @param projectionFields
     */
    public void prepareProjectionFields(List<AggregationOperation> aggregateOperations, String... projectionFields) {
        aggregateOperations.add(
                Aggregation.project(projectionFields).and(ArrayOperators.ArrayElemAt.arrayOf(COMPLETED_ON_END_TIME).elementAt(0)).as(COMPLETED_ON));
    }

}
