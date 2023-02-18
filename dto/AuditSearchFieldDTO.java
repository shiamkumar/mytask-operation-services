package com.ghx.api.operations.dto;

import com.ghx.api.operations.enums.AuditFieldSearchType;

import lombok.Data;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * 
 * @since 06/13/2022
 * 
 * Audit Search Field
 *
 */
@Data
public class AuditSearchFieldDTO {

    /** The Search Field Name */
    private String fieldName;
    
    /**
     * The Search Field Value
     */
    private Object fieldValue;
    
    /** Type of Search to be included */
    private AuditFieldSearchType searchType;
}
