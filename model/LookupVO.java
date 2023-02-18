package com.ghx.api.operations.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


/**
 * Value object for Lookup to encapsulate the business data.
 *
 * @author Krishnan M
 */
@Entity
@Table(name = "lookup")
@IdClass(LookupPK.class)
@Getter
@Setter
public class LookupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "code")
    private String code;

    @Id
    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "deprecated")
    private Boolean deprecated;

    @Column(name = "parent_oid")
    private String parentOid;
}
