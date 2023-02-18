package com.ghx.api.operations.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.model.AfterMigrationRequest;
import com.ghx.api.operations.model.BeforeMigrationRequest;
import com.ghx.api.operations.util.ConstantUtils;
import lombok.Data;

/**
 * 
 * @author Ajith
 *
 */
@Data
@JsonIgnoreProperties(value = { "migrationFileKey" }, allowSetters = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PricingMigrationRequestDTO {

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date createdOn;

    /** createdBy */
    private String createdBy;

    /** updatedOn */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date updatedOn;

    /** updatedBy */
    private String updatedBy;

    /** beforeMigrationRequest */
    private BeforeMigrationRequest beforeMigrationRequest;

    /** afterMigrationRequest */
    private AfterMigrationRequest afterMigrationRequest;

    /** unpaidUserCount */
    private int unpaidUserCount;

    /** paidUserCount */
    private int paidUserCount;

    /** autoVerificationStatus */
    private String autoVerificationStatus;

    /** verificationMessage */
    private String verificationMessage;

    /** Audit Details map    */
    private List<MigrationAuditDTO> auditDetails;
    
    /** completedOn date */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date completedOn;
}
