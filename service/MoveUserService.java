package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;

/**
 * This interface MoveUserService
 * 
 * @author Ananth Kandasamy
 *
 */
public interface MoveUserService {

    /**
     * create move user request
     * 
     * @param id
     * @param moveUserRequestDTO
     * @return
     */
    String createMoveUserRequest(String id, MoveUserRequestDTO moveUserRequestDTO);

    /**
     * Returns the List of Move User Requests matching the Search Criteria
     * @param searchRequest
     * @param pageable
     * @return map
     */
    Map<String, Object> getAllMoveRequests(SearchRequest searchRequest, Pageable pageable);


    /**
     * Export Move User requests, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param searchRequest
     * @param response
     * @throws IOException
     */
    void exportMoveUserRequests(String exportType, SearchRequest searchRequest, HttpServletResponse response) throws IOException;
}
