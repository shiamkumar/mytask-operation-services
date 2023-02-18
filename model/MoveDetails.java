package com.ghx.api.operations.model;


import lombok.Data;

/**
 * This class is MoveDetails. it contains move user additionl details.
 * @author Ananth Kandasamy
 *
 */
@Data
public class MoveDetails {

    /** rep idn count */
    private int repIdnCount;

    /** rep paid pricing tier */
    private String repPaidPricingtier;

    /** rep renewal date */
    private String repRenewalDate;

    /** rep paid status */
    private String repPaidStatus;

    /** source supplier total user count */
    private int sourceSupplierUserCount;

    /** Source supplie total IDN count */
    private int sourceSupplierIDNCount;

    /** Source supplie Fein */
    private String sourceSupplierFein;

    /** Source supplie Pricing Tier */
    private String sourceSupplierPricingTier;

    /** Destinatino Supplier Total User Cout */
    private int destSupplierUserCount;

    /** Destinatino Supplier Total IDN Count */
    private int destSupplierIDNCount;

    /** Destinatino Supplier Fein */
    private String destSupplierFein;

    /** Destinatino Supplier pricing Tier */
    private String destSupplierPricingTier;

}
