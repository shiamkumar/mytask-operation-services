package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.TicketDTO;

/**
 * The Interface TicketRepositoryCustom.
 */
public interface TicketRepositoryCustom {

	/**
	 * Find all tickets.
	 *
	 * @param ticketsDetail the contract tickets detail
	 * @param pageable the pageable
	 * @return the list
	 */
	List<TicketDTO> findAllTickets(TicketDTO ticketsDetail, Pageable pageable);

	/**
	 * Find tickets count.
	 *
	 * @param ticketsDetail the contract tickets detail
	 * @return the integer
	 */
	Integer findTicketsCount(TicketDTO ticketsDetail);

}
