package com.ghx.api.operations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 *
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTO {

    /**
     * Supplier Oid 
     */
    private String oid;
    
    /**
     * Supplier Fein
     */
    private String fein;
    
    /**
     * Supplier Legal Name
     */
    private String legalName;
}
