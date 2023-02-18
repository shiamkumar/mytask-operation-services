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
@Table(name = "acl_module")
public class Module implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name = "oid")
    private String oid;
    @Column(name = "name")
    private String moduleName;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "acl_user_module", joinColumns = { @JoinColumn(name = "module_oid") }, inverseJoinColumns = { @JoinColumn(name = "user_oid") })
    private Set<UserSecurity> users;

}
