package com.ghx.api.operations.util;

import static com.ghx.api.operations.util.ConstantUtils.COMPLETED_CAPS;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.CURRENT_TIER;
import static com.ghx.api.operations.util.ConstantUtils.DELETED;
import static com.ghx.api.operations.util.ConstantUtils.DELETE_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.DELETE_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.DESTINATION_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.DESTINATION_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.DOCUPLOAD_REQUEST_ID;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL_ID;
import static com.ghx.api.operations.util.ConstantUtils.FAILED;
import static com.ghx.api.operations.util.ConstantUtils.FAILED_CAPS;
import static com.ghx.api.operations.util.ConstantUtils.FAILURE;
import static com.ghx.api.operations.util.ConstantUtils.ID;
import static com.ghx.api.operations.util.ConstantUtils.NAME;
import static com.ghx.api.operations.util.ConstantUtils.OID;
import static com.ghx.api.operations.util.ConstantUtils.PROCESSED_ON;
import static com.ghx.api.operations.util.ConstantUtils.REQUESTED_TIER;
import static com.ghx.api.operations.util.ConstantUtils.RETAIN_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.RETAIN_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.REVIEWED_BY;
import static com.ghx.api.operations.util.ConstantUtils.REVIEWED_ON;
import static com.ghx.api.operations.util.ConstantUtils.SALESFORCE_ID;
import static com.ghx.api.operations.util.ConstantUtils.SOURCE_SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.SOURCE_SUPPLIER_NAME;
import static com.ghx.api.operations.util.ConstantUtils.STATUS;
import static com.ghx.api.operations.util.ConstantUtils.SUCCESS;
import static com.ghx.api.operations.util.ConstantUtils.SUCCESS_CAMEL_CASE;
import static com.ghx.api.operations.util.ConstantUtils.SUPPLIER_FEIN;
import static com.ghx.api.operations.util.ConstantUtils.SUPPLIER_LEGALNAME;
import static com.ghx.api.operations.util.ConstantUtils.TYPE;
import static com.ghx.api.operations.util.ConstantUtils.UPDATED_EMAIL_ID;
import static com.ghx.api.operations.util.ConstantUtils.USER_DETAILS_DOT;
import static com.ghx.api.operations.util.ConstantUtils.USER_UPLOADED;
import static com.ghx.api.operations.util.ConstantUtils. DOC_UPLOAD_REQUEST_DETAILS_DOT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.AuditTypeDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.TicketDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;

import javafx.util.Pair;

/**
 * The Class MongoQueryHelper.
 *
 * @author Nagarajan
 */
@Component
public class MongoQueryHelper {




    /**
	 * Aggregation operations.
	 *
	 * @param mainCriteria the main criteria
	 * @param addFields the add fields
	 * @param unwind the unwind
	 * @param subMatch the sub match
	 * @param projectionFields the projection fields
	 * @param pageable the pageable
	 * @return the list
	 */
	@SuppressWarnings("deprecation")
	public static List<AggregationOperation> aggregationOperations(Criteria mainCriteria,
			AggregationOperation addFields, AggregationOperation unwind, Criteria subMatch,
			List<Pair<String, Object>> projectionFields, Pageable pageable) {

		List<AggregationOperation> aggregateOperations = new ArrayList<>();

		if (Objects.nonNull(mainCriteria)) {
			MatchOperation matchOperation = Aggregation.match(mainCriteria);
			aggregateOperations.add(matchOperation);
		}

		if (Objects.nonNull(addFields)) {
			aggregateOperations.add(addFields);
		}

		if (Objects.nonNull(unwind)) {
			aggregateOperations.add(unwind);
		}

		if (Objects.nonNull(subMatch)) {
			MatchOperation subMatchOperation = Aggregation.match(subMatch);
			aggregateOperations.add(subMatchOperation);
		}
		
		if (Objects.nonNull(pageable)) {
			pageable.getSort().get().forEach(order -> {
				aggregateOperations.add(sort(order.getDirection(), ConstantUtils.SORT_TICKET.get(order.getProperty())));
			});
		}

		if (Objects.nonNull(projectionFields)) {
			aggregateOperations.add(projectionAggregation(projectionFields));
		}

		if (Objects.nonNull(pageable)) {
			aggregateOperations.add(skip(pageable.getPageNumber() * pageable.getPageSize()));
			aggregateOperations.add(limit(pageable.getPageSize()));

		}

		return aggregateOperations;
	}

	/**
	 * Gets the match operation.
	 *
	 * @param contractTicketsDetail the contract tickets detail
	 * @return the match operation
	 */
	public static Criteria mainCreteriaForGetContractTickets(TicketDTO contractTicketsDetail) {

		List<Pair<String, Object>> priceCriteria = new ArrayList<>();

		if (StringUtils.isNoneBlank(contractTicketsDetail.getStatus())) {
			priceCriteria.add(new Pair<String, Object>(ConstantUtils.STATUS, contractTicketsDetail.getStatus()));
		}
		if (StringUtils.isNoneBlank(contractTicketsDetail.getFein())) {
			priceCriteria.add(new Pair<String, Object>(ConstantUtils.FEIN, contractTicketsDetail.getFein()));
		}
		if (StringUtils.isNoneBlank(contractTicketsDetail.getRequestOrigin())) {
			priceCriteria.add(
					new Pair<String, Object>(ConstantUtils.REQUEST_ORIGIN, contractTicketsDetail.getRequestOrigin()));
		}
		if (StringUtils.isNoneBlank(contractTicketsDetail.getClosureResult())) {
			priceCriteria.add(
					new Pair<String, Object>(ConstantUtils.CLOSURE_RESULT, contractTicketsDetail.getClosureResult()));
		}
		if (StringUtils.isNoneBlank(contractTicketsDetail.getProcessedBy())) {
			priceCriteria
					.add(new Pair<String, Object>(ConstantUtils.PROCESSED_BY, contractTicketsDetail.getProcessedBy()));
		}

		Criteria mainCriteria = getMachingCriteria(priceCriteria);

		if (null != contractTicketsDetail.getProcessedFromDate()
				&& null != contractTicketsDetail.getProcessedToDate()) {


			mainCriteria.and(ConstantUtils.PROCESSED_ON).lte(calenderToDate(contractTicketsDetail.getProcessedToDate()).getTime())
					.gte(contractTicketsDetail.getProcessedFromDate());
		}

		if (StringUtils.isNoneBlank(contractTicketsDetail.getOrganizationName())) {

			mainCriteria.and(ConstantUtils.ORGANIZATION_NAME).regex(
					specialCharacterAppend(contractTicketsDetail.getOrganizationName()),
					ConstantUtils.CASE_INSENSITIVE);
		}
		
		Criteria similarRequest = new Criteria();

		if (null != contractTicketsDetail.getRequestedFromDate()
				&& null != contractTicketsDetail.getRequestedToDate()) {
			
			similarRequest.and(ConstantUtils.REQUESTED_ON)
					.lte(calenderToDate(contractTicketsDetail.getRequestedToDate()).getTime())
					.gte(contractTicketsDetail.getRequestedFromDate());
		}

		if (StringUtils.isNoneBlank(contractTicketsDetail.getRequestedBy())) {
			
			similarRequest.and(ConstantUtils.REQUESTED_BY).regex(
					specialCharacterAppend(contractTicketsDetail.getRequestedBy()), ConstantUtils.CASE_INSENSITIVE);
		}

		if (!similarRequest.getCriteriaObject().keySet().isEmpty()) {
			similarRequest.and(ConstantUtils.INITIAL_REQUEST).is(ConstantUtils.TRUE);
			mainCriteria.and(ConstantUtils.SIMILAR_REQUESTS).elemMatch(similarRequest);
		}
		
		return mainCriteria;
	}
	
	/**
	 * Special character append.
	 *
	 * @param criteriaName the criteria name
	 * @return the string
	 */
	private static String specialCharacterAppend(String criteriaName) {

		StringBuilder criteriaAppend = new StringBuilder();

		for (int i = 0; i < criteriaName.length(); i++) {

			if (!Character.isDigit(criteriaName.charAt(i)) && !Character.isLetter(criteriaName.charAt(i))) {
				criteriaAppend.append(ConstantUtils.SLASH).append(criteriaName.charAt(i));
			} else {
				criteriaAppend.append(criteriaName.charAt(i));
			}
		}
		return criteriaAppend.toString();
	}

	/**
	 * Gets the maching criteria.
	 *
	 * @param mainCriteriaFields the main criteria fields
	 * @return the maching criteria
	 */
	@SuppressWarnings("unchecked")
	private static Criteria getMachingCriteria(List<Pair<String, Object>> mainCriteriaFields) {
		Criteria mainCriteria = new Criteria();
		mainCriteriaFields.forEach(fields -> {
			if (fields.getValue() instanceof Collection<?>) {
				mainCriteria.and(fields.getKey()).in((Collection<String>) fields.getValue());
			} else {
				mainCriteria.and(fields.getKey()).is(fields.getValue());
			}
		});
		return mainCriteria;
	}

	/**
	 * Projection aggregate.
	 *
	 * @return the aggregation operation
	 */
	public static List<Pair<String, Object>> projectionForGetContractTickets() {
		List<Pair<String, Object>> projectionFields = new ArrayList<>();

		projectionFields.add(new Pair<String, Object>(ConstantUtils.ID, ConstantUtils.ID));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.TICKET_NUMBER, ConstantUtils.TICKET_NUMBER));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.FEIN, ConstantUtils.FEIN));
		projectionFields
				.add(new Pair<String, Object>(ConstantUtils.ORGANIZATION_NAME, ConstantUtils.ORGANIZATION_NAME));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.STATUS, ConstantUtils.STATUS));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.REQUEST_DETAILS, ConstantUtils.REQUEST_DETAILS));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.REQUEST_ORIGIN, ConstantUtils.REQUEST_ORIGIN));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.CLOSURE_NOTES, ConstantUtils.CLOSURE_NOTES));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.CLOSURE_RESULT, ConstantUtils.CLOSURE_RESULT));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.PROCESSED_BY, ConstantUtils.PROCESSED_BY));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.PROCESSED_ON, ConstantUtils.PROCESSED_ON));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.REQUESTED_BY, ConstantUtils.SIMILAR_REQUESTED_BY));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.REQUESTED_ON, ConstantUtils.SIMILAR_REQUESTED_ON));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.TYPE, ConstantUtils.TYPE));
		projectionFields.add(new Pair<String, Object>(ConstantUtils.SUBTYPE, ConstantUtils.SUBTYPE));
		projectionFields
				.add(new Pair<String, Object>(ConstantUtils.REQUEST_COUNT, ConstantUtils.SIMILAR_REQUEST_COUNT));
		return projectionFields;

	}

	/**
	 * Projection aggregation.
	 *
	 * @param projectionFields the projection fields
	 * @return the aggregation operation
	 */
	private static AggregationOperation projectionAggregation(List<Pair<String, Object>> projectionFields) {

		return new AggregationOperation() {

			@Override
			public Document toDocument(AggregationOperationContext aoc) {

				Map<String, Object> projection = new HashMap<>();
				projectionFields
						.forEach(fields -> projection.put(fields.getKey(), ConstantUtils.DOLLAR + fields.getValue()));
				return new Document(ConstantUtils.PROJECT, new Document(projection));

			}
		};
	}
	
	/**
	 * Calender date.
	 *
	 * @param toDate the to date
	 * @return the calendar
	 */
	private static Calendar calenderToDate(Date toDate) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(toDate); 
		cal.add(Calendar.DATE, 1);
		return cal;
	}

    /**
     * Creates criteria for merge search request
     *
     * @param mergeSupplierSearchRequest
     * @return the criteria
     */
    public static Criteria mainCreteriaForMergeRequests(SearchRequest mergeSupplierSearchRequest) {

        Criteria mainCriteria = new Criteria();

        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getSalesforceId())) {
            mainCriteria.and(SALESFORCE_ID).is(mergeSupplierSearchRequest.getSalesforceId());
        }

        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getStatus())) {
            mainCriteria.and(STATUS).is(mergeSupplierSearchRequest.getStatus().toUpperCase(Locale.getDefault()));
        } else {
            mainCriteria.and(STATUS).ne(DELETED);
        }

        if (Objects.nonNull(mergeSupplierSearchRequest.getSubmittedDateFrom()) && Objects.nonNull(mergeSupplierSearchRequest.getSubmittedDateTo())) {
            mainCriteria.and(CREATED_ON).lt(DateUtils.getISODate(DateUtils.getNextDay(mergeSupplierSearchRequest.getSubmittedDateTo()), ConstantUtils.EST))
                    .gte(DateUtils.getISODate(mergeSupplierSearchRequest.getSubmittedDateFrom(), ConstantUtils.EST));
        }

        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getSubmittedBy())) {
            mainCriteria.and(CREATED_BY).regex(specialCharacterAppend(mergeSupplierSearchRequest.getSubmittedBy()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getDeletedSupplierLegalName())) {
            mainCriteria.and(DELETE_SUPPLIER_NAME).regex(specialCharacterAppend(mergeSupplierSearchRequest.getDeletedSupplierLegalName()),
                    ConstantUtils.CASE_INSENSITIVE);
        }
        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getDeletedSupplierFein())) {
            mainCriteria.and(DELETE_SUPPLIER_FEIN).is(mergeSupplierSearchRequest.getDeletedSupplierFein());
        }

        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getRetainedSupplierLegalName())) {
            mainCriteria.and(RETAIN_SUPPLIER_NAME).regex(specialCharacterAppend(mergeSupplierSearchRequest.getRetainedSupplierLegalName()),
                    ConstantUtils.CASE_INSENSITIVE);
        }
        if (StringUtils.isNotBlank(mergeSupplierSearchRequest.getRetainedSupplierFein())) {
            mainCriteria.and(RETAIN_SUPPLIER_FEIN).is(mergeSupplierSearchRequest.getRetainedSupplierFein());
        }
        return mainCriteria;
    }

    /**
     * Creates criteria for move user search request
     *
     * @param moveUserSearchRequest
     * @return the criteria
     */
    public static Criteria mainCreteriaForMoveRequests(SearchRequest moveUserSearchRequest) {

        Criteria mainCriteria = new Criteria();

        if (StringUtils.isNotBlank(moveUserSearchRequest.getSalesforceId())) {
            mainCriteria.and(SALESFORCE_ID).is(moveUserSearchRequest.getSalesforceId());
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getStatus())) {
            mainCriteria.and(STATUS).is(moveUserSearchRequest.getStatus().toUpperCase(Locale.getDefault()));
        }

        if (Objects.nonNull(moveUserSearchRequest.getSubmittedDateFrom()) && Objects.nonNull(moveUserSearchRequest.getSubmittedDateTo())) {
            mainCriteria.and(CREATED_ON).lt(DateUtils.getISODate(DateUtils.getNextDay(moveUserSearchRequest.getSubmittedDateTo()), ConstantUtils.EST))
                    .gte(DateUtils.getISODate(moveUserSearchRequest.getSubmittedDateFrom(), ConstantUtils.EST));
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getSubmittedBy())) {
            mainCriteria.and(CREATED_BY).regex(specialCharacterAppend(moveUserSearchRequest.getSubmittedBy()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getEmailId())) {
            mainCriteria.and(EMAIL_ID).regex(specialCharacterAppend(moveUserSearchRequest.getEmailId()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getUpdatedEmailId())) {
            mainCriteria.and(UPDATED_EMAIL_ID).regex(specialCharacterAppend(moveUserSearchRequest.getUpdatedEmailId()),
                    ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getName())) {
            mainCriteria.and(NAME).regex(specialCharacterAppend(moveUserSearchRequest.getName()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getSourceSupplierLegalName())) {
            mainCriteria.and(SOURCE_SUPPLIER_NAME).regex(specialCharacterAppend(moveUserSearchRequest.getSourceSupplierLegalName()),
                    ConstantUtils.CASE_INSENSITIVE);
        }
        if (StringUtils.isNotBlank(moveUserSearchRequest.getSourceSupplierFein())) {
            mainCriteria.and(SOURCE_SUPPLIER_FEIN).is(moveUserSearchRequest.getSourceSupplierFein());
        }

        if (StringUtils.isNotBlank(moveUserSearchRequest.getDestinationSupplierLegalName())) {
            mainCriteria.and(DESTINATION_SUPPLIER_NAME).regex(specialCharacterAppend(moveUserSearchRequest.getDestinationSupplierLegalName()),
                    ConstantUtils.CASE_INSENSITIVE);
        }
        if (StringUtils.isNotBlank(moveUserSearchRequest.getDestinationSupplierFein())) {
            mainCriteria.and(DESTINATION_SUPPLIER_FEIN).is(moveUserSearchRequest.getDestinationSupplierFein());
        }
        return mainCriteria;
    }
    
    /**
     * Creates criteria for Import rep request
     *
     * @param searchRequest
     * @return the criteria
     */
    public static Criteria mainCreteriaForImportRepRequests(SearchRequest searchRequest) {

        Criteria mainCriteria = new Criteria();
        mainCriteria.and(OID).is(searchRequest.getOid());

        if (StringUtils.isNotBlank(searchRequest.getSalesforceId())) {
            mainCriteria.and(SALESFORCE_ID).is(searchRequest.getSalesforceId());
        }

        if (StringUtils.isNotBlank(searchRequest.getStatus())) {
            mainCriteria.and(STATUS).is(searchRequest.getStatus());
        } 
 
        if (StringUtils.isNotBlank(searchRequest.getEmailId())) {
            mainCriteria.and(EMAIL_ID).regex(specialCharacterAppend(searchRequest.getEmailId()),
                    ConstantUtils.CASE_INSENSITIVE);
        }
         
        return mainCriteria;
    }
    
    
    /**
     * Creates criteria for Import request user details
     *
     * @param searchRequest
     * @return the criteria
     */
    public static Criteria mainCreteriaForImportRepUserDetails(SearchRequest searchRequest) {

        Criteria mainCriteria = new Criteria();
        mainCriteria.and(ID).is(searchRequest.getImportRequestId());

        if (StringUtils.isNotBlank(searchRequest.getStatus())) {
            if(searchRequest.getStatus().equals(SUCCESS)) {
                mainCriteria.and(USER_DETAILS_DOT + STATUS).is(USER_UPLOADED);
            }else if(searchRequest.getStatus().equals(FAILED)) {
                mainCriteria.and(USER_DETAILS_DOT + STATUS).ne(USER_UPLOADED);
            }
        }

        if (StringUtils.isNotBlank(searchRequest.getEmailId())) {
            mainCriteria.and(USER_DETAILS_DOT + EMAIL).regex(specialCharacterAppend(searchRequest.getEmailId()), ConstantUtils.CASE_INSENSITIVE);
        }

        return mainCriteria;
    }

    /**
     * Creates criteria for Tier Change Requests
     *
     * @param searchRequest
     * @return the criteria
     */
    public static Criteria mainCreteriaForTierChangeRequests(TierChangeRequestSearchDTO searchRequest) {
        Criteria mainCriteria = new Criteria();

        if (Objects.nonNull(searchRequest.getStatus())) {
            mainCriteria.and(STATUS).is(searchRequest.getStatus().getStatus());
        }

        if (StringUtils.isNotBlank(searchRequest.getFein())) {
            mainCriteria.and(SUPPLIER_FEIN).is(searchRequest.getFein());
        }

        if (StringUtils.isNotBlank(searchRequest.getLegalName())) {
            mainCriteria.and(SUPPLIER_LEGALNAME).regex(specialCharacterAppend(searchRequest.getLegalName()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(searchRequest.getCurrentTierCode())) {
            mainCriteria.and(CURRENT_TIER).is(searchRequest.getCurrentTierCode());
        }

        if (StringUtils.isNotBlank(searchRequest.getRequestedTierCode())) {
            mainCriteria.and(REQUESTED_TIER).is(searchRequest.getRequestedTierCode());
        }

        if (Objects.nonNull(searchRequest.getRequestedOnFromDate()) && Objects.nonNull(searchRequest.getRequestedOnToDate())) {
            mainCriteria.and(CREATED_ON).lt(DateUtils.getISODate(DateUtils.getNextDay(searchRequest.getRequestedOnToDate()), ConstantUtils.EST))
                    .gte(DateUtils.getISODate(searchRequest.getRequestedOnFromDate(), ConstantUtils.EST));
        }

        if (StringUtils.isNotBlank(searchRequest.getRequestedBy())) {
            mainCriteria.and(CREATED_BY).regex(specialCharacterAppend(searchRequest.getRequestedBy()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (StringUtils.isNotBlank(searchRequest.getReviewedBy())) {
            mainCriteria.and(REVIEWED_BY).regex(specialCharacterAppend(searchRequest.getReviewedBy()), ConstantUtils.CASE_INSENSITIVE);
        }

        if (Objects.nonNull(searchRequest.getReviewedOnFromDate()) && Objects.nonNull(searchRequest.getReviewedOnToDate())) {
            mainCriteria.and(REVIEWED_ON).lt(DateUtils.getISODate(DateUtils.getNextDay(searchRequest.getReviewedOnToDate()), ConstantUtils.EST))
                    .gte(DateUtils.getISODate(searchRequest.getReviewedOnFromDate(), ConstantUtils.EST));
        }

        if (Objects.nonNull(searchRequest.getProcessedOnFromDate()) && Objects.nonNull(searchRequest.getProcessedOnToDate())) {
            mainCriteria.and(PROCESSED_ON).lt(DateUtils.getISODate(DateUtils.getNextDay(searchRequest.getProcessedOnToDate()), ConstantUtils.EST))
                    .gte(DateUtils.getISODate(searchRequest.getProcessedOnFromDate(), ConstantUtils.EST));
        }
        return mainCriteria;
    }
    /**
     * Creates criteria for Audit Types
     *
     * @param searchRequest
     * @return the criteria
     */
    public static Criteria prepareCriteriaByAuditType(AuditTypeDTO searchRequest) {
        Criteria mainCriteria = new Criteria();

        if (StringUtils.isNotBlank(searchRequest.getType())) {
            mainCriteria.and(TYPE).is(searchRequest.getType());
        }

        if (StringUtils.isNotBlank(searchRequest.getName())) {
            mainCriteria.and(NAME).is(searchRequest.getName());
        }
        return mainCriteria;
    }
    
    /**
     * method for  user delete requests Criteria
     * @return
     */
	public static Criteria mainCreteriaForUserDeleteRequests() {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and("status").ne("DELETED");
		return mainCriteria;
	}
	
	/**
	 * creteria added for user delete request's user details
	 * 
	 * @param searchRequest
	 * @return
	 */
	public static Criteria prepareCriteriaForDeleteRequestUserDetails(SearchRequest searchRequest) {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and(ID).is(searchRequest.getOid());
		if (StringUtils.isNotBlank(searchRequest.getStatus())) {
			if (searchRequest.getStatus().equals(SUCCESS_CAMEL_CASE)) {
				mainCriteria.and(USER_DETAILS_DOT + STATUS).is(COMPLETED_CAPS);
			} else if (searchRequest.getStatus().equals(FAILURE)) {
				mainCriteria.and(USER_DETAILS_DOT + STATUS).is(FAILED_CAPS);
			}
		}
		return mainCriteria;
	}
	
	 /**
     * method for  Doc Upload requests Criteria
     * @return
     */
	public static Criteria mainCreteriaForDocUploadRequests() {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and("status").ne("DELETED");
		return mainCriteria;
	}
	
	/**
     * method for  prepareCriteriaForDocUploadRequestDetails
     * @param docUploadRequestId
     * @return
     */
	public static Criteria prepareCriteriaForDocUploadRequestDetails(String docUploadRequestId) {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and(DOCUPLOAD_REQUEST_ID).is(docUploadRequestId);
		return mainCriteria;
	}
	
	/**
     * method for  prepareCriteriaForDocUploadRequestDetails
     * @param docUploadRequestId
     * @return
     */
	public static Criteria prepareCriteriaForUploadHistoryDetails(String docUploadRequestId) {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and(DOCUPLOAD_REQUEST_ID).is(docUploadRequestId);
		return mainCriteria;
	}
	
	/**
	 * creteria added for Doc Upload Request Details
	 * 
	 * @param searchRequest
	 * @return
	 */
	public static Criteria prepareCriteriaForDocUploadRequestDetailsInfo(SearchRequest searchRequest) {
		Criteria mainCriteria = new Criteria();
		mainCriteria.and(DOCUPLOAD_REQUEST_ID).is(searchRequest.getOid());
		if (StringUtils.isNotBlank(searchRequest.getStatus())) {
			if (searchRequest.getStatus().equals(SUCCESS_CAMEL_CASE)) {
				mainCriteria.and(DOC_UPLOAD_REQUEST_DETAILS_DOT + STATUS).is(COMPLETED_CAPS);
			} else if (searchRequest.getStatus().equals(FAILURE)) {
				mainCriteria.and(DOC_UPLOAD_REQUEST_DETAILS_DOT + STATUS).is(FAILED_CAPS);
			}
		}
		return mainCriteria;
	}
}

