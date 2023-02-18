package com.ghx.api.operations.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ghx.api.operations.model.Ticket;
 
/**
 * The Interface TicketRepository.
 */
public interface TicketRepository extends MongoRepository<Ticket, String>, TicketRepositoryCustom {

    Ticket findByTicketNumber(String ticketNumber);

    Ticket findByFeinAndRequestOriginAndStatus(String fein, String requestOrigin, String open);

}
