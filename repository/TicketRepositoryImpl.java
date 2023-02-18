package com.ghx.api.operations.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.dto.TicketDTO;
import com.ghx.api.operations.model.Ticket;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.MongoQueryHelper;

import javafx.util.Pair;

/**
 * The Class TicketRepositoryImpl.
 */
@Repository
public class TicketRepositoryImpl implements TicketRepositoryCustom {

	/** The mongo template. */
	@Autowired
	private transient MongoTemplate mongoTemplate;

	/**
	 * Find all tickets.
	 *
	 * @param ticketsDetail the tickets detail
	 * @param pageable the pageable
	 * @return the list
	 */
	@Override
	public List<TicketDTO> findAllTickets(TicketDTO ticketsDetail, Pageable pageable) {

		/* Main Criteria */
		Criteria mainCriteria = MongoQueryHelper.mainCreteriaForGetContractTickets(ticketsDetail);

		/* Add fields */
		AggregationOperation addFields = new AggregationOperation() {
			@Override
			public Document toDocument(AggregationOperationContext aoc) {
				return new Document(ConstantUtils.DOLLAR + ConstantUtils.ADD_FIELDS,
						new Document(ConstantUtils.SIMILAR_REQUEST_COUNT,
								new Document(ConstantUtils.DOLLAR + ConstantUtils.SIZE,
										ConstantUtils.DOLLAR + ConstantUtils.SIMILAR_REQUESTS)));
			}
		};

		/* unwind */
		AggregationOperation unwind = Aggregation.unwind(ConstantUtils.SIMILAR_REQUESTS);

		/* Sub Match */
		Criteria subCriteria = Criteria.where(ConstantUtils.SIMILAR_INITIAL_REQUEST).is(ConstantUtils.TRUE);

		/* Projection */
		List<Pair<String, Object>> projections = MongoQueryHelper.projectionForGetContractTickets();

		return this.mongoTemplate
				.aggregate(Aggregation.newAggregation(Ticket.class, MongoQueryHelper.aggregationOperations(mainCriteria,
						addFields, unwind, subCriteria, projections, pageable)), TicketDTO.class)
				.getMappedResults();

	}

	/**
	 * Find tickets count for all grid services.
	 *
	 * @param ticketsDetail the contract tickets detail
	 * @return the integer
	 */
	@SuppressWarnings("boxing")
	@Override
	public Integer findTicketsCount(TicketDTO ticketsDetail) {

		/* Main Criteria */
		Criteria mainCriteria = MongoQueryHelper.mainCreteriaForGetContractTickets(ticketsDetail);

		/* Add fields */
		AggregationOperation addFields = new AggregationOperation() {
			@Override
			public Document toDocument(AggregationOperationContext aoc) {
				return new Document(ConstantUtils.DOLLAR + ConstantUtils.ADD_FIELDS,
						new Document(ConstantUtils.SIMILAR_REQUEST_COUNT,
								new Document(ConstantUtils.DOLLAR + ConstantUtils.SIZE,
										ConstantUtils.DOLLAR + ConstantUtils.SIMILAR_REQUESTS)));
			}
		};

		/* unwind */
		AggregationOperation unwind = Aggregation.unwind(ConstantUtils.SIMILAR_REQUESTS);

		/* Sub Match */
		Criteria subCriteria = Criteria.where(ConstantUtils.SIMILAR_INITIAL_REQUEST).is(ConstantUtils.TRUE);

		List<AggregationOperation> countOperators = new ArrayList<>();
		countOperators.addAll(
				MongoQueryHelper.aggregationOperations(mainCriteria, addFields, unwind, subCriteria, null, null));
		countOperators.add(Aggregation.count().as(ConstantUtils.TOTAL_NO_OF_RECORDS));
		AggregationResults<Map> res = this.mongoTemplate.aggregate(Aggregation.newAggregation(countOperators),
				Ticket.class, Map.class);
		return CollectionUtils.isNotEmpty(res.getMappedResults())
				? (int) res.getMappedResults().get(0).get(ConstantUtils.TOTAL_NO_OF_RECORDS)
				: 0;
	}

}
