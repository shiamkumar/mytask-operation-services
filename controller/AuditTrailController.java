package com.ghx.api.operations.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.AuditExportDTO;
import com.ghx.api.operations.dto.AuditTrailFieldsDTO;
import com.ghx.api.operations.dto.AuditTrailSearchRequest;
import com.ghx.api.operations.dto.AuditTrailSearchResponse;
import com.ghx.api.operations.dto.AuditTypeDTO;
import com.ghx.api.operations.service.AuditTrailService;
import com.ghx.api.operations.util.ConstantUtils;

/**
 * 
 * @author Vijayakuamr S
 * @author Mari Muthu Muthukrishnan
 * 
 * @since 06/09/2022
 * 
 *        Controller for audit-trail
 *
 */
@RestController
@RequestMapping("/v1/audittrail")
public class AuditTrailController {

    /** AuditTrail Service */
    @Autowired
    private AuditTrailService auditTrailService;

    /** ResourceLoader */
    @Autowired
    private transient ResourceLoader resourceLoader;


    /**
     * 
     * @param auditTrailSearchRequest
     * @param pageable
     * @return
     */
    @PostMapping
    public ResponseEntity<AuditTrailSearchResponse> getAuditTrails(@RequestBody AuditTrailSearchRequest auditTrailSearchRequest, Pageable pageable) {
        return new ResponseEntity<>(auditTrailService.getAuditTrails(auditTrailSearchRequest, pageable), HttpStatus.OK);
    }


    /**
     * 
     * @param type
     * @return
     */
    @GetMapping("/fields")
    public ResponseEntity<AuditTrailFieldsDTO> getFields(@RequestParam String type) {
        return new ResponseEntity<AuditTrailFieldsDTO>(auditTrailService.getFields(type), HttpStatus.OK);
    }

    /**
     * 
     * @param type
     * @param name
     * @param pageable
     * @return
     */
    @GetMapping("/audittypes")
    public ResponseEntity<Map<String, Object>> getAllAuditTypes(@RequestParam(required = false) String type,
            @RequestParam(required = false) String name, Pageable pageable) {
        AuditTypeDTO searchRequest = AuditTypeDTO.builder().type(type).name(name).build();
        return new ResponseEntity<>(auditTrailService.getAllAuditTypes(searchRequest, pageable), HttpStatus.OK);
    }

    /**
     * 
     * @param auditTrailSearchRequest
     * @param exportType
     * @param response
     * @param pageable
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/export/{type}")
    public ResponseEntity<byte[]> exportAuditTrailsReports(@RequestBody AuditTrailSearchRequest auditTrailSearchRequest, @PathVariable String type,
            HttpServletResponse response, Pageable pageable) throws IOException {
        AuditExportDTO auditExportDTO = auditTrailService.exportAuditTrailsReports(resourceLoader, auditTrailSearchRequest, type, response, pageable);
        response.setHeader(ConstantUtils.CONTENT_DISPOSITION, "attachment; filename=\"" + auditExportDTO.getContentDisposition() + "\"");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(auditExportDTO.getContentType())).body(auditExportDTO.getExportData());
    }
}
