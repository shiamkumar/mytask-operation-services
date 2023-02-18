package com.ghx.api.operations.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.LookupDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.LookupVO;
import com.ghx.api.operations.repository.LookupRepository;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * The Class LookupServiceImpl.
 */
@Component
public class LookupServiceImpl implements LookupService {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(LookupServiceImpl.class);

    @Autowired
    private LookupRepository lookupRepository;

    /** The model mapper. */
    private final transient ModelMapper modelMapper = new ModelMapper();

    @Override
    @LogExecutionTime
    public LookupDTO findByCategoryAndCodeAndParentId(String category, String code, String parentId) {
        LookupVO lookupVO = null;
        if (StringUtils.isBlank(parentId)) {
            lookupVO = lookupRepository.findByCategoryAndCode(category, code);
    		if (Objects.isNull(lookupVO)) {
				throw new BusinessException(
						CustomMessageSource.getMessage(ErrorConstants.LOOKUP_NOT_EXIST, category, code));
			}
            return this.modelMapper.map(lookupVO, LookupDTO.class);
        } else {
            lookupVO = lookupRepository.findByCategoryAndCodeAndParentOid(category, code, parentId);
    		if (Objects.isNull(lookupVO)) {
				throw new BusinessException(
						CustomMessageSource.getMessage(ErrorConstants.LOOKUP_NOT_EXIST, category, code));
			}
            return this.modelMapper.map(lookupVO, LookupDTO.class);

        }
    }

    @LogExecutionTime
    @Override
    public Map<String, List<LookupDTO>> findByCategories(String categories) {
        LOGGER.info("inside findByCategories {}", categories);
        Map<String, List<LookupDTO>> categoriesMap = null;
        if (StringUtils.isNotBlank(categories)) {
            categoriesMap = new HashMap<>();
            String[] visionCategories = StringUtils.split(categories, ConstantUtils.COMMA);
            for (String category : visionCategories) {
				Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(ConstantUtils.SEQ).ascending());
                List<LookupVO> lookupList = null;
                if (StringUtils.equalsAnyIgnoreCase(category, ConstantUtils.PREPAID, ConstantUtils.CREDIT_CARD)) {
                    lookupList = lookupRepository.findByCategory(category, pageable);
                } else if (StringUtils.equalsIgnoreCase(category, ConstantUtils.STATE)) {
                    lookupList = lookupRepository.findAllStateLookups();
                } else if (StringUtils.equalsIgnoreCase(category, ConstantUtils.CERTIFICATION_AGENCY)) {
                    lookupList = lookupRepository.findAllCertificateAgencies();
                } else {
                    lookupList = lookupRepository.findByCategory(category.toUpperCase(LocaleContextHolder.getLocale()), pageable);
                }
                if (CollectionUtils.isNotEmpty(lookupList)) {
                    List<LookupDTO> lookupDTOList = Arrays.asList(modelMapper.map(lookupList, LookupDTO[].class));
                    categoriesMap.put(category, lookupDTOList);
                } else {
                    LOGGER.error(" this category : {} has no values ", category);
                    throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.CATGEGORY_NOT_EXIST, category));
                }
            }
        }
        return categoriesMap;
    }
    
	/**
	 * Find by category and description.
	 *
	 * @param category the category
	 * @param description the description
	 * @return the lookup DTO
	 */
	@Override
	@LogExecutionTime
	public LookupDTO findByCategoryAndDescription(String category, String description) {

		List<LookupVO> lookupVO = lookupRepository.findByCategoryAndDescription(category, description);
		
		if (CollectionUtils.isEmpty(lookupVO) || Objects.isNull(lookupVO.get(0))) {
			throw new BusinessException(
					CustomMessageSource.getMessage(ErrorConstants.LOOKUP_NOT_EXIST, category, description));
		}
		return this.modelMapper.map(lookupVO.get(0), LookupDTO.class);
	}
}
