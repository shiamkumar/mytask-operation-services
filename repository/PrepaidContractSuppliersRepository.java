package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ghx.api.operations.model.PrepaidContractSuppliers;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 07/21/2020
 *
 */

@Primary
@Repository
@Transactional
public interface PrepaidContractSuppliersRepository extends JpaRepository<PrepaidContractSuppliers, String> {

    /**
     * Delete suppliers from contract
     * @param feins
     * @param contract oid
     */
    @Modifying
    @Query("DELETE FROM PrepaidContractSuppliers pcs WHERE pcs.fein in (?1) and pcs.prepaidContractOid = ?2 ")
    int deleteByFeins(List<String> feins, String oid);
}
