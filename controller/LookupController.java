package com.ghx.api.operations.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.LookupDTO;
import com.ghx.api.operations.service.LookupService;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * The Class LookupController.
 */
@RestController
@Validated
@RequestMapping("/v1/lookups")
public class LookupController {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(LookupController.class);

    @Autowired
    private transient LookupService lookupService;

    @GetMapping
    public ResponseEntity<LookupDTO> findByCategoryAndCodeAndParentId(@RequestParam String category, @RequestParam String code,
            @RequestParam(required = false) String parentId) {
        LOGGER.info("LookupController findByCategoryAndCodeAndParentId start {} ", System.currentTimeMillis());
        return new ResponseEntity<>(lookupService.findByCategoryAndCodeAndParentId(category, code, parentId), HttpStatus.OK);
    }

    @GetMapping(path = "/category")
    public ResponseEntity<Map<String, List<LookupDTO>>> findByCategories(@RequestParam String categories) {
        LOGGER.info("LookupController findByCategories start {} ", System.currentTimeMillis());
        return new ResponseEntity<>(lookupService.findByCategories(categories), HttpStatus.OK);
    }
    
    @GetMapping(path = "/description")
    public ResponseEntity<LookupDTO> findByCategoryAndDescription(@RequestParam String category, @RequestParam String description) {
        LOGGER.info("LookupController findByCategoryAndDescription start {} ", System.currentTimeMillis());
        return new ResponseEntity<>(lookupService.findByCategoryAndDescription(category, description), HttpStatus.OK);
    }
}
