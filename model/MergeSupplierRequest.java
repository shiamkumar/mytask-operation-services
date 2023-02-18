package com.ghx.api.operations.model;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ghx.api.operations.dto.SupplierDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p> Merge Supplier Request </p>
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 *  
 */

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "merge_supplier_request")
public class MergeSupplierRequest implements Persistable<String> {

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
    
    /**
     * Merge Request Processed Date+
     */
    private Date processedOn;
    
    @CreatedDate
    private Date createdOn;

    @CreatedBy
    private String createdBy;
    
    @LastModifiedDate
    private Date updatedOn;

    @LastModifiedBy
    private String updatedBy;

    private String errorMessage;

    @Transient
    private boolean persisted;

    @Override
    public boolean isNew() {
        return persisted;
    }
}
