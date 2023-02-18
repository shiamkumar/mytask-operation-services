package com.ghx.api.operations.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;
/**
 * 
 * @author Ajith
 *
 */
@Data
public class SubscriptionRequestDTO {
    
    /** parent Oid */
    private String parentOid;

    /** The date Of Purchase */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern")
    private Date dateOfPurchase;

    /** The date Of Expiry */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY, timezone = "US/Eastern")
    private Date dateOfExpiry;

    /** plan Oid */
    private String planOid;

    /** subscription plan */
    private String subscriptionPlan;


}
