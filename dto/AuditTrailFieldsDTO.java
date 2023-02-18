package com.ghx.api.operations.dto;

import java.util.List;

import com.ghx.api.operations.model.RenderField;
import com.ghx.api.operations.model.SearchField;

import lombok.Data;

/**
 * 
 * @author vijayakumar.s
 * 
 * @since 10/JUNE/2022
 * 
 *        DTO for audit-trail fields
 */
@Data
public class AuditTrailFieldsDTO {
    
    /** Audit Type */
    private String type;
    
    /** Render Fields*/
    private List<RenderField> renderFields;
    
    /** Audit Search Fields */
    private List<SearchField> searchFields;
}
