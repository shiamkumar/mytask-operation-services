package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * @author sreedivya
 *
 */
@Data
public class BadgeCountInfo {

    /** migrated badge printed user count */
    private int badgePrintedMigratedUsersCount;

    /** new pricing badge printed user count */
    private int badgePrintedNewUsersCount;
    
    /** yet to badge migrated user count */
    private int yetToBadgeMigratedUsersCount;

    /** yet to badge new user count */
    private int yetToBadgeNewUsersCount;
    
    /** badge prepaid Printed Migrated Users Count */
    private int badgedPPMigratedUsersCount;
    
    /** badge prepaid Printed New Users Count */
    private int badgedPPNewUsersCount;
    
    /** yet to badge migrated prepaid Users Count */
    private int yetToBadgePPMigratedUsersCount;
    
    /** yet to badge prepaid New Users Count */
    private int yetToBadgePPNewUsersCount;

}
