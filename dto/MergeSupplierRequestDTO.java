package com.ghx.api.operations.dto;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.model.MergeDetails;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 *
 */

@Getter
@Setter
public class MergeSupplierRequestDTO {

    @Id
    private String id;

    /**
     * The Sales Force Identifier for the request
     */
    private String salesforceId;
    
    /**
     * Merge Request Status
     */
    private String status;
    
    /**
     * Merge Request Notes
     */
    private String notes;
    
    /**
     * Delete Supplier Details
     */
    private SupplierDTO deleteSupplier;
    
    /**
     * Retain Supplier Details
     */
    private SupplierDTO retainSupplier;

    /**
     * Merge Supplier Details 
     */
    private MergeDetails mergeDetails;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date createdOn;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date processedOn;

    private String createdBy;

    private String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String retainSupplierOid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deleteSupplierOid;
}
