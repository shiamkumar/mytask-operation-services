package com.ghx.api.operations.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * AfterMigrationRequest class contains the pojo after the migration
 * @author Ajith
 *
 */
@Data
@JsonIgnoreProperties(value = { "inactiveVcs", "inactiveUsers" }, allowSetters = true)
public class AfterMigrationRequest {

    /** activeVcs */
    private int activeVcs;

    /** inactiveVcs */
    private int inactiveVcs;

    /** activeUsers */
    private int activeUsers;

    /** inactiveUsers */
    private int inactiveUsers;

}
