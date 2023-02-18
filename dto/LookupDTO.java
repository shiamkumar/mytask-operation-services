package com.ghx.api.operations.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Value object for LookupDTO to encapsulate the business data
 * 
 * @author Nagarajan G
 *
 */

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String category;

    private String description;

    private String parentOid;
    
    private Integer seq;

    public String toString() {
        return "Category: " + category + ",description: " + description;
    }

}
