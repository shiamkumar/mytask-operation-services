package com.ghx.api.operations.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.DocUploadHistoryDTO;
import com.ghx.api.operations.dto.DocUploadRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.enums.DocUploadRequestStatus;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.exception.ResourceNotFoundException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.DocTemplateDetails;
import com.ghx.api.operations.model.DocUploadRequest;
import com.ghx.api.operations.model.DocUploadRequestDetails;
import com.ghx.api.operations.repository.DocUploadRequestRepository;
import com.ghx.api.operations.repository.DocUploadRequestRepositoryCustom;
import com.ghx.api.operations.repository.LookupRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.util.SecurityUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoException;

/**
 * 
 * @author Manoharan.R
 * 
 *         The class DocUploadRequestServiceImpl - the class which holds all
 *         the business layer of doc upload request
 *
 */
@Service
public class DocUploadRequestServiceImpl implements DocUploadRequestService {
	
	/** The Constant LOGGER. */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(DocUploadRequestServiceImpl.class);
	
	
	/**Initialize  DocUploadRepositoryCustom*/
	@Autowired
	private DocUploadRequestRepositoryCustom docUploadRequestRepositoryCustom;
    
    /** The securityUtils to getCurrent loggin user    */
    @Autowired
    private SecurityUtils securityUtils;
    
    /** The class docUploadRequestRepository */
    @Autowired
    private transient DocUploadRequestRepository docUploadRequestRepository;
    
    /** initalize ResourceLoader */
    @Autowired
    private transient ResourceLoader resourceLoader;
    /** initalize ResourceLoader */
    
    @Autowired
    private transient LookupRepositoryCustom lookupRepository;

 
	/**
	 * save DocUploadRequest 
	 * @docUploadRequestDTO
	 * salesForceId
	 * mongoKey
	 */
	@Override
	public DocUploadRequest saveDocUploadRequest(DocUploadRequestDTO docUploadRequestDTO) {
		LOGGER.info("saveDocUploadRequest started");
		DocUploadRequest uploadRequestDTO = populateDocUploadRequestDTO(docUploadRequestDTO);
		docUploadRequestRepository.save(uploadRequestDTO);
		LOGGER.info("DocUploadRequest save Successfully");
		return uploadRequestDTO;
	}
	
	/**
	 * populate DocUploadRequest
	 * @param blobDTO 
	 * @param docUploadRequestDTO
	 * @param userIds
	 * @return
	 */
	private DocUploadRequest populateDocUploadRequestDTO(DocUploadRequestDTO docUploadRequestDTO) {
		DocUploadRequest docUploadRequest = new DocUploadRequest();
		docUploadRequest.setId(UUID.randomUUID().toString());
		docUploadRequest.setSalesForceId(docUploadRequestDTO.getSalesForceId());
		docUploadRequest.setTemplateMongoKey(docUploadRequestDTO.getTemplateMongoKey());
		docUploadRequest.setTemplateOids(docUploadRequestDTO.getTemplateOids());
		docUploadRequest.setDocStatus(docUploadRequestDTO.getDocStatus());
		docUploadRequest.setTotalUserCount((int) docUploadRequestDTO.getTotalUserCount());
		docUploadRequest.setSuccessUserCount((int) docUploadRequestDTO.getSuccessUserCount());
		docUploadRequest.setFailedUserCount((int) docUploadRequestDTO.getFailedUserCount());
		docUploadRequest.setCreatedBy(securityUtils.getCurrentUser());
		docUploadRequest.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		docUploadRequest.setUpdatedBy(securityUtils.getCurrentUser());
		docUploadRequest.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
		docUploadRequest.setFein(docUploadRequestDTO.getFein());
		docUploadRequest.setAllReps(docUploadRequestDTO.isAllReps());	
		if(docUploadRequestDTO.isAllReps()) {
			docUploadRequest.setRepsMongoKey(null);
		} else {
			docUploadRequest.setRepsMongoKey(docUploadRequestDTO.getRepsMongoKey());
		}

		return docUploadRequest;
	}

	/**
	 * get all docUpload requests
	 * @param Pageable
	 */
	@Override
	public Map<String,Object> getAllDocUploadRequests(Pageable pageable) {
		Map<String,Object> allDocUploadRequestMap = new HashMap<>();
		try {
			List<DocUploadRequestDTO> allDocUploadRequests = docUploadRequestRepositoryCustom.findAllDocUploadRequests(pageable);
			long docUploadRequestCount = docUploadRequestRepositoryCustom.findDocUploadRequestCount();
			allDocUploadRequestMap.put(ConstantUtils.TOTAL_NO_OF_RECORDS, docUploadRequestCount);
			allDocUploadRequestMap.put(ConstantUtils.DOCUPLOAD_REQUEST, allDocUploadRequests);
			LOGGER.info(" Document Upload Requests Fetched: {} " , docUploadRequestCount);
		} catch (MongoException e) {
			LOGGER.error("Error occured while fetch all doc upload requests ", e);
			throw new BusinessException(e);
		}
		return allDocUploadRequestMap;
	}
    
    /**
     * DocUploadDeleteRequest
     */
    @Override
    @LogExecutionTime
    public String docUploadDeleteRequest(String id) {
        Optional<DocUploadRequest> docUploadRequestOptional = docUploadRequestRepository.findById(id);
        if (!docUploadRequestOptional.isPresent()) {
            throw new ResourceNotFoundException(CustomMessageSource.getMessage(ErrorConstants.RESOURCE_NOT_FOUND));
        }
        DocUploadRequest docUploadRequest = docUploadRequestOptional.get();
        if (!StringUtils.equalsIgnoreCase(ConstantUtils.CREATED, docUploadRequest.getStatus().getStatus())) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.REQUEST_ALREADY_IN_PROGRESS));
        }
        docUploadRequest.setStatus(DocUploadRequestStatus.valueOf(ConstantUtils.DELETED));
        docUploadRequest.setUpdatedBy(securityUtils.getCurrentUser());
        docUploadRequest.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        docUploadRequestRepository.save(docUploadRequest);
		LOGGER.info("DocUploadRequest Deleted Successfully for the Id {} ",id);
        return ConstantUtils.DELETED_SUCCESSFULLY;
    }
    
    /**
	 * get upload doc details by DocUploadRequestId
	 * 
	 * @param id 
	 * @param pageable      Example: page = 0, size = 1
	 */
	@Override
	public Map<String, Object> getDocUploadRequestDetails(String docUploadRequestId, Pageable pageable) {
		
		List<DocUploadRequestDetails> docUploadRequestDetails = docUploadRequestRepositoryCustom
				.fetchAllDocUploadRequestDetails(docUploadRequestId, pageable);
		if (docUploadRequestDetails.isEmpty()) {
			throw new ResourceNotFoundException(CustomMessageSource.getMessage(ErrorConstants.RESOURCE_NOT_FOUND));
		}
		long docUploadRequestDetailsCount = docUploadRequestRepositoryCustom.findDocUploadRequestDetailsCount(docUploadRequestId);
		Map<String, Object> docUploadRequestDetailsMap = new HashMap<>();
		docUploadRequestDetailsMap.put(ConstantUtils.TOTAL_NO_OF_RECORDS, docUploadRequestDetailsCount);
		docUploadRequestDetailsMap.put(ConstantUtils.DOCUPLOAD_REQUEST_DETAILS, docUploadRequestDetails);
		LOGGER.info(" Document Upload Requests Details Fetched: {} " , docUploadRequestDetails.size());
		return docUploadRequestDetailsMap;
	}

	/**
	 * export DocUploadHistory
	 * 
	 * @param exportType
	 * @param pageable
	 * @throws IOException
	 */
	@Override
	public void exportDocUploadHistory(String exportType, Pageable page, HttpServletResponse response)
			throws IOException {
		List<DocUploadRequest> requestDetails = docUploadRequestRepositoryCustom.fetchUploadHistoryDetails(page);
		List<DocUploadHistoryDTO> requestSummaryDTO = new ArrayList<>();
		DocUploadHistoryDTO docUploadHistoryDTO = null;
		for (DocUploadRequest docUploadRequest : requestDetails) {

			docUploadHistoryDTO = populateDocUploadRequestDetail(docUploadRequest, docUploadHistoryDTO);
			List<String> templateOids = docUploadRequest.getTemplateOids();
			List<String> templateNames = lookupRepository.findAllTemplateNames(templateOids);
			docUploadHistoryDTO.setTemplateNames(String.join(", ", templateNames));
			requestSummaryDTO.add(docUploadHistoryDTO);
		}
		List objectList =  Arrays.asList(new ObjectMapper().convertValue(requestSummaryDTO, DocUploadHistoryDTO[].class));
		ReportUtils.generateReport(exportType, objectList, response, this.resourceLoader,
				ConstantUtils.FILENAME_DOCUPLOADREQUEST, null,ConstantUtils.DOCUPLOADREQUEST_EXPORT);
	}

	/**
	 * populate DocUploadRequestDetail
	 * 
	 * @param requestDetailsDTO
	 * @param docUploadHistoryDTO
	 * @return
	 */
	private DocUploadHistoryDTO populateDocUploadRequestDetail(DocUploadRequest requestDetailsDTO,
			DocUploadHistoryDTO docUploadHistoryDTO) {
		docUploadHistoryDTO = new DocUploadHistoryDTO();
		docUploadHistoryDTO.setStatus(populateDocUploadRequestStatus(requestDetailsDTO.getStatus()));
		docUploadHistoryDTO.setTotalUserCount(requestDetailsDTO.getTotalUserCount());
		docUploadHistoryDTO.setSuccessUserCount(requestDetailsDTO.getSuccessUserCount());
		docUploadHistoryDTO.setFailedUserCount(requestDetailsDTO.getFailedUserCount());
		docUploadHistoryDTO.setFein(requestDetailsDTO.getFein());
		docUploadHistoryDTO.setSalesForceId(requestDetailsDTO.getSalesForceId());
		docUploadHistoryDTO.setDocStatus(String.join(", ", requestDetailsDTO.getDocStatus()));
		docUploadHistoryDTO.setUpdatedBy(requestDetailsDTO.getUpdatedBy());
		docUploadHistoryDTO.setUpdatedOn(requestDetailsDTO.getUpdatedOn());
		return docUploadHistoryDTO;
	}
	
	/**
	 * populate DocUploadRequestStatus
	 * @param status
	 * @return
	 */
	private String populateDocUploadRequestStatus(DocUploadRequestStatus status) {
		String docUploadStatus =status.toString();
		switch (docUploadStatus) {
        case "FAILED":
        	docUploadStatus = "Failure / Partial Failure";
            break;
        case "CREATED":
        	docUploadStatus = "Created";
            break;
        case "SUCCESS":
        	docUploadStatus = "Success";
            break;
        case "IN_PROGRESS":
        	docUploadStatus = "In Progress";
            break;
        case "COMPLETED":
        	docUploadStatus = "Completed";
            break;
        case "REPROCESS":
        	docUploadStatus = "Reprocess";
            break;           
        default:
        	docUploadStatus = "Created";
            break;
    }
		return docUploadStatus;
	}
	
	/**
	 * export Doc Upload Request Details
	 * 
	 * @param exportType
	 * @param searchRequest
	 * @param pageable
	 * @param resourceLoader
	 * @param httpResponse
	 * @throws IOException
	 */
	@Override
	public void exportDocUploadRequestDetailsInfo(String exportType, SearchRequest searchRequest, Pageable page,
			HttpServletResponse response) throws IOException {
		List<DocUploadRequestDetails> docUploadRequestDetails = docUploadRequestRepositoryCustom
				.fetchAllDocUploadRequestDetailsInfo(searchRequest, page);
		for (DocUploadRequestDetails docUploadRequest : docUploadRequestDetails) {
			if(CollectionUtils.isNotEmpty(docUploadRequest.getTemplate())) {
				for(DocTemplateDetails docUploadTemplate : docUploadRequest.getTemplate()) { 
					if(ConstantUtils.FAILED.equalsIgnoreCase(docUploadTemplate.getStatus()) && StringUtils.isNotBlank(docUploadTemplate.getReason()) && StringUtils.isNotEmpty(docUploadTemplate.getReason().trim())) {
						docUploadRequest.setFailureReason(docUploadTemplate.getReason());
					}
				}
			}
		}
		ReportUtils.generateReport(exportType, docUploadRequestDetails, response,
				this.resourceLoader, ConstantUtils.FILENAME_DOCUPLOADREQUESTDETAILS,null,ConstantUtils.DOCUPLOADREQUESTDETAILS_EXPORT);
	}

}
