package com.ghx.api.operations.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * BeforeMigrationRequest class contains the pojo before the migration
 * @author Ajith
 *
 */
@Data
@JsonIgnoreProperties(value = { "inactiveVcs", "inactiveUsers" }, allowSetters = true)
public class BeforeMigrationRequest {

    /** activeVcs */
    private int activeVcs;

    /** inactiveVcs */
    private int inactiveVcs;

    /** rfpmtVcs */
    private int rfpmtVcs;

    /** activeUsers */
    private int activeUsers;

    /** inactiveUsers */
    private int inactiveUsers;

}
