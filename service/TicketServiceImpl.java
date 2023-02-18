package com.ghx.api.operations.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.SimilarRequestDTO;
import com.ghx.api.operations.dto.TicketDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.SimilarRequest;
import com.ghx.api.operations.model.Ticket;
import com.ghx.api.operations.repository.TicketRepository;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.DateUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.validation.business.TicketBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoException;

import ma.glasnost.orika.MapperFacade;

/**
 * The Class TicketServiceImpl.
 */
@Component
public class TicketServiceImpl implements TicketService {

    private static final String MMDDYYYYHHMMSS = "MMddyyyyHHmmss";

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TicketServiceImpl.class);

    @Autowired
    private transient TicketRepository ticketRepository;

    @Autowired
    private transient MapperFacade mapper;

    @Autowired
    private transient OperationsUtil operationsUtil;

    private static int maxNoOfTicketNoRetries = 2;


    /** The ticket business validator. */
    @Autowired
    private transient TicketBusinessValidator ticketBusinessValidator;

    @Override
    @LogExecutionTime
    public void update(List<TicketDTO> ticketDTOList) {
        if (CollectionUtils.isNotEmpty(ticketDTOList)) {
            Map<String, TicketDTO> requestTicketMap = ticketDTOList.stream()
                    .collect(Collectors.toMap(ticketDTO -> ticketDTO.getId(), ticketDTO -> ticketDTO));
            List<String> ticketIds = ticketDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(ticketIds)) {
                List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(ticketIds);
                List<Ticket> closeTicketList = ticketList.stream().filter(c -> c.getStatus().equals(ConstantUtils.CLOSED))
                        .collect(Collectors.toList());
                if (ticketIds.size() != ticketList.size()) {
                    throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.TICKET_INVALID_IDS));
                } else if (CollectionUtils.isNotEmpty(closeTicketList)) {
                    throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.TICKET_ALREADY_CLOSED));
                }
                List<Ticket> modifiedTicketList = new ArrayList<>();

                for (Ticket ticket : ticketList) {
                    TicketDTO ticketDTO = requestTicketMap.get(ticket.getId());
                    ticket.setClosureNotes(ticketDTO.getClosureNotes());
                    ticket.setClosureResult(ticketDTO.getClosureResult());
                    ticket.setStatus(ConstantUtils.CLOSED);
                    ticket.setProcessedBy(operationsUtil.getCurrentUser());
                    ticket.setProcessedOn(new Date());
                    modifiedTicketList.add(ticket);
                }
                ticketRepository.saveAll(modifiedTicketList);
            }

        }

        LOGGER.debug("Ticket close end {}");
    }

    @LogExecutionTime
    @Override
    public List<SimilarRequestDTO> getSimilarRequests(String id) {
        LOGGER.info("getSimilarRequests id  {} :", id);
        Optional<Ticket> optTicket = ticketRepository.findById(id);
        if (optTicket.isPresent()) {
            return mapper.mapAsList(optTicket.get().getSimilarRequests(), SimilarRequestDTO.class);
        } else {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.TICKET_SIMILAR_REQUEST_NOT_FOUND));
        }
    }

	/**
	 * Fetch tickets.
	 *
	 * @param ticketsDetail the tickets detail
	 * @param pageable      the pageable
	 * @return the map
	 */
	@Override
	public Map<String, Object> fetchTickets(TicketDTO ticketsDetail, Pageable pageable) {

		long start = System.currentTimeMillis();
		LOGGER.info("fetchTickets start {}", start);

		// Request and Processed date validation
		this.ticketBusinessValidator.validateDateParams(ticketsDetail);

		Map<String, Object> ticketDetail = new HashMap<>();

		try {
			List<TicketDTO> allTickets = ticketRepository.findAllTickets(ticketsDetail, pageable);
			Integer ticketsCount = ticketRepository.findTicketsCount(ticketsDetail);

			ticketDetail.put(ConstantUtils.TICKETS_LIST, allTickets);
			ticketDetail.put(ConstantUtils.TOTAL_NO_OF_RECORDS, ticketsCount.toString());
			LOGGER.info("fetchTickets end {}", System.currentTimeMillis() - start);

		} catch (MongoException ex) {
		    LOGGER.error("fetchTickets:: Exception occurred while fetching Tickets", ex);
		    throw new BusinessException(ex);
		}
		return ticketDetail;

	}

    @LogExecutionTime
    @Override
    public TicketDTO save(TicketDTO ticketDTO) {
        
        
        LOGGER.error("TicketServiceImpl save Fein  {} with origin {} and details {}", ticketDTO.getFein(), ticketDTO.getRequestOrigin(),
                ticketDTO.getRequestDetails());
        ticketBusinessValidator.isEmpty(ticketDTO);
        final String requestedBy = StringUtils.isBlank(ticketDTO.getRequestedBy()) ? operationsUtil.getCurrentUser()
                : ticketDTO.getRequestedBy();

        Ticket ticket = ticketRepository.findByFeinAndRequestOriginAndStatus(ticketDTO.getFein(), ticketDTO.getRequestOrigin(), ConstantUtils.OPEN);

        if (Objects.nonNull(ticket)) {
            List<SimilarRequest> similarRequestList = ticket.getSimilarRequests();

            SimilarRequest request = similarRequestList.stream().filter(sr -> StringUtils.equals(requestedBy,sr.getRequestedBy())).findAny()
                    .orElse(null);
            if (Objects.isNull(request)) {
                SimilarRequest similarRequest = populateSimilarRequest(Boolean.FALSE, requestedBy);
                similarRequestList.add(similarRequest);
                ticket.setSimilarRequests(similarRequestList);
                ticket = ticketRepository.save(ticket);
            }
        } else {
            ticketDTO.setStatus(ConstantUtils.OPEN);
            ticketDTO.setTicketNumber(generateTicketNumber());
            populateTicketNumber(ticketDTO);
            ticket = mapper.map(ticketDTO, Ticket.class);
            SimilarRequest similarRequest = populateSimilarRequest(Boolean.TRUE, requestedBy);
            List<SimilarRequest> similarRequests = new ArrayList<>();
            similarRequests.add(similarRequest);
            ticket.setSimilarRequests(similarRequests);
            ticket = ticketRepository.save(ticket);
        }
        
        ticketDTO.setId(ticket.getId());
        LOGGER.info("save oid: {}", ticket.getId());
        return ticketDTO;

    }

    public void populateTicketNumber(TicketDTO ticketDTO) {
        int retries = 0;
        Ticket ticket = null;
        do {
            LOGGER.debug("Generating Ticket number for {} time for ticketNumber {}", retries, ticketDTO.getTicketNumber());
            ticket = ticketRepository.findByTicketNumber(ticketDTO.getTicketNumber());
            if (Objects.nonNull(ticket)) {

                if (retries == maxNoOfTicketNoRetries) {
                    LOGGER.debug("Maximum retiries done.still we are getting duplicate ticket number {}", ticketDTO.getTicketNumber());
                    throw new BusinessException("Duplicate Ticket Number generated.");
                }
                ticketDTO.setTicketNumber(generateTicketNumber());
            }
            retries++;
        } while (Objects.nonNull(ticket) && retries <= maxNoOfTicketNoRetries);
    }

    public SimilarRequest populateSimilarRequest(boolean initialRequest, String requestedBy) {
        SimilarRequest similarRequest = new SimilarRequest();
        similarRequest.setInitialRequest(initialRequest);
        similarRequest.setRequestedBy(requestedBy);
        similarRequest.setRequestedOn(DateUtils.today());
        return similarRequest;
    }

    private static String generateTicketNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat(MMDDYYYYHHMMSS, LocaleContextHolder.getLocale());
        return sdf.format(DateUtils.today());
    }
}
