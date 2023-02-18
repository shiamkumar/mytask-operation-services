package com.ghx.api.operations.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author biju.m
 *
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenderDetails {
	
	/** Field hyperLinkPermission */
	private String hyperLinkPermission;
}
