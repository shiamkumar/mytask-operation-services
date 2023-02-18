package com.ghx.api.operations.service;

import java.util.List;
import java.util.Map;

import com.ghx.api.operations.dto.LookupDTO;

/**
 * The Interface LookupService.
 */
public interface LookupService {

    LookupDTO findByCategoryAndCodeAndParentId(String category, String code, String parentId);

    Map<String, List<LookupDTO>> findByCategories(String categories);

	LookupDTO findByCategoryAndDescription(String category, String description);

}
