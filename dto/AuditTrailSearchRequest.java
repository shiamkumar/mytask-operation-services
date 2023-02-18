package com.ghx.api.operations.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 10/JUNE/2022
 * 
 *        Search request DTO for audit-trail
 */
@Data
public class AuditTrailSearchRequest {

    /** Audit Type */
    private String type;
    
    /** Created On From Date */
    private Date createdOnFrom;
    
    /** Created On To Date*/
    private Date createdOnTo;
    
    /** Audit Created By */
    private String createdBy;
    
    /** List of search fields and properties */
    private List<AuditSearchFieldDTO> advancedSearch;
    
    /** Global Search fields and value */
    private GlobalSearchDTO globalSearch;
}
