package com.ghx.api.operations.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 
 * @author vijayakumar.s
 * @author Mari Muthu Muthukrishnan
 *
 * @since 10/JUNE/2022
 * 
 *        VO for renderProperties in "audit_fields" collection
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenderProperties {

    /** Sort applicable property */
    private boolean sort;

    /** Search allowed */
    private boolean search;

    /** Type of the data to display */
    private String type;

    /** To display data as hyperlink */
    private boolean displayHyperlink;

    /** Navigate View - incase of display as hyperlink */
    private String navigateView;
    
    /**
     * maxLength property
     */
    private int maxLength;
    
    /** Sort Field */
    private String sortField;
}
