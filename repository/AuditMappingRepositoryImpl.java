package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.AUDIT_MAPPING_COLLECTION;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_NAME;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_TYPE;
import static com.ghx.api.operations.util.ConstantUtils.TYPE;

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

import com.ghx.api.operations.dto.AuditTypeDTO;
import com.ghx.api.operations.model.AuditMappingVO;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * The Class AuditMappingRepositoryImpl
 */

@Repository
@SuppressWarnings("deprecation")
public class AuditMappingRepositoryImpl implements AuditMappingRepositoryCustom {

    /** initialize Mongo Template */
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<AuditTypeDTO> findAllAuditTypes(AuditTypeDTO searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregation = populateAggregationForAuditTypes(searchRequest, pageable);
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), AUDIT_MAPPING_COLLECTION, AuditTypeDTO.class).getMappedResults();
    }

    private List<AggregationOperation> populateAggregationForAuditTypes(AuditTypeDTO searchRequest, Pageable pageable) {
        List<AggregationOperation> aggregateOperations = new ArrayList<>();
        aggregateOperations.add(Aggregation.match(MongoQueryHelper.prepareCriteriaByAuditType(searchRequest)));
        aggregateOperations.add(Aggregation.project(getProjectionFields()));
        if (pageable.getSort().isUnsorted()) {
            aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, TYPE)));
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
                case "type":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), TYPE)));
                    break;
                case "name":
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), AUDIT_NAME)));
                    break;
                default:
                    aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), TYPE)));
                    break;
            }
        });
    }

    private String[] getProjectionFields() {
        return new String[]{ TYPE, AUDIT_NAME };
    }

    @Override
    public long findAuditTypesCount(AuditTypeDTO searchRequest) {
        Query query = new Query();
        query.addCriteria(MongoQueryHelper.prepareCriteriaByAuditType(searchRequest));
        return mongoTemplate.count(query, AuditMappingVO.class);
    }

}
