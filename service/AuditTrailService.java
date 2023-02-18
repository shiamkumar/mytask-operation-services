package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.AuditExportDTO;
import com.ghx.api.operations.dto.AuditTrailFieldsDTO;
import com.ghx.api.operations.dto.AuditTrailSearchRequest;
import com.ghx.api.operations.dto.AuditTrailSearchResponse;
import com.ghx.api.operations.dto.AuditTypeDTO;

/**
 * 
 * @author Vijayakumar S
 * 
 * @since 06/09/2022
 * 
 *        Service layer for audit-trail
 *
 */
public interface AuditTrailService {


    /**
     * 
     * @param auditTrailSearchRequest
     * @param pageable
     * @return
     */
    AuditTrailSearchResponse getAuditTrails(AuditTrailSearchRequest auditTrailSearchRequest, Pageable pageable);

    /**
     * 
     * @param sourceType
     * @return
     */
    AuditTrailFieldsDTO getFields(String sourceType);

    /**
     * 
     * @param searchRequest
     * @return
     */
    Map<String, Object> getAllAuditTypes(AuditTypeDTO searchRequest, Pageable pageable);

    /**
     * 
     * @param resourceLoader
     * @param auditTrailSearchRequest
     * @param exportType
     * @param response
     * @param pageable
     * @return 
     * @throws IOException
     */
    AuditExportDTO exportAuditTrailsReports(ResourceLoader resourceLoader, AuditTrailSearchRequest auditTrailSearchRequest, String exportType,
            HttpServletResponse response, Pageable pageable) throws IOException;

}
