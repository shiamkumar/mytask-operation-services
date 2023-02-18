package com.ghx.api.operations.repository;

import static com.ghx.api.operations.util.ConstantUtils.ALL_REPS;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.DOCUMENT_OID;
import static com.ghx.api.operations.util.ConstantUtils.DOCUPLOAD_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.DOCUPLOAD_REQUEST_ID;
import static com.ghx.api.operations.util.ConstantUtils.DOC_STATUS;
import static com.ghx.api.operations.util.ConstantUtils.DOC_UPLOAD_REQUEST;
import static com.ghx.api.operations.util.ConstantUtils.DOC_UPLOAD_REQUEST_DETAILS;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL_ID;
import static com.ghx.api.operations.util.ConstantUtils.ERROR_MESSAGE;
import static com.ghx.api.operations.util.ConstantUtils.FAILURE_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.FAILURE_REASON;
import static com.ghx.api.operations.util.ConstantUtils.FAILURE_USER_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.FEIN;
import static com.ghx.api.operations.util.ConstantUtils.FIRST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.LAST_NAME;
import static com.ghx.api.operations.util.ConstantUtils.MAIL_SENT;
import static com.ghx.api.operations.util.ConstantUtils.MONGO_KEY;
import static com.ghx.api.operations.util.ConstantUtils.REPS_MAPPING_MONGO_KEY;
import static com.ghx.api.operations.util.ConstantUtils.SALES_FORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.SUCCESS_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.SUCCESS_USER_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.TEMPLATE;
import static com.ghx.api.operations.util.ConstantUtils.TEMPLATE_MONGO_KEY;
import static com.ghx.api.operations.util.ConstantUtils.TEMPLATE_OIDS;
import static com.ghx.api.operations.util.ConstantUtils.TEMPLATE_STATUS;
import static com.ghx.api.operations.util.ConstantUtils.TOTAL_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.TOTAL_USER_COUNT;
import static com.ghx.api.operations.util.ConstantUtils.UPDATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.UPDATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.USER_OID;
import static com.ghx.api.operations.util.ConstantUtils.SORT_TYPE_DESC;
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

import com.ghx.api.operations.dto.DocUploadRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.model.DocUploadRequest;
import com.ghx.api.operations.model.DocUploadRequestDetails;
import com.ghx.api.operations.util.MongoQueryHelper;

/**
 * Class DocUploadRequestRepositoryImpl: using this class for invoke database for doc upload requests 
 * @author Manoharan.R
 *
 */
@Repository
@SuppressWarnings({ "deprecation" })
public class DocUploadRequestRepositoryImpl implements DocUploadRequestRepositoryCustom {

	/** initialize Mongo Template */
	@Autowired
	private MongoTemplate mongoTemplate;
	/** id */
	private static final String ID = "id";

	/**
	 * method to fetch all doc upload requests 
	 * 
	 * @param pageable: example: page 1 , size 10, sort UPDATED_ON desc
	 */
	@Override
	public List<DocUploadRequestDTO> findAllDocUploadRequests(Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationForDocUploadRequest(pageable);
		return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), DOC_UPLOAD_REQUEST,
				DocUploadRequestDTO.class).getMappedResults();
	}

	private List<AggregationOperation> populateAggregationForDocUploadRequest(Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForDocUploadRequests()));
		aggregateOperations.add(Aggregation.project(getProjectionFields()));
		if (pageable.getSort().isUnsorted()) {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, UPDATED_ON)));
		} else {
			populateSortFields(pageable, aggregateOperations);
		}
		aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
		aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));

		return aggregateOperations;
	}
	
	private String[] getProjectionFields() {
		return new String[] { ID, TEMPLATE_MONGO_KEY, REPS_MAPPING_MONGO_KEY, FEIN, SALES_FORCE_ID, STATUS, ALL_REPS,
				TEMPLATE_OIDS, TEMPLATE_STATUS, DOCUPLOAD_REQUEST, CREATED_BY, CREATED_ON, TOTAL_USER_COUNT,
				SUCCESS_USER_COUNT, DOC_STATUS, FAILURE_USER_COUNT, UPDATED_ON, UPDATED_BY, MAIL_SENT};
	}

	private void populateSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
		pageable.getSort().get().forEach(order -> {
			String sortField = order.getProperty();
			switch (sortField) {
			case UPDATED_ON:
				aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), UPDATED_ON)));
				break;
			default:
				aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
				break;
			}
		});
	}
	
	/**
	 * find Doc Upload requests count
	 */
	@Override
	public long findDocUploadRequestCount() {
		Query query = new Query();
        query.addCriteria(MongoQueryHelper.mainCreteriaForDocUploadRequests());
		return mongoTemplate.count(query, DOC_UPLOAD_REQUEST);
	}
	
	/**
	 * fetch Doc Upload Request details by searchrequest
	 */
	@Override
	public List<DocUploadRequestDetails> fetchAllDocUploadRequestDetails(String docUploadRequestId, Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationGetDocUploadRequestDetails(docUploadRequestId,
				pageable);
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(aggregation), DOC_UPLOAD_REQUEST_DETAILS, DocUploadRequestDetails.class)
				.getMappedResults();
	}

	/**
	 * method for populate aggregation doc Upload request's user detail
	 * @param searchRequest
	 * @param pageable
	 * @return
	 */
	private List<AggregationOperation> populateAggregationGetDocUploadRequestDetails(String docUploadRequestId,
			Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations
				.add(Aggregation.match(MongoQueryHelper.prepareCriteriaForDocUploadRequestDetails(docUploadRequestId)));
		aggregateOperations.add(Aggregation.project(getDocUploadRequestDetailsProjectionFields()));
		if (pageable.getSort().isUnsorted()) {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, FAILURE_COUNT, EMAIL_ID)));
		} else {
			populateSortFieldsWithFailureCount(pageable, aggregateOperations);
		}
		aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
		aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));
		return aggregateOperations;
	}

	private String[] getDocUploadRequestDetailsProjectionFields() {
		return new String[] { ID, DOCUPLOAD_REQUEST_ID, USER_OID, EMAIL_ID, FIRST_NAME, LAST_NAME, MONGO_KEY,
				DOCUMENT_OID, TEMPLATE, STATUS, FAILURE_REASON, TOTAL_COUNT, SUCCESS_COUNT, FAILURE_COUNT, CREATED_BY,
				CREATED_ON, UPDATED_ON, UPDATED_BY, ERROR_MESSAGE };
	}

	private void populateDocUploadRequestDetailsSortFields(Pageable pageable, List<AggregationOperation> aggregateOperations) {
		pageable.getSort().get().forEach(order -> {
			aggregateOperations.add(Aggregation.sort(Sort.by(order.getDirection(), order.getProperty())));
		});
	}

	/**
	 * get doc upload requests  count
	 * 
	 * @param searchRequest: Example: status=ALL
	 */
	@Override
	public long findDocUploadRequestDetailsCount(String docUploadRequestId) {
		Query query = new Query();
        query.addCriteria(MongoQueryHelper.prepareCriteriaForDocUploadRequestDetails(docUploadRequestId));
		return mongoTemplate.count(query, DOC_UPLOAD_REQUEST_DETAILS);
	}

	/**
	 * method to fetch doc upload request history
	 * 
	 * @param pageable: example: page 1 , size 10, sort UPDATED_ON desc
	 */
	@Override
	public List<DocUploadRequest> fetchUploadHistoryDetails(Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationForUploadHistoryDetails(pageable);
		return mongoTemplate
				.aggregate(Aggregation.newAggregation(aggregation), DOC_UPLOAD_REQUEST, DocUploadRequest.class)
				.getMappedResults();
	}

	private List<AggregationOperation> populateAggregationForUploadHistoryDetails(Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations.add(Aggregation.match(MongoQueryHelper.mainCreteriaForDocUploadRequests()));
		aggregateOperations.add(Aggregation.project(getProjectionFieldsForUploadHistory()));
		if (pageable.getSort().isUnsorted()) {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, UPDATED_ON)));
		} else {
			populateSortFields(pageable, aggregateOperations);
		}
		aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
		aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));

		return aggregateOperations;
	}

	private String[] getProjectionFieldsForUploadHistory() {
		return new String[] { ID, FEIN, SALES_FORCE_ID, STATUS, TOTAL_USER_COUNT, SUCCESS_USER_COUNT,
				FAILURE_USER_COUNT, TEMPLATE_OIDS, DOC_STATUS, UPDATED_ON, UPDATED_BY };
	}

	/**
	 * fetch Doc Upload Request Details by search request
	 */
	@Override
	public List<DocUploadRequestDetails> fetchAllDocUploadRequestDetailsInfo(SearchRequest searchRequest,
			Pageable pageable) {
		List<AggregationOperation> aggregation = populateAggregationGetDocUploadRequestDetailsInfo(searchRequest,
				pageable);
		return mongoTemplate.aggregate(Aggregation.newAggregation(aggregation), DOC_UPLOAD_REQUEST_DETAILS,
				DocUploadRequestDetails.class).getMappedResults();
	}

	/**
	 * method for populate aggregation Doc Upload Request Details
	 * 
	 * @param searchRequest
	 * @param pageable
	 * @return
	 */
	private List<AggregationOperation> populateAggregationGetDocUploadRequestDetailsInfo(SearchRequest searchRequest,
			Pageable pageable) {
		List<AggregationOperation> aggregateOperations = new ArrayList<>();
		aggregateOperations
				.add(Aggregation.match(MongoQueryHelper.prepareCriteriaForDocUploadRequestDetailsInfo(searchRequest)));
		aggregateOperations.add(Aggregation.project(getDocUploadRequestDetailsProjectionFields()));
		if (pageable.getSort().isUnsorted()) {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, FAILURE_COUNT, EMAIL_ID)));
		} else {
			populateSortFieldsWithFailureCount(pageable, aggregateOperations);
		}
		aggregateOperations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
		aggregateOperations.add(Aggregation.limit(pageable.getPageSize()));
		return aggregateOperations;
	}	
	
	private void populateSortFieldsWithFailureCount(Pageable pageable, List<AggregationOperation> aggregateOperations) {
    	String sortData = pageable.getSort().toString();
    	String[] splitDatas = sortData.split(",", 2);
    	String failedCountData = splitDatas[0];
    	String emailIdData = splitDatas[1];

    	String[] failedCountArray = failedCountData.split(": ", 2); 
    	String[] emailIdArray = emailIdData.split(": ", 2); 
		
		String failedCountFeildName = failedCountArray[0]; 
		String failedCountSortType = failedCountArray[1];
		
		String emailIdFeildName = emailIdArray[0];
		String emailIdSortType = emailIdArray[1];

//		String sortData = pageable.getSort().toString();
//		String[] dataArray = sortData.split(": ", 2);
//		String sortFeild = dataArray[0];
//		String sortType = dataArray[1];
		if (failedCountSortType.equalsIgnoreCase(SORT_TYPE_DESC)) {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, failedCountFeildName, emailIdFeildName)));
		} else {
			aggregateOperations.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, failedCountFeildName, emailIdFeildName)));
		}
	}
	
}

