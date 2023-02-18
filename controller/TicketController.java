package com.ghx.api.operations.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.SimilarRequestDTO;
import com.ghx.api.operations.dto.TicketDTO;
import com.ghx.api.operations.service.TicketService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * The Class TicketController.
 */
@RestController
@Validated
@RequestMapping("/v1/tickets")
public class TicketController {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TicketController.class);

    @Autowired
    private transient TicketService ticketService;

    @Autowired
    private transient ResourceLoader resourceLoader;
    
    @PatchMapping
    public ResponseEntity<HttpStatus> update(@RequestBody List<TicketDTO> ticketDTOs) {
        LOGGER.info("TicketController update start {}", System.currentTimeMillis());
        ticketService.update(ticketDTOs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/similarrequest")
    public ResponseEntity<List<SimilarRequestDTO>> get(@PathVariable String id) {
        LOGGER.info("TicketController similarrequest start {}", System.currentTimeMillis());
        return new ResponseEntity<>(ticketService.getSimilarRequests(id), HttpStatus.OK);
    }


	/**
	 * This method will fetch all Tickets details available with contract
	 * plan.
	 *
	 * @param status            the status
	 * @param fein              the fein
	 * @param organizationName  the organization name
	 * @param requestOrigin     the request origin
	 * @param requestedBy       the requested by
	 * @param closureResult     the closure result
	 * @param requestedFromDate the requested from date
	 * @param requestedToDate   the requested to date
	 * @param processedFromDate the processed from date
	 * @param processedToDate   the processed to date
	 * @param processedBy       the processed by
	 * @param type              the type
	 * @param subType           the sub type
	 * @param pageable          the pageable
	 * @return the response entity
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> fetchTickets(@RequestParam(required = false) String status,
			@RequestParam(required = false) String fein, @RequestParam(required = false) String organizationName,
			@RequestParam(required = false) String requestOrigin, @RequestParam(required = false) String requestedBy,
			@RequestParam(required = false) String closureResult,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedFromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedToDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedFromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedToDate,
			@RequestParam(required = false) String processedBy, @RequestParam(required = true) String type,
			@RequestParam(required = true) String subType, Pageable pageable) {

		LOGGER.info("TicketController fetchTickets fein {} start {}", fein, System.currentTimeMillis());

		TicketDTO ticketsDetail = new TicketDTO(null, null, fein, organizationName, status,
				null, requestOrigin, null, closureResult, processedBy, type, subType, null, requestedBy, null, null,
				requestedFromDate, requestedToDate, processedFromDate, processedToDate);
		return new ResponseEntity<>(this.ticketService.fetchTickets(ticketsDetail, pageable),
				HttpStatus.OK);
	}
	
	@GetMapping(path = "/export/{exportType}")
	@SuppressWarnings("unchecked")
	public <T> void exportTickets(@PathVariable String exportType, HttpServletResponse response,
			@RequestParam(required = false) String status, @RequestParam(required = false) String fein,
			@RequestParam(required = false) String organizationName,
			@RequestParam(required = false) String requestOrigin, @RequestParam(required = false) String requestedBy,
			@RequestParam(required = false) String closureResult,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedFromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date requestedToDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedFromDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) Date processedToDate,
			@RequestParam(required = false) String processedBy, @RequestParam(required = true) String type,
			@RequestParam(required = true) String subType, Pageable pageable) throws IOException {
		
		LOGGER.info("TicketController exportTickets fein {} start {}", fein, System.currentTimeMillis());

		TicketDTO ticketsDetail = new TicketDTO(null, null, fein, organizationName, status, null, requestOrigin,
				null, closureResult, processedBy, type, subType, null, requestedBy, null, null, requestedFromDate,
				requestedToDate, processedFromDate, processedToDate);
		
		Map<String, Object> parameter = new HashMap<>();
		parameter.put(ConstantUtils.JASPER_STATUS, status!=null?status:ConstantUtils.ALL);

		List<T> transactionList = (List<T>) Arrays.asList(new ObjectMapper().convertValue(
				this.ticketService.fetchTickets(ticketsDetail, pageable).get(ConstantUtils.TICKETS_LIST), TicketDTO[].class));

		ReportUtils.generateReport(exportType, transactionList, response, this.resourceLoader,
				ConstantUtils.FILENAME_TICKET,parameter,ConstantUtils.TICKETS_EXPORT);
	}

    @PostMapping
    public ResponseEntity<TicketDTO> save(@RequestBody TicketDTO ticketDTO) {
        LOGGER.info("TicketController save start {} ", System.currentTimeMillis());
        return new ResponseEntity<>(ticketService.save(ticketDTO), HttpStatus.CREATED);
    }
}
