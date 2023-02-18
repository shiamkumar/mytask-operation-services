package com.ghx.api.operations.repository;

import java.sql.Timestamp;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ghx.api.operations.model.PrepaidContract;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * The PrepaidContract Repository
 *
 */
@Primary
@Repository
@Transactional
public interface PrepaidContractRepository extends JpaRepository<PrepaidContract, String>, PrepaidContractRepositoryCustom {

    PrepaidContract findByOidAndDeleted(String oid, boolean deleted);

    /**
     * @param oid
     * @param deleted
     * @param oasContract
     * @param updatedBy
     * @param updatedOn
     * @return
     */
    @Modifying
    @Query("UPDATE PrepaidContract c SET c.deleted = :deleted, c.oasContract= :oasContract, updatedBy =:updatedBy , updatedOn =:updatedOn WHERE c.oid = :oid")
    int updatePrepaidContract(@Param("oid") String oid, @Param("deleted") boolean deleted, @Param("oasContract") boolean oasContract ,@Param("updatedBy") String updatedBy,@Param("updatedOn") Timestamp updatedOn);
    
}
