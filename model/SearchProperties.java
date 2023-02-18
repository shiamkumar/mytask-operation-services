package com.ghx.api.operations.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 10/JUNE/2022
 * 
 *        VO for searchProperties in "audit_fields" collection
 */
@Getter
@Setter
public class SearchProperties {
    
    /** Search Type */
    private String type;
    
    /** Search Details */
    private SearchFieldDetails details;
}
