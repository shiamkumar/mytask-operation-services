package com.ghx.api.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
/**
 * 
 * @author Ajith
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionResponse {
    
    /** plan Oid */
    private String planOid;
}
