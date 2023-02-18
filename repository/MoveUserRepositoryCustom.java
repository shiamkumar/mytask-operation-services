package com.ghx.api.operations.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * This Interface MoveUserRepositoryCustom
 * @author Ananth kandasamy
 *
 */
public interface MoveUserRepositoryCustom {

    /**
     * find domain by vendor oid
     * @param vendorOid
     * @return
     */
    List<String> findDomainByVendorOid(String vendorOid);

    /**
     * find all move requests
     * @param searchRequest
     * @param pageable
     * @return
     */
    List<MoveUserRequestDTO> findAllMoveRequests(SearchRequest searchRequest, Pageable pageable);

    /**
     * find count of move requests
     * @param searchRequest
     * @return
     */
    long findMoveRequestsCount(SearchRequest searchRequest);

}
