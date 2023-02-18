package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * @author sreedivya
 *
 */
@Data
public class SupplierStatisticsDTO {

    /** current statistics details */
    private MigrationStatisticsDTO currentStatistics;

    /** previous statistics details */
    private MigrationStatisticsDTO previousStatistics;

}
