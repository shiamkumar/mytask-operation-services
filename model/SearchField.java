package com.ghx.api.operations.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author vijayakumar.s
 * @author Mari Muthu Muthukrishnan
 *
 * @since 10/JUNE/2022
 * 
 *        VO for searchFields in "audit_mapping" collection
 */
@Getter
@Setter
public class SearchField extends SearchProperties {
    
    /** Search Field Name */
    private String name;
    
    /** Search Field label */
    private String label;
    
    /** Search field display sequence */
    private int sequence;
    
    /** Inherit Properties from global field set */
    private boolean inheritProperties;
    
    /** maxLength property */
    private int maxLength;
    
    /** dataSource property */
    private String dataSource;
}
