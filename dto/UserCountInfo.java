package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * @author sreedivya.s
 *
 */
@Data
public class UserCountInfo {

    /** migrated paid user count */
    private int migratedPaidCount;
    
    /** migrated prepaid paid user count */
    private int migratedPrepaidPaidCount;

    /** migrated unpaid user count */
    private int migratedUnpaidCount;

    /** paid user count in new pricing */
    private int newPricingPaidCount;
    
    /** prepaid paid user count in new pricing */
    private int newPricingPrepaidPaidCount;

    /** unpaid user count in new pricing */
    private int newPricingUnpaidCount;

    /** migrated user count */
    private int migratedSupplierUsersCount;

}
