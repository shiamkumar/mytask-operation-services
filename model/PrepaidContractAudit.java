package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Krishnan M
 *
 */

@Data
@NoArgsConstructor
@Document(collection = "prepaid_contract_audit")
public class PrepaidContractAudit {

    @Id
    private String id;

    private String prepaidContractOid;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD_HH_MM_SS)
    private Date contractStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD_HH_MM_SS)
    private Date contractEndDate;

    private int maxIDNCount;
    
    private int currentIDNCount;
    
    private int maxUserCount;
    
    private int currentUserCount;

    private String notes;
    
    private String pricingTierName;
    
    private boolean deleted;

    @CreatedDate
    private Date createdOn;

    @CreatedBy
    private String createdBy;
    
    /** The Unlimited Reps flag for the Contract */
    private boolean unlimitedReps;
    
    /**
     * Suppliers in a Prepaid Contract
     */
    private List<PrepaidContractSupplierDTO> suppliers;

}
