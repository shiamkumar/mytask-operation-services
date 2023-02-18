package com.ghx.api.operations.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.enums.AuditFieldSearchType;

import lombok.Getter;
import lombok.Setter;


/***
 * Audit Search Field Details
 *  
 * @author Mari Muthu M
 * 
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchFieldDetails {

    /** Field acceptInput */
    private String acceptInput;

    /** Field value */
    private String value;

    /** Field searchFields */
    private String searchField;
    
    /** Search Type */
    private String searchType;
}
