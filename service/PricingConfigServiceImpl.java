package com.ghx.api.operations.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.PricingTierConfigDto;
import com.ghx.api.operations.model.PricingTierConfig;
import com.ghx.api.operations.repository.PricingConfigRepository;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.DateUtils;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.validation.business.PricingBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

import ma.glasnost.orika.MapperFacade;

/**
 * @author Rajasekar Jayakumar
 * 
 *         Pricing config service class to call database operations.
 * 
 */
@Component
public class PricingConfigServiceImpl implements PricingConfigService {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(PricingConfigServiceImpl.class);

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private PricingConfigRepository pricingConfigRepository;

    @Autowired
    private PricingBusinessValidator pricingBusinessValidator;

    @Autowired
    private OperationsUtil operationsUtil;

    @Override
    public List<PricingTierConfigDto> getByType(String tierType, Pageable pageable) {
        Pageable pageableSort = PageRequest.of(ConstantUtils.PAGE_START_INDEX, ConstantUtils.PAGE_END_INDEX,
                Sort.by(ConstantUtils.TIER_SEQ).ascending().and(Sort.by(ConstantUtils.EFFECTIVE_FROM).ascending()));
        return pricingConfigRepository.findByTierType(tierType, DateUtils.getISODate(new Date(), ConstantUtils.UTC), pageableSort).stream()
                .map(plan -> mapper.map(plan, PricingTierConfigDto.class)).collect(Collectors.toCollection(ArrayList::new));
    }

    public PricingTierConfigDto getById(String id) {
        return mapper.map(pricingConfigRepository.findById(id).get(), PricingTierConfigDto.class);
    }

    @Override
    public PricingTierConfigDto save(PricingTierConfig pricingTierConfig) {
        if (Objects.nonNull(pricingTierConfig)) {
            pricingBusinessValidator.validatePricingTierConfig(pricingTierConfig);
        }
        pricingBusinessValidator.getValidatedTier(pricingTierConfig);
        PricingTierConfigDto pricingConfigDto = mapper.map(pricingConfigRepository.insert(pricingTierConfig), PricingTierConfigDto.class);
        if (pricingConfigDto.getId() != null) {
            operationsUtil.saveAuditTrial(pricingConfigDto.getId(), pricingTierConfig);
            updateExpiredonColumn(pricingConfigDto.getId(), pricingTierConfig);
        }
        return pricingConfigDto;
    }

    @Override
    public PricingTierConfigDto update(String id, PricingTierConfig pricingTierConfig) {

        PricingTierConfig dbPricingTierConfig = pricingBusinessValidator.validateTier(id, pricingTierConfig.getTierName());
        pricingBusinessValidator.validateInputParams(pricingTierConfig, dbPricingTierConfig);
        pricingBusinessValidator.validatePricingTierConfig(pricingTierConfig);
        pricingBusinessValidator.getValidatedTier(pricingTierConfig);
        PricingTierConfigDto pricingConfigDto = mapper.map(pricingConfigRepository.save(pricingTierConfig), PricingTierConfigDto.class);
        if (pricingConfigDto.getId() != null) {
            pricingTierConfig.setId(id);
            updateExpiredonColumn(id, pricingTierConfig);
        }
        operationsUtil.saveAuditTrial(id, pricingTierConfig);
        return pricingConfigDto;
    }

    private void updateExpiredonColumn(String id, PricingTierConfig pricingTierConfig) {
        Pageable pageable = PageRequest.of(ConstantUtils.PAGE_START_INDEX, ConstantUtils.PAGE_END_INDEX,
                Sort.by(ConstantUtils.TIER_SEQ).descending().and(Sort.by(ConstantUtils.EFFECTIVE_FROM).ascending()));
        List<PricingTierConfig> tierplans = pricingConfigRepository.findByTierType(pricingTierConfig.getTierType(), DateUtils.getISODate(new Date(), ConstantUtils.UTC), pageable)
                .stream().filter(plan -> plan.getTierCode().equalsIgnoreCase(pricingTierConfig.getTierCode()) && !(plan.getId().equalsIgnoreCase(id)))
                .collect(Collectors.toList());
        tierplans.forEach((currentPlan) -> {
            if (null != currentPlan.getEffectiveFrom()) {
                currentPlan.setExpiredOn(DateUtils.getPreviousDay(pricingTierConfig.getEffectiveFrom()));
                mapper.map(pricingConfigRepository.save(currentPlan), PricingTierConfigDto.class);
                operationsUtil.saveAuditTrial(id, currentPlan);
            }
        });
    }

    @Override
    public List<PricingTierConfigDto> getActiveByType(String tierType) {
        Pageable pageableSort = PageRequest.of(ConstantUtils.PAGE_START_INDEX, ConstantUtils.PAGE_END_INDEX,
                Sort.by(ConstantUtils.TIER_SEQ).ascending());
        return pricingConfigRepository.findActiveByType(tierType, DateUtils.getISODate(new Date(), ConstantUtils.UTC), pageableSort).stream()
                .map(plan -> mapper.map(plan, PricingTierConfigDto.class)).collect(Collectors.toCollection(ArrayList::new));
    }
}
