package com.ghx.api.operations.dto;

import java.util.Date;

import lombok.Data;

/**
 * @author sreedivya.s
 *
 */
@Data
public class MigrationStatisticsDTO {

    /** supplier count info */
    private SupplierCountInfo suppliers;

    /** user count info */
    private UserCountInfo users;

    /** badge count info */
    private BadgeCountInfo badge;

    /** created on */
    private Date createdOn;

    /** created by */
    private String createdBy;

}
