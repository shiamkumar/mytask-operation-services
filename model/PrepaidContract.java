package com.ghx.api.operations.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class PrepaidContractConfig.
 */
@SqlResultSetMapping(name = "findByPrepaidContractResult", classes = {
        @ConstructorResult(targetClass = com.ghx.api.operations.dto.PrepaidContractDTO.class, columns = { @ColumnResult(name = "oid"),
                @ColumnResult(name = "fein"), @ColumnResult(name = "supplierName"), @ColumnResult(name = "contractStatus"),
                @ColumnResult(name = "contractStartDate"), @ColumnResult(name = "contractEndDate"), @ColumnResult(name = "currentUserCount"),
                @ColumnResult(name = "currentIDNCount"), @ColumnResult(name = "maxUserCount"), @ColumnResult(name = "maxIDNCount"),
                @ColumnResult(name = "updatedBy"), @ColumnResult(name = "notes"), @ColumnResult(name = "pricingTierCode"),
                @ColumnResult(name = "pricingTierName"), @ColumnResult(name = "supplierCount", type=Integer.class),
                @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdOn"), @ColumnResult(name = "updatedOn")}) })

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "prepaid_contracts")
public class PrepaidContract implements Serializable {

    public static final String FIND_BY_PREPAID_CONTRACT_RESULTSET = "findByPrepaidContractResult";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7035075052916757384L;

    /** The oid. */
    @Id
    @GeneratedValue(generator = "visionHiLoGenerator")
    @GenericGenerator(name = "visionHiLoGenerator", strategy = "com.ghx.api.operations.util.VisionHiLoGenerator", parameters = {
            @Parameter(name = "table", value = "seed_container"), @Parameter(name = "column", value = "high_oid"),
            @Parameter(name = "install_id", value = "seed_id"), @Parameter(name = "max_lo", value = "100000") })
    @Column(name = "oid")
    private String oid;

    /** The start date. */
    @Column(name = "contract_start_date")
    private Timestamp contractStartDate;

    /** The end date. */
    @Column(name = "contract_end_date")
    private Timestamp contractEndDate;

    /** The max user Count. */
    @Column(name = "max_user_count")
    private int maxUserCount;

    /** The max IDN Count. */
    @Column(name = "max_idn_count")
    private int maxIDNCount;

    /** The current user Count. */
    @Column(name = "current_user_count")
    private int currentUserCount;

    /** The current IDN Count. */
    @Column(name = "current_idn_count")
    private int currentIDNCount;

    /** The tier code. */
    @Column(name = "pricing_tier_code")
    private String pricingTierCode;

    /** The created by. */
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    /** The created on. */
    @CreatedDate
    @Column(name = "created_on")
    private Timestamp createdOn;


    /** The updated by. */
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;


    /** The updated on. */
    @LastModifiedDate
    @Column(name = "updated_on")
    private Timestamp updatedOn;

    /** The deleted. */
    @Column(name = "deleted")
    private boolean deleted;

    /** The notes. */
    @Column(name = "notes")
    private String notes;
    
    /** Unlimited Reps flag */
    @Column(name = "unlimited_reps")
    private boolean unlimitedReps;

    /** Status field */
    @Column(name = "status")
    private String status;
    
    /** oas contract - old prepaid model */
    @Column(name="oas_contract")
    private boolean oasContract;
}
