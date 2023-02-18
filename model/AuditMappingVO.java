package com.ghx.api.operations.model;

import java.util.List;

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
 *        VO for "audit_mapping" collection
 */
@Getter
@Setter
@Document(collection = "audit_mapping")
public class AuditMappingVO {
    
    /** Audit Mapping Id */
    @Id
    private String id;
    
    /** Audit Type */
    private String type;
    
    /** Audit Name */
    private String name;
    
    /** Render Fields*/
    private List<RenderField> renderFields;
    
    /** Search Fields */
    private List<SearchField> searchFields;
}
