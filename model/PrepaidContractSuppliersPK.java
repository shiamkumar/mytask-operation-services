package com.ghx.api.operations.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 07/21/2021
 *
 */

@Data
@Embeddable
@NoArgsConstructor
@AttributeOverrides({ @AttributeOverride(name = "prepaidContractOid", column = @Column(name = "prepaid_contract_oid")),
        @AttributeOverride(name = "fein", column = @Column(name = "fein")) })
public class PrepaidContractSuppliersPK  implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4347106340307177694L;

    /**
     * The prepaid contract oid
     */
    private String prepaidContractOid;

    /**
     * Supplier Fein
     */
    private String fein;

}
