package com.ghx.api.operations.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ghx.api.operations.dto.PricingTierConfigDto;
import com.ghx.api.operations.model.PricingTierConfig;

/**
 * 
 * @author Rajasekar Jayakumar
 *
 */
public interface PricingConfigService {

    List<PricingTierConfigDto> getByType(String tierType, Pageable pageable);
    
    PricingTierConfigDto getById(String id);
    
    PricingTierConfigDto save(PricingTierConfig pricingTierConfig);

    PricingTierConfigDto update(String id, PricingTierConfig pricingTierConfig);

    List<PricingTierConfigDto> getActiveByType(String tierType);

}
