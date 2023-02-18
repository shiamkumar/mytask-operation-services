package com.ghx.api.operations.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * The Class PrepaidContractDTO.
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true, value = {"feinsList"}, allowSetters = true)
public class PrepaidContractDTO {

    /** The oid. */
    private String oid;

    /** The fein. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fein;

    /** The supplier name. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String supplierName;

    /** The contract status. */
    private String contractStatus;

    /** The start date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern" )
    private Date contractStartDate;

    /** The end date. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern" )
    private Date contractEndDate;

    /** The current user count. */
    private Integer currentUserCount;

    /** The current IDN count. */
    private Integer currentIDNCount;

    /** The max user Count. */
    private Integer maxUserCount;

    /** The max IDN Count. */
    private Integer maxIDNCount;

    /** The updated by. */
    private String updatedBy;

    /** The start date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String auditUpdatedOn;

    /** The notes. */
    private String notes;

    /** The prepaid plan. */
    private String pricingTierCode;

    /** The pricing tier name. */
    private String pricingTierName;
    
    /** The VendorTierPlanCode */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String vendorTierPlanCode;

    /** The vendorOid */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String vendorOid;

    /** The start date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = ConstantUtils.MM_DD_YYYY)
    private LocalDate startFromDt;

    /** The start date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = ConstantUtils.MM_DD_YYYY)
    private LocalDate startToDt;

    /** The end date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = ConstantUtils.MM_DD_YYYY)
    private LocalDate endFromDt;
    
    /** The end date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean deleted;

    /** The end date. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = ConstantUtils.MM_DD_YYYY)
    private LocalDate endToDt;
    
    /**
     * supplier count
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer supplierCount;
    
    /**
     * created by
     */
    private String createdBy;
    
    /**
     * created on
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern" )
    private Date createdOn;
    
    /**
     * updated on
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern" )
    private Date updatedOn;

    /** The list of feins */
    private String feinsList;

    /** The list of supplier details */
    private List<PrepaidContractSupplierDTO> suppliers;

    /** The Unlimited Reps flag */
    private boolean unlimitedReps;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** The updated contract count */
    private Integer updatedContract;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** The updated unpaid users count */
    private Integer usersMarkedUnpaid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    /** The updated paid users count */
    private Integer usersMarkedPaid;

    /** The status field */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    
    /** The oas Plan */
    private Boolean oasContract;
    
    /** The package Oid */
    private String packageOid;

    /**
     * Instantiates a new prepaid contract config DTO.
     *
     * @param oid
     *            the oid
     * @param fein
     *            the fein
     * @param supplierName
     *            the supplier name
     * @param contractStatus
     *            the contract status
     * @param contractStartDate
     *            the contract start date
     * @param contractEndDate
     *            the contract end date
     * @param currentUserCount
     *            the current rep count
     * @param currentIDNCount
     *            the current IDN count
     * @param maxUserCount
     *            the max rep count
     * @param maxIDNCount
     *            the max IDN count
     * @param updatedBy
     *            the updated by
     * @param notes
     *            the notes
     * @param pricingTierCode
     *            the pricing tier code
     * @param pricingTierName
     *            the pricing tier name
     */
    public PrepaidContractDTO(String oid, String fein, String supplierName, String contractStatus, Date contractStartDate, Date contractEndDate,
            int currentUserCount, int currentIDNCount, int maxUserCount, int maxIDNCount, String updatedBy, String notes, String pricingTierCode,
            String pricingTierName, Integer supplierCount, String createdBy, Date createdOn, Date updatedOn) {
        super();
        this.oid = oid;
        this.fein = fein;
        this.supplierName = supplierName;
        this.contractStatus = contractStatus;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.currentUserCount = currentUserCount;
        this.currentIDNCount = currentIDNCount;
        this.maxUserCount = maxUserCount;
        this.maxIDNCount = maxIDNCount;
        this.updatedBy = updatedBy;
        this.notes = notes;
        this.pricingTierCode = pricingTierCode;
        this.pricingTierName = pricingTierName;
        this.supplierCount = supplierCount;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    /**
     * Instantiates a new prepaid contract config DTO.
     *
     * @param fein
     *            the fein
     * @param supplierName
     *            the supplier name
     * @param contractStatus
     *            the contract status
     * @param startFromDt
     *            the start from dt
     * @param startToDt
     *            the start to dt
     * @param endFromDt
     *            the end from dt
     * @param endToDt
     *            the end to dt
     * @param updatedBy
     *            the updated by
     * @param pageable
     *            the pageable
     * @param pricingTierName
     *            the pricing tier code
     */
    public PrepaidContractDTO(String fein, String supplierName, String contractStatus, LocalDate startFromDt, LocalDate startToDt,
            LocalDate endFromDt, LocalDate endToDt, String updatedBy, String pricingTierCode) {
        super();
        this.fein = fein;
        this.supplierName = supplierName;
        this.contractStatus = contractStatus;
        this.startFromDt = startFromDt;
        this.startToDt = startToDt;
        this.endFromDt = endFromDt;
        this.endToDt = endToDt;
        this.updatedBy = updatedBy;
        this.pricingTierCode = pricingTierCode;
    }

    /**
     * Instantiates a new prepaid contract config DTO.
     */
    public PrepaidContractDTO() {
        super();
    }
}
