package com.ghx.api.operations.dto;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghx.api.operations.enums.TierChangeRequestStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthkrishnan
 * @since 08/18/2021
 * @category DTO
 *
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TierChangeRequestSearchDTO extends SupplierDTO {
    
    /** Request Reviewed By */
    private String reviewedBy;
    
    /** Supplier's Current Pricing Tier Code */
    private String currentTierCode;
    
    /** Requested Pricing Tier Code */
    private String requestedTierCode;
    
    /** Request Processed-On From Date*/
    private Date processedOnFromDate;
    
    /** Request Processed-On To Date*/
    private Date processedOnToDate;
    
    /** Requested on From Date*/
    private Date requestedOnFromDate;
    
    /** Requested on To Date*/
    private Date requestedOnToDate;
    
    /** Tier Change Request Status */
    @Enumerated(EnumType.STRING)
    private TierChangeRequestStatus status;
    
    /** Supplier FEIN */
    private String fein;
    
    /** Supplier Legal Name */
    private String legalName;
    
    /** Requested By */
    private String requestedBy;
    
    /** Reviewed on From Date*/
    private Date reviewedOnFromDate;
    
    /** Reviewed on To Date*/
    private Date reviewedOnToDate;
}
