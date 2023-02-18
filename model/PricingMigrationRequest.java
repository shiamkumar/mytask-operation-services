package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
@Document(collection = "pricing_migration_request")
public class PricingMigrationRequest {

    /** The id */
    private String id;

    /** The oid */
    private String oid;

    /** legal Name */
    private String legalName;

    /** fein */
    private String fein;

    /** The migrationFileKey */
    private String migrationFileKey;

    /** currentPlan */
    private String currentPlan;

    /** pricingPlan */
    private String pricingPlan;

    /** pricingPlanCode */
    private String pricingPlanCode;

    /** The status */
    private String status;

    /** The notes */
    private String notes;

    /** createdOn */
    @CreatedDate
    private Date createdOn;

    /** createdBy */
    @CreatedBy
    private String createdBy;

    /** updatedOn */
    @LastModifiedDate
    private Date updatedOn;

    /** updatedBy */
    @LastModifiedBy
    private String updatedBy;

    /** beforeMigrationRequest */
    private BeforeMigrationRequest beforeMigrationRequest;

    /** afterMigrationRequest */
    private AfterMigrationRequest afterMigrationRequest;

    /** RequestAudit */
    private List<RequestAudit> requestAudit;
    
    /** processIndex */
    private boolean processIndex;

    /** autoVerificationStatus */
    private String autoVerificationStatus;

    /** verificationMessage */
    private String verificationMessage;

}
