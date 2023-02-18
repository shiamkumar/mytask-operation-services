package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * 
 * @author Ajith
 *
 */
@Data
public class PricingMigrationDTO {

    /** migrationFileKey */
    private String migrationFileKey;

    /** submitOnError */
    private boolean submitOnError;

}
