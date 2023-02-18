package com.ghx.api.operations.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 07/21/2021
 *
 */

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@IdClass(PrepaidContractSuppliersPK.class)
@Table(name = "prepaid_contract_suppliers")
public class PrepaidContractSuppliers {

    /**
     * The prepaid contract oid
     */
    @Id
    @Column(name="prepaid_contract_oid")
    private String prepaidContractOid;
    
    /**
     * Supplier Fein
     */
    @Id
    @Column(name = "fein")
    private String fein;
    
    /**
     * Supplier Name
     */
    @Column(name = "supplier_name")
    private String supplierName;
}
