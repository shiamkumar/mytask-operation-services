package com.ghx.api.operations.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
@Table(name = "role")
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "oid")
	private String oid;
	@Column(name = "role_name")
	private String roleName;
	@Column(name = "immutable")
	private boolean immutable;
	
	@Column(name = "type")
	private String type;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "role_oid") }, inverseJoinColumns = {
			@JoinColumn(name = "user_oid") })
	private Set<UserSecurity> users;

}
