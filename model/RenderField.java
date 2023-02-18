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
 *        VO for renderFields in "audit_mapping" collection
 */
@Getter
@Setter
public class RenderField extends RenderProperties {
    
    /** Feilds */
    private String fields;
    
    /** Field Label  */
    private String label;
    
    /** Field Datatype */
    private String dataType;
    
    /** field sequence to display in Grid */
    private int sequence;
    
    /** Inherit properties */
    private boolean inheritProperties;
    
    /** Field details */
    private RenderDetails details;
}
