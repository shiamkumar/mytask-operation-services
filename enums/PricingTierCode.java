package com.ghx.api.operations.enums;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since v1.1
 * @category enum
 */
public enum PricingTierCode {

    CREDIT_CARD_LOCAL("CCL"),
    CREDIT_CARD_STATE("CCS"),
    CREDIT_CARD_REGIONAL("CCR"),
    CREDIT_CARD_NATIONAL("CCN"),
    PREPAID_LOCAL("PPL"),
    PREPAID_STATE("PPS"),
    PREPAID_REGIONAL("PPR"),
    PREPAID_NATIONAL("PPN");
    
    /** Pricing Tier Code */
    private String code;
    
    /**
     * Returns the Pricing Tier code 
     * @return
     */
    public String getCode() {
        return this.code;
    }
    
    PricingTierCode(String tierCode) {
        this.code = tierCode;
    }

}