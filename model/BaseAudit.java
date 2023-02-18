package com.ghx.api.operations.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Loganathan
 * Base audit collection to track the audit details.
 * 
 */
@Getter
@Setter
@Document(collection = "base_audit")
public class BaseAudit {

    @Id
    private String id;

    @CreatedBy
    @Field("createdBy")
    private String createdBy;

    @CreatedDate
    @Field("createdOn")
    private Date createdOn;

    @Field("auditType")
    private String auditType;

    @Field("details")
    private Map<String, Object> details = new HashMap();

}
