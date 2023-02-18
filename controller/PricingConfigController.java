package com.ghx.api.operations.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.PricingTierConfigDto;
import com.ghx.api.operations.model.PricingTierConfig;
import com.ghx.api.operations.service.PricingConfigService;

/**
 * @author Rajasekar Jayakumar
 * Controller class contains all the api details of pricing configuration.
 * 
 */
@RestController
@Validated
@RequestMapping("/v1/pricing/configurations")
public class PricingConfigController {

    @Autowired
    private PricingConfigService pricingConfigService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<PricingTierConfigDto>> getByType(@RequestParam String tierType, Pageable pageable) {
        return new ResponseEntity<>(pricingConfigService.getByType(tierType, pageable), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public ResponseEntity<PricingTierConfigDto> getById(@PathVariable String id) {
        return new ResponseEntity<>(pricingConfigService.getById(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PricingTierConfigDto> save(@RequestBody PricingTierConfig pricingConfig) {
        return new ResponseEntity<>(pricingConfigService.save(pricingConfig), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/{id}")
    public ResponseEntity<PricingTierConfigDto> update(@PathVariable String id, @RequestBody PricingTierConfig pricingConfig) {
        return new ResponseEntity<>(pricingConfigService.update(id, pricingConfig), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/active")
    public ResponseEntity<List<PricingTierConfigDto>> getActiveByType(@RequestParam String tierType) {
        return new ResponseEntity<>(pricingConfigService.getActiveByType(tierType), HttpStatus.OK);
    }
}
