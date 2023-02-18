package com.ghx.api.operations.model;

import lombok.Getter;
import lombok.Setter;

/**
 * <p> The Merge Supplier Details </p>
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 03/05/2021
 * 
 */

@Getter
@Setter
public class MergeDetails {
    
    /**
     * Delete Supplier's User Count 
     */
    private int deleteSupplierUserCount;
    
    /**
     * Delete Supplier's IDN Count 
     */
    private int deleteSupplierIdnCount;
    
    /**
     * Delete Supplier's Pricing Tier Name
     */
    private String deleteSupplierTierName;
    
    /**
     * Delete Supplier's Pricing Tier Type
     */
    private String deleteSupplierTierType;
    
    /**
     * Delete Supplier's Pricing Tier Id
     */
    private String deleteSupplierTierId;
    
    /**
     * Retain Supplier's User Count
     */
    private int retainSupplierUserCount;
    
    /**
     * Delete Supplier's IDN Count
     */
    private int retainSupplierIdnCount;

    /**
     * Delete Supplier's Pricing Tier Name
     */
    private String retainSupplierTierName;
    
    /**
     * Retain Supplier's Pricing Tier Type
     */
    private String retainSupplierTierType;
    
    /**
     * Delete Supplier's Pricing Tier Id
     */
    private String retainSupplierTierId;
    
    /**
     * The common IDN count for Delete & Retain Supplier
     */
    private int commonIdnCount;
    
    /**
     * Number of Delete Supplier Users marked as 'UnPaid' 
     */
    private int usersMarkedAsUnPaid;
    
}
