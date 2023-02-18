package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.MergeSupplierRequest;
import com.ghx.api.operations.repository.MergeRequestRepositoryCustom;
import com.ghx.api.operations.repository.MergeSupplierRepository;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.MergeSupplierRequestUtil;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.validation.business.MergeSupplierBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoException;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @author Sundari V
 * @since 03/05/2021
 */
@Component
public class MergeSupplierServiceImpl implements MergeSupplierService {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(MergeSupplierServiceImpl.class);

    private final transient ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MergeSupplierRepository mergeSupplierRepository;

    @Autowired
    private MergeRequestRepositoryCustom mergeRequestRepositoryCustom;

    @Autowired
    private MergeSupplierBusinessValidator mergeSupplierBusinessValidator;

    @Autowired
    private MergeSupplierRequestUtil mergeSupplierRequestUtil;

    /** mongo export limit */
    @Value("${export.limit.merge-supplier}")
    private int mergeSupplierExportLimit;
/**
 * Delete service implementation for Merge Supplier Request
 */
	@Override
	@LogExecutionTime
	public void delete(String mergeRequestId) {
		Optional<MergeSupplierRequest> optMergeSupplierRequest = mergeSupplierRepository.findById(mergeRequestId);
		if (optMergeSupplierRequest.isPresent()) {
			if (ConstantUtils.CREATED.equalsIgnoreCase(optMergeSupplierRequest.get().getStatus())) {
				optMergeSupplierRequest.get().setStatus(ConstantUtils.DELETED);
				mergeSupplierRepository.save(optMergeSupplierRequest.get());
				LOGGER.info("MergeSupplier status marked as DELETED {} ", mergeRequestId);
			} else {
				throw new BusinessException(
						CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUEST_SUPPLIER_CANNOT_BE_DELETED));
			}

		} else {
			throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUESTID_NOT_FOUND));
		}
	}

    /**
     * Returns the List of Merge Supplier Requests matching the Search Criteria
     * @param searchRequest
     * @param pageable
     * @return map
     */
    @Override
    @LogExecutionTime
    public Map<String, Object> getAllMergeRequests(SearchRequest searchRequest, Pageable pageable) {
        mergeSupplierBusinessValidator.validateDateParams(searchRequest.getSubmittedDateFrom(), searchRequest.getSubmittedDateTo());
        Map<String, Object> mergeRequestDetails = new HashMap<>();
        try {
        	List<MergeSupplierRequestDTO> allMergeRequests = mergeRequestRepositoryCustom.findAllMergeRequests(searchRequest, pageable);
        	long allMergerRequestsCount = mergeRequestRepositoryCustom.findMergeRequestsCount(searchRequest);

            mergeRequestDetails.put(ConstantUtils.MERGE_REQUESTS, allMergeRequests);
            mergeRequestDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, allMergerRequestsCount);

            List<MergeSupplierRequestDTO> mergeList = Arrays
                    .asList(new ObjectMapper().convertValue(allMergeRequests, MergeSupplierRequestDTO[].class));
            LOGGER.info("getAllMergeRequests:: Merge Requests fetched: {}, total requests found: {}", mergeList.size(), allMergerRequestsCount);
        } catch (MongoException ex) {
            LOGGER.error("getAllMergeRequests:: Exception occurred while fetching Merge Supplier Requests");
            throw new BusinessException(ex);
        }
        return mergeRequestDetails;
    }

    /**
     * Saves the given merge Supplier request Details
     * @param mergeSupplierRequest
     * @return
     */
    @Override
    @LogExecutionTime
    public String createMergeSupplierRequest(MergeSupplierRequestDTO mergeSupplierRequestDTO) {
        mergeSupplierBusinessValidator.validateSaveRequestParams(mergeSupplierRequestDTO);
        mergeSupplierBusinessValidator.validateDuplicateMergeRequest(mergeSupplierRequestDTO.getDeleteSupplierOid(),
                mergeSupplierRequestDTO.getRetainSupplierOid());
        mergeSupplierRequestUtil.populateMergeRequest(mergeSupplierRequestDTO);
        MergeSupplierRequest mergeSupplierRequest = modelMapper.map(mergeSupplierRequestDTO, MergeSupplierRequest.class);
        mergeSupplierRequest.setPersisted(true);
        mergeSupplierRequest = mergeSupplierRepository.save(mergeSupplierRequest);
        return mergeSupplierRequest.getId();
    }

    /**
     * export Prepaid Contracts Service Implementation
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void exportMergeSupplierRequests(SearchRequest searchRequest, ResourceLoader resourceLoader, String exportType,
            HttpServletResponse response) throws IOException {
        searchRequest.setPageable(searchRequest.getPageable().getPageSize() >= mergeSupplierExportLimit
                ? PageRequest.of(searchRequest.getPageable().getPageNumber(), mergeSupplierExportLimit, searchRequest.getPageable().getSort())
                : searchRequest.getPageable());
        List<MergeSupplierRequestDTO> mergeRequestDTOList = (List<MergeSupplierRequestDTO>) getAllMergeRequests(searchRequest,
                searchRequest.getPageable()).get(ConstantUtils.MERGE_REQUESTS);
        ReportUtils.exportMergeSupplierRequests(exportType, response, mergeRequestDTOList, resourceLoader, ConstantUtils.MERGE_SUPPLIER_REQUESTS);
    }

}
