package com.ghx.api.operations.service;

import static com.ghx.api.operations.util.ConstantUtils.AUDIT_TRAIL_ALIAS;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_TYPE;
import static com.ghx.api.operations.util.ConstantUtils.AUDIT_TYPES;
import static com.ghx.api.operations.util.ConstantUtils.COLON;
import static com.ghx.api.operations.util.ConstantUtils.COMMA;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_BY;
import static com.ghx.api.operations.util.ConstantUtils.CREATED_ON;
import static com.ghx.api.operations.util.ConstantUtils.DETAILS;
import static com.ghx.api.operations.util.ConstantUtils.DOUBLE_HASH;
import static com.ghx.api.operations.util.ConstantUtils.EMAIL_URL;
import static com.ghx.api.operations.util.ConstantUtils.ES_TOTAL_RECORDS;
import static com.ghx.api.operations.util.ConstantUtils.SEARCH;
import static com.ghx.api.operations.util.ConstantUtils.TOTAL_NO_OF_RECORDS;
import static com.ghx.api.operations.util.ConstantUtils.UNDERSCORE;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.AuditExportDTO;
import com.ghx.api.operations.dto.AuditTrailFieldsDTO;
import com.ghx.api.operations.dto.AuditTrailSearchRequest;
import com.ghx.api.operations.dto.AuditTrailSearchResponse;
import com.ghx.api.operations.dto.AuditTypeDTO;
import com.ghx.api.operations.dto.GlobalSearchDTO;
import com.ghx.api.operations.enums.AuditFieldSearchType;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.exception.SystemException;
import com.ghx.api.operations.model.AuditFieldsVO;
import com.ghx.api.operations.model.AuditMappingVO;
import com.ghx.api.operations.model.RenderField;
import com.ghx.api.operations.model.SearchField;
import com.ghx.api.operations.repository.AuditFieldsRepository;
import com.ghx.api.operations.repository.AuditMappingRepository;
import com.ghx.api.operations.repository.AuditMappingRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.common.search.engine.SearchEngineClient;
import com.ghx.common.search.model.SearchResult;
import com.ghx.common.search.query.support.SearchOption;
import com.ghx.common.search.query.support.SortOption;
import com.ghx.common.search.query.support.SortOption.FieldDirection;
import com.ghx.eventutils.model.AuditType;
import com.ghx.eventutils.model.DocumentUploadFields;
import com.mongodb.MongoException;

/**
 * 
 * @author Vijayakumar S
 * @author Mari Muthu Muthukrishnan
 * 
 * @since 09/JUNE/2022
 * 
 *        Service implementation layer of Audit-trail
 *
 */
@Service
public class AuditTrailServiceImpl implements AuditTrailService {

    /** Logger Instance */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(AuditTrailServiceImpl.class);

    /**
     * Dot operator constant
     */
    private static final String DOT_OPERATOR = ".";

    /** Elastic Search Client */
    @Autowired
    private SearchEngineClient searchEngineClient;

    /** AuditMapping Repository Instance */
    @Autowired
    private AuditMappingRepository auditMappingRepository;

    /** AuditFields Repository */
    @Autowired
    private AuditFieldsRepository auditFieldsRepository;

    /** AuditMapping Repository */
    @Autowired
    private AuditMappingRepositoryCustom auditMappingRepositoryCustom;

    @SuppressWarnings("unchecked")
    @Override
    public AuditTrailSearchResponse getAuditTrails(AuditTrailSearchRequest auditTrailSearchRequest, Pageable pageable) {
        // Build Query
        BoolQueryBuilder query = buildQuery(auditTrailSearchRequest);

        // sort
        SearchOption searchOption = new SearchOption();
        List<SortOption> sortFields = new ArrayList<>();
        SortOption sortOption = new SortOption();
        if (pageable.getSort().isUnsorted()) {
            sortOption.setFieldDirection(FieldDirection.DESC);
            sortOption.setFieldName(CREATED_ON);
        } else {
            pageable.getSort().get().forEach(order -> {
                sortOption.setFieldName(order.getProperty());
                sortOption.setFieldDirection(order.getDirection().isAscending() ? FieldDirection.ASC : FieldDirection.DESC);
            });
        }
        sortFields.add(sortOption);
        searchOption.setSortOptions(sortFields);

        // pagination
        searchOption.setPageOffset((int) pageable.getOffset());
        searchOption.setPageTotal(pageable.getPageSize());
        AuditTrailSearchResponse auditTrailSearchResponse = new AuditTrailSearchResponse();

        SearchResult searchResult = searchEngineClient.search(query, searchOption, AUDIT_TRAIL_ALIAS);
        if (CollectionUtils.isNotEmpty(searchResult.getResponseDocuments())) {
            auditTrailSearchResponse.setAuditRecords(searchResult.getResponseDocuments().stream().map(doc -> {
                Map<String, Object> auditRecord = (Map<String, Object>) doc.get(DETAILS);
                auditRecord.put(CREATED_ON, doc.get(CREATED_ON));
                auditRecord.put(CREATED_BY, doc.get(CREATED_BY));
                auditRecord.put(AUDIT_TYPE, doc.get(AUDIT_TYPE));
                return auditRecord;
            }).collect(Collectors.toList()));
        }
        auditTrailSearchResponse.setTotalRecords((long) searchResult.getResponseMetaData().get(ES_TOTAL_RECORDS));
        return auditTrailSearchResponse;
    }

    /**
     * 
     * @param auditTrailSearchRequest
     * @return
     */
    private BoolQueryBuilder buildQuery(AuditTrailSearchRequest auditTrailSearchRequest) {
        BoolQueryBuilder query = boolQuery();
        if (StringUtils.isNotBlank(auditTrailSearchRequest.getType())) {
            query.must(matchQuery(ConstantUtils.AUDIT_TYPE, auditTrailSearchRequest.getType()));
        } else {
            throw new BusinessException("AuditType is needed");
        }
        if (Objects.nonNull(auditTrailSearchRequest.getCreatedOnFrom()) && Objects.nonNull(auditTrailSearchRequest.getCreatedOnTo())) {
            query.must(rangeQuery(CREATED_ON).gte(auditTrailSearchRequest.getCreatedOnFrom().getTime())
                    .lte(auditTrailSearchRequest.getCreatedOnTo().getTime()));
        }
        if (StringUtils.isNotBlank(auditTrailSearchRequest.getCreatedBy())) {
            query.must(matchQuery(CREATED_BY, auditTrailSearchRequest.getCreatedBy()));
        }
        processAdvancedSearchOption(auditTrailSearchRequest, query);
        processGlobalSearchOption(auditTrailSearchRequest, query);
        LOGGER.debug("Query created - {}", query);
        return query;
    }

    /**
     * Process global search option
     * @param auditTrailSearchRequest
     * @param query
     */
    private void processGlobalSearchOption(AuditTrailSearchRequest auditTrailSearchRequest, BoolQueryBuilder query) {
        GlobalSearchDTO globalSearch = auditTrailSearchRequest.getGlobalSearch();
        if (Objects.nonNull(globalSearch)) {
            Map<String, String> searchType = StringUtils.isNotBlank(globalSearch.getSearchType()) ? Arrays.asList(globalSearch.getSearchType().split(COMMA)).stream()
                    .map(sKeyValue -> sKeyValue.split(COLON)).collect(Collectors.toMap(mColonMap -> mColonMap[0], str -> str[1])) : new HashMap<>();
            if (StringUtils.isBlank(auditTrailSearchRequest.getGlobalSearch().getSearchText())
                    || CollectionUtils.isEmpty(auditTrailSearchRequest.getGlobalSearch().getFieldNames())) {
                LOGGER.error(ConstantUtils.GLOBAL_SEARCH_PARAMETERS_INCORRECT);
                throw new BusinessException(ConstantUtils.GLOBAL_SEARCH_PARAMETERS_INCORRECT);
            }
            MultiMatchQueryBuilder multiMatchQuery = multiMatchQuery(auditTrailSearchRequest.getGlobalSearch().getSearchText());
            auditTrailSearchRequest.getGlobalSearch().getFieldNames().forEach(fieldName -> {
                if (MapUtils.isNotEmpty(searchType) && searchType.containsKey(fieldName)
                        && StringUtils.equalsIgnoreCase(AuditFieldSearchType.text_partial.toString(), MapUtils.getString(searchType, fieldName))) {
                    multiMatchQuery.field(StringUtils.join(DETAILS, DOT_OPERATOR, fieldName, DOT_OPERATOR, fieldName, UNDERSCORE, SEARCH));
                } else if ((MapUtils.isNotEmpty(searchType) && searchType.containsKey(fieldName)
                        && StringUtils.equalsAnyIgnoreCase(AuditFieldSearchType.email.toString(), MapUtils.getString(searchType, fieldName)))) {
                    multiMatchQuery.field(StringUtils.join(DETAILS, DOT_OPERATOR, fieldName));
                    multiMatchQuery.field(StringUtils.join(DETAILS, DOT_OPERATOR, fieldName, DOT_OPERATOR, fieldName, UNDERSCORE, EMAIL_URL));
                } else {
                    multiMatchQuery.field(StringUtils.join(DETAILS, DOT_OPERATOR, fieldName));
                }
            });
            multiMatchQuery.operator(Operator.AND);
            query.must(multiMatchQuery);
        }
    }

    /**
     * Process the Advanced search options
     * @param auditTrailSearchRequest
     * @param query
     */
    private void processAdvancedSearchOption(AuditTrailSearchRequest auditTrailSearchRequest, BoolQueryBuilder query) {
        if (CollectionUtils.isNotEmpty(auditTrailSearchRequest.getAdvancedSearch())) {
            auditTrailSearchRequest.getAdvancedSearch().forEach(advancedSearch -> {
                if (Objects.nonNull(advancedSearch.getSearchType()) && advancedSearch.getSearchType().toString().equalsIgnoreCase("date_range")) {
                    ObjectMapper mapper = new ObjectMapper();
                    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
                    };
                    HashMap<String, Object> fieldValueMap = mapper.convertValue(advancedSearch.getFieldValue(), typeRef);
                    query.must(rangeQuery(StringUtils.join(DETAILS, DOT_OPERATOR, advancedSearch.getFieldName())).gte(fieldValueMap.get("from"))
                            .lte(fieldValueMap.get("to")));
                } else if (Objects.nonNull(advancedSearch.getSearchType())
                        && StringUtils.equalsIgnoreCase(advancedSearch.getSearchType().toString(), "text_partial")) {
                            query.must(matchQuery(StringUtils.join(DETAILS, DOT_OPERATOR, advancedSearch.getFieldName(), DOT_OPERATOR,
                                    advancedSearch.getFieldName(), "_search"), advancedSearch.getFieldValue()).operator(Operator.AND));
                        } else
                    if (Objects.nonNull(advancedSearch.getSearchType()) && StringUtils.equalsAnyIgnoreCase(advancedSearch.getSearchType().toString(), "email")) {
                        query.should(new QueryStringQueryBuilder(
                                StringUtils.join(normalizeSearchString(advancedSearch.getFieldValue().toString()), ConstantUtils.STAR_SYMBOL))
                                        .field(StringUtils.join(DETAILS, DOT_OPERATOR, advancedSearch.getFieldName())).defaultOperator(Operator.AND));
                        query.should(new QueryStringQueryBuilder(StringUtils.join((advancedSearch.getFieldValue().toString())))
                                .field(StringUtils.join(DETAILS, DOT_OPERATOR, advancedSearch.getFieldName())).field(StringUtils.join(DETAILS,
                                        DOT_OPERATOR, advancedSearch.getFieldName(), DOT_OPERATOR, advancedSearch.getFieldName(), "_emailurl"))
                                .defaultOperator(Operator.AND));
                        query.minimumShouldMatch(1);
                    } else {
                        query.must(
                                matchQuery(StringUtils.join(DETAILS, DOT_OPERATOR, advancedSearch.getFieldName()), advancedSearch.getFieldValue()));
                    }
            });
        }
    }

    /**
     * 
     * @param searchValue
     * @return
     */
    private static String normalizeSearchString(String searchValue) {
        String replacedValue = searchValue;
        replacedValue = replacedValue.replaceAll("'(?=[\\d])|(?<=[\\d])'", StringUtils.SPACE).trim();
        replacedValue = replacedValue.replaceAll("[^a-zA-Z0-9._' ]", StringUtils.SPACE).trim();
        return replacedValue;
    }

    @Override
    public AuditTrailFieldsDTO getFields(String type) {
        LOGGER.info("AuditType - {}", type);
        if (StringUtils.isBlank(type)) {
            LOGGER.error("Invalid Sourcetype");
            throw new BusinessException("Invalid Sourcetype");
        }
        AuditMappingVO auditMappingVO = auditMappingRepository.findByType(type);
        if (Objects.isNull(auditMappingVO)) {
            LOGGER.error("Audittype not found - {}", type);
            throw new BusinessException(ConstantUtils.AUDIT_TYPE_NOT_FOUND);
        }
        List<RenderField> renderFields = populateRenderFields(auditMappingVO, type);

        List<SearchField> searchFields = populateSearchFields(auditMappingVO, type);

        AuditTrailFieldsDTO auditTrailFieldsDTO = new AuditTrailFieldsDTO();
        auditTrailFieldsDTO.setType(type);
        auditTrailFieldsDTO.setRenderFields(renderFields);
        auditTrailFieldsDTO.setSearchFields(searchFields);
        return auditTrailFieldsDTO;
    }

    private List<SearchField> populateSearchFields(AuditMappingVO auditMappingVO, String sourceType) {
        List<SearchField> searchFields = auditMappingVO.getSearchFields();
        if (CollectionUtils.isNotEmpty(searchFields)) {
            searchFields.forEach(searchField -> {
                if (searchField.isInheritProperties()) {
                    AuditFieldsVO auditFieldsVO = auditFieldsRepository.findByFieldName(searchField.getName());
                    if (Objects.isNull(auditFieldsVO)) {
                        LOGGER.info("No audit_fields found for field - {}", searchField.getName());
                    } else {
                        searchField.setType(auditFieldsVO.getSearchProperties().getType());
                        searchField.setDetails(auditFieldsVO.getSearchProperties().getDetails());
                    }
                }
            });
        } else {
            LOGGER.error("No searchFields detected for AuditType - {}", sourceType);
            throw new SystemException("No searchFields detected for AuditType - " + sourceType);
        }
        return searchFields;
    }

    private List<RenderField> populateRenderFields(AuditMappingVO auditMappingVO, String sourceType) {
        List<RenderField> renderFields = auditMappingVO.getRenderFields();
        if (CollectionUtils.isNotEmpty(renderFields)) {
            renderFields.forEach(renderField -> {
                if (renderField.isInheritProperties()) {
                    AuditFieldsVO auditFieldsVO = auditFieldsRepository.findByFieldName(renderField.getFields());
                    if (Objects.isNull(auditFieldsVO)) {
                        LOGGER.info("No audit_fields found for field - {}", renderField.getFields());
                    } else {
                        renderField.setSort(auditFieldsVO.getRenderProperties().isSort());
                        renderField.setSearch(auditFieldsVO.getRenderProperties().isSearch());
                        renderField.setDisplayHyperlink(auditFieldsVO.getRenderProperties().isDisplayHyperlink());
                        renderField.setNavigateView(auditFieldsVO.getRenderProperties().getNavigateView());
                        renderField.setType(auditFieldsVO.getRenderProperties().getType());
                    }
                }
            });
        } else {
            LOGGER.error("No renderfields detected for AuditType - {}", sourceType);
            throw new SystemException("No renderfields detected for AuditType - " + sourceType);
        }
        return renderFields;
    }

    @Override
    public Map<String, Object> getAllAuditTypes(AuditTypeDTO searchRequest, Pageable pageable) {
        Map<String, Object> auditTypesDetails = new HashMap<>();
        try {
            List<AuditTypeDTO> allAuditTypes = auditMappingRepositoryCustom.findAllAuditTypes(searchRequest, pageable);
            long auditTypesCount = auditMappingRepositoryCustom.findAuditTypesCount(searchRequest);

            auditTypesDetails.put(AUDIT_TYPES, allAuditTypes);
            auditTypesDetails.put(TOTAL_NO_OF_RECORDS, auditTypesCount);

            List<AuditTypeDTO> auditTypesList = Arrays.asList(new ObjectMapper().convertValue(allAuditTypes, AuditTypeDTO[].class));
            LOGGER.info("getAllAuditTypes:: Audit Types fetched: {}, total Audit Types: {}", auditTypesList.size(), auditTypesCount);
        } catch (MongoException ex) {
            LOGGER.error("getAllAuditTypes:: Exception occurred while fetching Audit Types ");
            throw new BusinessException(ex);
        }
        return auditTypesDetails;
    }

    @Override
    public AuditExportDTO exportAuditTrailsReports(ResourceLoader resourceLoader, AuditTrailSearchRequest auditTrailSearchRequest, String exportType,
            HttpServletResponse response, Pageable pageable) throws IOException {
        AuditTrailSearchResponse auditTrailSearchResponse = getAuditTrails(auditTrailSearchRequest, pageable);
        if(StringUtils.equalsIgnoreCase(AuditType.DOCUMENT_AUDIT.getType(), auditTrailSearchRequest.getType())) {
            processHealthSystems(auditTrailSearchResponse.getAuditRecords());
        }
        return ReportUtils.exportAuditTrailsReports(exportType, auditTrailSearchResponse.getAuditRecords(), resourceLoader, ConstantUtils.EXPORT_AUDIT_TRIALS_REPORTS);
    }

    /**
     * Replaces the health system separator from ## to , 
     * e.g. 
     * Before : Minority Vendor Sourcing##Persistent Vendormate 
     * After  : Minority Vendor Sourcing,Persistent Vendormate
     * 
     * @param auditRecords
     */
    private void processHealthSystems(List<Map<String, Object>> auditRecords) {
        if(CollectionUtils.isNotEmpty(auditRecords)) {
            for (Map<String, Object> auditRecord : auditRecords) {
                Object providerNames = auditRecord.get(DocumentUploadFields.provider_name.name());
                if(Objects.nonNull(providerNames)) {
                    auditRecord.put(DocumentUploadFields.provider_name.name(), StringUtils.replace(providerNames.toString(), DOUBLE_HASH, COMMA));
                }
            }
        }
    }
}

