package com.ghx.api.operations.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 10/JUNE/2022
 * 
 *        Search response DTO for audit-trail
 */
@Data
public class AuditTrailSearchResponse {

    /** Audit Trail Records */
    private List<Map<String, Object>> auditRecords;
    
    /** Total Number of Records for the Audit Result */
    private long totalRecords;
}
