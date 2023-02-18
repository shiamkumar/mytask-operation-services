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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Move user request details for collection
 * 
 * @author Ananth Kandasamy
 *
 */
@Data
@NoArgsConstructor
@Document(collection = "move_user_request")
public class MoveUserRequest implements Persistable<String> {

    /** id */
    @Id
    private String id;

    /**
     * The Sales Force Identifier for the request
     */
    private String salesforceId;

    /** move user status */
    private String status;

    /** notes */
    private String notes;

    /** user Oid */
    private String userOid;

    /** email id */
    private String emailId;

    /** updated new email id */
    private String updatedEmailId;

    /** user name */
    private String name;

    /** acknowledge add domain */
    private boolean acknowledgeAddDomain;

    /** retain idns */
    private String[] retainIdns;

    /** source supplier detail */
    private SupplierDTO sourceSupplier;

    /** destination supplier detail */
    private SupplierDTO destinationSupplier;

    /** error message */
    private String errorMessage;

    /** processed on */
    private Date processedOn;

    /** created date */
    @CreatedDate
    private Date createdOn;

    /** created user */
    @CreatedBy
    private String createdBy;

    /** last modified date */
    @LastModifiedDate
    private Date updatedOn;

    /** last modified user */
    @LastModifiedBy
    private String updatedBy;
    
    /**Move details */
    private MoveDetails moveDetails;

    /** persisted */
    @Transient
    private boolean persisted;

    @Override
    public boolean isNew() {
        return persisted;
    }
	
}
