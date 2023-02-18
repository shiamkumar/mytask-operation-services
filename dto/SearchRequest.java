package com.ghx.api.operations.dto;

import java.util.Date;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 *
 */
@Data
@Builder
public class SearchRequest {

    private String salesforceId;
    
    private String status;
    
    private String submittedBy;
    
    private Date submittedDateFrom;
    
    private Date submittedDateTo;
    
    private String deletedSupplierFein;
    
    private String deletedSupplierLegalName;
    
    private String retainedSupplierFein;
    
    private String retainedSupplierLegalName;

    /** name */
    private String name;

    /** email id */
    private String emailId;

    /** user's updated new email id */
    private String updatedEmailId;

    /** source supplier fein */
    private String sourceSupplierFein;

    /** destination supplier legal name */
    private String destinationSupplierLegalName;

    /** destination supplier fein */
    private String destinationSupplierFein;

    /** source supplier legal name */
    private String sourceSupplierLegalName;
    
    /**
     * legal Name
     */
    private String legalName;
    
    /**
     * pageable
     */
    private Pageable pageable;
    
    /**
     * fein
     */
    private String fein;

    /** oid */
    private String oid;
    
    /** importRequestId */
    private String importRequestId;

    /** currentPlan */
    private String currentPlan;

    /** pricingPlan */
    private String pricingPlanCode;
    
    /** ProcessedOn from date */
    private Date processedOnFromDate;
    /** ProcessedOn to date */
    private Date processedOnToDate;
}
