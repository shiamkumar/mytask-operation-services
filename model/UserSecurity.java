package com.ghx.api.operations.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Loganathan.M
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_vm")
public class UserSecurity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "oid")
	private String oid;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "user_type_code")
	private String userType;
	@Column(name = "password")
	private String password;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "user_status_code")
	private String userStatusCode;
	@Column(name = "password_exp_date")
	private Timestamp passwordExpDate;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_oid") }, inverseJoinColumns = {
			@JoinColumn(name = "role_oid") })
	private Set<Role> roles;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "acl_user_module", joinColumns = { @JoinColumn(name = "user_oid") }, inverseJoinColumns = {
			@JoinColumn(name = "module_oid") })
	private Set<Module> modules;

}
