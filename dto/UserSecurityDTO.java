package com.ghx.api.operations.dto;

import java.sql.Timestamp;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSecurityDTO {
	
	private String oid;
	
	private String firstName;
	
	private String lastName;
	
	private String userType;
	
	private String password;
	
	private String userId;
	
	private String userStatusCode;
	
	private Timestamp passwordExpDate;
	
	private Set<RoleDTO> roles;

}
