package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ghx.api.operations.dto.SupplierDTO;
import com.ghx.api.operations.enums.TierChangeRequestStatus;
import com.ghx.api.operations.enums.TierChangeRequestType;

import lombok.Data;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category Entity
 * 
 */
@Data
@Document(collection = "tier_change_request")
public class TierChangeRequest {
    
    /** Tier Change Request id */
    @Id
    private String id;

    /** Supplier Details - Name, Fein and Oid */
    private SupplierDTO supplier;
    
    @Enumerated(EnumType.STRING)
    private TierChangeRequestType type;
    
    /** Request Notes */
    private String notes;
    
    /** Tier Change Requested Pricing Tier Code */
    private String requestedTierCode;
    
    /** Supplier's Current Tier Code */
    private String currentTierCode;

    /** Tier Change Request Status */
    @Enumerated(EnumType.STRING)
    private TierChangeRequestStatus status;

    /** Request Created On Date Time */
    @CreatedDate
    private Date createdOn;

    /** Request Raised / Created By */
    @CreatedBy
    private String createdBy;
    
    /** Request Last updated On Date Time */
    @LastModifiedDate
    private Date updatedOn;

    /** Request Last Updated By */
    @LastModifiedBy
    private String updatedBy;

    /** Request Reviewed Date Time */
    private Date reviewedOn;

    /** Request Reviewed By */
    private String reviewedBy;

    /** Request Processed Date */
    private Date processedOn;
    
    /** Error Message in case the Request Failed to Execute / Complete */
    private String errorMessage;
    
    /** List of IDNs to be eliminated for a Tier Change */
    private List<String> eliminatedIdns;
    
    /** Idn count before (Downgrade) */
    private int idnCountBefore;

    /** Idn count after (Downgrade) */
    private int idnCountAfter;
    
    /** total paid and unpaid Idn count before (Downgrade) */
    private int totalIDNCountBefore;

    /** total paid and unpaid Idn count after (Downgrade) */
    private int totalIDNCountAfter;
    
    /**processed  by */
    private String processedBy;
    
}
