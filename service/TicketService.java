package com.ghx.api.operations.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.SimilarRequestDTO;
import com.ghx.api.operations.dto.TicketDTO;

/**
 * The Interface TicketService.
 */
public interface TicketService {
    
    void update(List<TicketDTO> ticketDTOs);

    List<SimilarRequestDTO> getSimilarRequests(String id);

	/**
	 * Fetch tickets.
	 *
	 * @param contractTicketsDetail the contract tickets detail
	 * @param pageable              the pageable
	 * @return the map
	 */
	Map<String, Object> fetchTickets(TicketDTO ticketsDetail, Pageable pageable);

    TicketDTO save(TicketDTO ticketDTO);

}
