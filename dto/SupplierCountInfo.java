package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * @author sreedivya.s
 *
 */
@Data
public class SupplierCountInfo {

    /** supplier count in old model */
    private int oldPricingCount;

    /** supplier count in new pricing */
    private int newPricingCount;

    /** migrated supplier count */
    private int migratedCount;

    /** pilotSupplierCount **/
    private int pilotSupplierCount;

}
