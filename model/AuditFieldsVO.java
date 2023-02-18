package com.ghx.api.operations.model;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author vijayakumar.s
 *
 * @since 10/JUNE/2022
 * 
 *        VO for "audit_fields" collection
 */
@Getter
@Setter
@Document(collection = "audit_fields")
public class AuditFieldsVO {
    
    /** Audit Id */
    @Id
    private String id;
    
    /** Audit Field Name*/
    private String fieldName;
    
    /** Audit Field Label to display */
    private String label;
    
    /** AuditField Enable property */
    private boolean enable;
    
    /** Audit Field Render Properties */
    private RenderProperties renderProperties;
    
    /** Audit Field Search Properties */
    private SearchProperties searchProperties;
}
