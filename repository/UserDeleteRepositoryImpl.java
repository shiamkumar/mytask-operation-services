package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.COUNT;
import static com.ghx.api.operations.util.ConstantUtils.DELETED_BY;
import static com.ghx.api.operations.util.ConstantUtils.DELETED_ON;
import static com.ghx.api.operations.util.ConstantUtils.FAILURE_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.FIRST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.LAST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.REASON;
import static com.ghx.api.operations.util.ConstantUtils.SALES_FORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.SUCCESS_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.TOTAL_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.UPLOADED_BY;
import static com.ghx.api.operations.util.ConstantUtils.UPLOADED_DATE;
import static com.ghx.api.operations.util.ConstantUtils.USER_DELETE_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.USER_DETAILS;
import static com.ghx.api.operations.util.ConstantUtils.USER_DETAILS_DOT;
import static com.ghx.api.operations.util.ConstantUtils.USER_ID;
import static com.ghx.api.operations.util.ConstantUtils.USER_OID;

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

import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.UserDeleteRequestDTO;
import com.ghx.api.operations.dto.UserDetailsInfo;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * Class UserDeleteRepositoryImpl: using this class for invoke database for user deleted request stuffs.
 * @author ananth.k
 *
 */
@Repository
@SuppressWarnings({ "unchecked", "deprecation" })
public class UserDeleteRepositoryImpl implements UserDeleteRepositoryCustom {

	/** initialize Mongo Template */
	@Autowired
	private MongoTemplate mongoTemplate;
	/** id */
	private static final String ID = "id";

	/**
	 * method for fetch all user delte requests implementation
	 * 
	 * @param pageable: example: page 1 , size 10, sort uploadedOn desc
	 */
	@Override
	public List<UserDeleteRequestDTO> findAllUserDeleteRequests(Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationForDeleteUserRequest(pageable);
		return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), USER_DELETE_REQUEST,
				UserDeleteRequestDTO.class).getMappedResults();
	}

	private List<AggregationOperation> populateAggregationForDeleteUserRequest(Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForUserDeleteRequests()));
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
	
	private String[] getProjectionFields() {
		return new String[] { ID, SALES_FORCE_ID, STATUS, UPLOADED_BY, UPLOADED_DATE, TOTAL_COUNT, SUCCESS_COUNT,
				FAILURE_COUNT, DELETED_ON, DELETED_BY };
	}

	private void populateSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
		pageable.getSort().get().forEach(order -> {
			String sortField = order.getProperty();
			switch (sortField) {
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
	 * find user delete requests count
	 */
	@Override
	public long findUserDeleteRequestCount() {
		Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForUserDeleteRequests());
		return mongoTemplate.count(query, USER_DELETE_REQUEST);
	}

	/**
	 * fetch user details by searchrequest
	 */
	@Override
	public List<UserDetailsInfo> fetchAllUserDetails(SearchRequest searchRequest, Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationGetUserDetails(searchRequest,
				pageable);
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(aggregation), USER_DELETE_REQUEST, UserDetailsInfo.class)
				.getMappedResults();
	}

	/**
	 * method for populate aggregation delete user request's user detail
	 * @param searchRequest
	 * @param pageable
	 * @return
	 */
	private List<AggregationOperation> populateAggregationGetUserDetails(SearchRequest searchRequest,
			Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations.add(Aggregation.unwind(USER_DETAILS));
		aggregateOperations
				.add(Aggregation.match(MongoQueryHelper.prepareCriteriaForDeleteRequestUserDetails(searchRequest)));
		aggregateOperations.add(Aggregation.project(getDeleteRequestUserDetailsProjectionFields()));
		if (!pageable.getSort().isUnsorted()) {
			populateUserSortFields(pageable, aggregateOperations);
		}
		aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
		aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));
		return aggregateOperations;
	}

	private String[] getDeleteRequestUserDetailsProjectionFields() {
		return new String[] { USER_DETAILS_DOT + USER_OID, USER_DETAILS_DOT + STATUS, USER_DETAILS_DOT + USER_ID,
				USER_DETAILS_DOT + FIRST_NAME, USER_DETAILS_DOT + LAST_NAME, USER_DETAILS_DOT + REASON };
	}

	private void populateUserSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
		pageable.getSort().get().forEach(order -> {
			aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
		});
	}

	/**
	 * get user details count
	 * 
	 * @param searchRequest: Example: status=ALL
	 */
	@Override
	public long findUserDetailsCount(SearchRequest searchRequest) {
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.unwind(USER_DETAILS),
				Aggregation.match(MongoQueryHelper.prepareCriteriaForDeleteRequestUserDetails(searchRequest)),
				Aggregation.count().as(COUNT));
		AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, USER_DELETE_REQUEST, Map.class);
		return Long.parseLong(results.getMappedResults().get(0).get(COUNT).toString());
	}

}
