package com.ghx.api.operations.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.transform.Transformers;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.LookupVO;

/**
 * The Class LookupRepositoryImpl.
 */
@Repository
@SuppressWarnings({ "unchecked", "deprecation" })
public class LookupRepositoryImpl implements LookupRepositoryCustom {

    /** The entity manager. */
    @PersistenceContext
    private transient EntityManager entityManager;

    private final transient ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<LookupVO> findAllStateLookups() {


        List<Map<String, Object>> listOfMaps = entityManager.createNativeQuery(
                "select l.code, l.seq, l.category,l.description,l.parent_oid as parentOid from vision.lookup l where l.deprecated is false and l.category ilike 'STATE%'")
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        return CollectionUtils.isNotEmpty(listOfMaps) ? Arrays.asList(modelMapper.map(listOfMaps, LookupVO[].class)) : null;


    }

    @Override
    public List<LookupVO> findAllCertificateAgencies() {
        List<Map<String, Object>> listOfMaps = entityManager
                .createNativeQuery("SELECT oid as code , cert_type_code as category , name as description  from certification_agency c ")
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
        return CollectionUtils.isNotEmpty(listOfMaps) ? Arrays.asList(modelMapper.map(listOfMaps, LookupVO[].class)) : null;
    }

	/**
	 * findAllTemplateNames by templateOids
	 * 
	 * @param templateOids
	 * @return
	 */
	@Override
	public List<String> findAllTemplateNames(List<String> templateOids) {
		return entityManager.createNativeQuery(
				"SELECT l.sub_description as \"templateName\" from vision.lookup l where l.description in (?1) ")
				.setParameter(1, templateOids).unwrap(org.hibernate.query.Query.class).getResultList();

	}
}

