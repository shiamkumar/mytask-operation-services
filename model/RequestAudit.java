package com.ghx.api.operations.model;

import java.util.Date;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
public class RequestAudit {

    /** The migrationFileKey */
    private String migrationFileKey;

    /** The status */
    private String status;

    /** The notes */
    private String notes;

    /** createdOn */
    private Date createdOn;

    /** createdBy */
    private String createdBy;

    /** updatedOn */
    private Date updatedOn;

    /** updatedBy */
    private String updatedBy;

}
