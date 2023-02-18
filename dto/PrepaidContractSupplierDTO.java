package com.ghx.api.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The DTO for prepaid_contract_suppliers
 * @author Sundari V
 */

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrepaidContractSupplierDTO {

    /** the fein */
    private String fein;

    /** the user count */
    private String userCount;

    /** the idn count */
    private String idnCount;
    
    /** contractExists */
    private boolean contractExists;

    /** oid */
    private String oid;

    /** tier plan code */
    private String supplierTierPlanCode;

    /** legal Name */
    private String legalName;

}
