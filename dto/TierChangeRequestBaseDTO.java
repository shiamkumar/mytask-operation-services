package com.ghx.api.operations.dto;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ghx.api.operations.enums.TierChangeRequestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category DTO
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TierChangeRequestBaseDTO {
    
    /** Supplier Details - Name, Fein and Oid */
    private SupplierDTO supplier;
    
    /** Tier Change Request Type */
    @Enumerated(EnumType.STRING)
    private TierChangeRequestType type;
    
    /** Request Notes */
    private String notes;
    
    /** Tier Change Requested Pricing Tier Code */
    private String requestedTierCode;
    
    /** List of IDNs to be eliminated for a Tier Change */
    private List<String> eliminatedIdns;
 
}
