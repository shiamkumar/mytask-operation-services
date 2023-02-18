package com.ghx.api.operations.repository;

import java.util.List;

import com.ghx.api.operations.model.LookupVO;

/**
 * The Interface LookupRepositoryCustom.
 */
public interface LookupRepositoryCustom {

    List<LookupVO> findAllStateLookups();

    List<LookupVO> findAllCertificateAgencies();
    
	/** findAllTemplateNames by templateOids */
	List<String> findAllTemplateNames(List<String> templateOids);
    
}
