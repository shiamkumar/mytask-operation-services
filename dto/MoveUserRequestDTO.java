package com.ghx.api.operations.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.model.MoveDetails;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * Value object for MoveUserRequestDTO to encapsulate the messaging data
 * @author Ananth Kandasamy
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveUserRequestDTO {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * The Sales Force Identifier for the request
     */
    @NotBlank(message = "Salesforce Id must not be null or empty")
    private String salesforceId;

    /** Source supplier oid */
    @NotBlank(message = "Source Supplier Id must not be null or empty")
    private String sourceSupplierOid;

    /** Destination supplier Oid */
    @NotBlank(message = "Destination Supplier Id must not be null or empty")
    private String destinationSupplierOid;

    /** move request status */
    private String status;

    /** notes */
    private String notes;

    /** user's updated new email id */
    private String updatedEmailId;

    /** retian idns */
    private String[] retainIdns;

    /** email id */
    @NotBlank(message = "Email Id must not be null or empty")
    private String emailId;

    /** useroid */
    private String userOid;

    /** name */
    private String name;

    /** boolean for acknowledgeAddDomain */
    private boolean acknowledgeAddDomain;

    /**
     * source supplier details
     */
    private SupplierDTO sourceSupplier;

    /**
     * Destination supplier details
     */
    private SupplierDTO destinationSupplier;

    /** processed on */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date processedOn;

    /** created date */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date createdOn;

    /** created user */
    private String createdBy;
    
    /** Move Details */
    private MoveDetails moveDetails;
    
    /** error message */
    private String errorMessage;

}
