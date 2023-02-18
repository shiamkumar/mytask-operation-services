package com.ghx.api.operations.dto;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * The Class ContractTicketDTO.
 *
 */

@Getter
@Setter
@Accessors(chain = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDTO {

	/** The id. */
	@Id
	private String id;

	/** The ticket number. */
	private String ticketNumber;

	/** The fein. */
	private String fein;

	/** The organization name. */
	private String organizationName;

	/** The status. */
	private String status;

	/** The request details. */
	private String requestDetails;

	/** The request origin. */
	private String requestOrigin;

	/** The closure notes. */
	private String closureNotes;

	/** The closure result. */
	private String closureResult;

	/** The processed by. */
	private String processedBy;

	/** The type. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String type;

	/** The sub type. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String subType;

	/** The processed on. */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.EST)
	private Date processedOn;

	/** The requested by. */
	private String requestedBy;

	/** The requested on. */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.EST)
	private Date requestedOn;

	/** The request count. */
	private Integer requestCount;

	/** The requested from date. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.ISO)
	private Date requestedFromDate;

	/** The requested to date. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.ISO)
	private Date requestedToDate;

	/** The processed from date. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.ISO)
	private Date processedFromDate;
	/** The processed to date. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(pattern = ConstantUtils.MM_DD_YYYY, timezone = ConstantUtils.ISO)
	private Date processedToDate;
	
}
