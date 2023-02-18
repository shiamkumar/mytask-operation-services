package com.ghx.api.operations.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 06/10/2022
 * 
 *        DTO for audit-trail
 */
@Data
public class AuditTrailDTO {
    /** Audit Type */
    private String auditType;
    
    /**Audit SubType */
    private String auditSubType;
    
    /** Audit Created On */
    private Date createdOn;
    
    /** Audit Created By */
    private String createdBy;
    
    /** Details */
    private Map<String, Object> details;
    
    /** Matched Queries*/
    private List<String> matchedQueries;
}
