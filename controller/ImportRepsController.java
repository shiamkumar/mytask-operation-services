package com.ghx.api.operations.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.ImportRepsDTO;
import com.ghx.api.operations.dto.RepDetailsDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierDetailsDTO;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.service.ImportRepsService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ReportUtils;

/**
 *
 * @author Ajith
 *
 */
@RestController
@Validated
@RequestMapping("/v1")
public class ImportRepsController {

    /**
     * The ImportRepsService
     */
    @Autowired
    private transient ImportRepsService importRepsService;

    /** the Resource Loader */
    @Autowired
    private transient ResourceLoader resourceLoader;
    
    /**
     *
     * @param readExcelDataFile
     * @param vcRelationOid
     * @return Map<String, Object>
     */
    @PostMapping("/idns/{id}/importreps")
    @LogExecutionTime
    public ResponseEntity<SupplierDetailsDTO> importReps(@PathVariable String id, @RequestBody ImportRepsDTO importRepsDTO) {
        return new ResponseEntity<>(importRepsService.importReps(id,importRepsDTO), HttpStatus.CREATED);
    }

    /**
     *
     * @param vcRelationOid
     * @return Map<String, Object>
     */
    @GetMapping("/idns/{id}/importreps")
    @LogExecutionTime
    public ResponseEntity<Object> getImportRepRequest(@PathVariable String id, @RequestParam(required = false) String emailId,
            @RequestParam(required = false) String salesforceId, @RequestParam(required = false) String status, Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.builder().emailId(emailId).oid(id).salesforceId(salesforceId).status(status).build();
        
        return new ResponseEntity<>(importRepsService.getImportRepRequests(searchRequest, pageable), HttpStatus.OK);
    }

    /**
     *
     * @param vcRelationOid
     * @return Map<String, Object>
     */
    @GetMapping("/idns/{id}/importreps/{importRepRequestId}")
    @LogExecutionTime
    public ResponseEntity<Object> getImportRequestUserDetails(@PathVariable String id, @PathVariable String importRepRequestId,
            @RequestParam(required = false) String email, @RequestParam(required = false) String status, Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.builder().emailId(email).oid(id).importRequestId(importRepRequestId).status(status).build();

        return new ResponseEntity<>(importRepsService.getImportRequestUserDetails(searchRequest, pageable), HttpStatus.OK);
    }
    
    /**
     *
     * @param vcRelationOid
     * @return
     * @return Map<String, Object>
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/idns/{id}/importreps/{importRepRequestId}/export")
    @LogExecutionTime
    public <T> void exportImportRequestUserDetails(@RequestParam String exportType, HttpServletResponse response, @PathVariable String id,
            @PathVariable String importRepRequestId, @RequestParam(required = false) String email, @RequestParam(required = false) String status,
            Pageable pageable) throws IOException {
        SearchRequest searchRequest = SearchRequest.builder().emailId(email).oid(id).importRequestId(importRepRequestId).status(status).build();

        List<T> importrepuserdetailList = (List<T>) Arrays.asList(new ObjectMapper().convertValue(
                importRepsService.getImportRequestUserDetails(searchRequest, pageable).get(ConstantUtils.IMPORT_REP_USER_DETAIL),
                RepDetailsDTO[].class));
        Map<String, Object> parameter = new HashMap<>();
        parameter.put(ConstantUtils.JASPER_STATUS, status != null ? status : ConstantUtils.ALL);
        ReportUtils.export(exportType, importrepuserdetailList, response, this.resourceLoader, ConstantUtils.FILENAME_IMPORT_REP_USER_DETAIL,
                parameter);

    }
}
