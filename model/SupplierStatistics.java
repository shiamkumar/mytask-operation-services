package com.ghx.api.operations.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.ghx.api.operations.dto.MigrationStatisticsDTO;

import lombok.Data;

/**
 * The Class SupplierStatistics.
 */
@Data
@Document(collection = "supplier_statistics")
public class SupplierStatistics {

    /** The id */
    private String id;

    /** current statistics details */
    private MigrationStatisticsDTO currentStatistics;

    /** previous statistics details */
    private MigrationStatisticsDTO previousStatistics;

}
