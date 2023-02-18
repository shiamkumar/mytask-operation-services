package com.ghx.api.operations.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.MoveUserRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.exception.SystemException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.MoveUserRequest;
import com.ghx.api.operations.repository.MoveUserRepository;
import com.ghx.api.operations.repository.MoveUserRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.validation.business.MergeSupplierBusinessValidator;
import com.ghx.api.operations.validation.business.MoveUserBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoException;

/**
 * This class MoveUserServiceImpl
 * 
 * @author Ananth Kandasamy
 *
 */
@Component
public class MoveUserServiceImpl implements MoveUserService {

    /** Constant logger */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(MoveUserServiceImpl.class);

    /** intialize MoveUserBusinessValidator */
    @Autowired
    private MoveUserBusinessValidator moveUserBusinessValidator;

    /** intialize MoveUserRepository */
    @Autowired
    private MoveUserRepository moveUserRepository;

    /** intialize ModelMapper */
    private final transient ModelMapper modelMapper = new ModelMapper();

    /** initialize operationsutil */
    @Autowired
    private transient OperationsUtil operationsUtil;

    /** initialize mergeSupplierBusinessValidator */
    @Autowired
    private MergeSupplierBusinessValidator mergeSupplierBusinessValidator;

    /** intialize MoveUserRepositoryCustom */
    @Autowired
    private MoveUserRepositoryCustom moveUserRepositoryCustom;
    
    /** initalize ReportUtils */
    @Autowired
    private transient ReportUtils reportUtils;

    /** initalize ResourceLoader */
    @Autowired
    private transient ResourceLoader resourceLoader;

    /** export limit for move user */
    @Value("${export.limit.move-user}")
    private int exportMoveUserLimit;

    /**
     * Created new Move User Request
     * @param id
     * @param moveUserRequestDTO
     * @return String
     */
    @Override
    @LogExecutionTime
    public String createMoveUserRequest(String id, MoveUserRequestDTO moveUserRequestDTO) {
        moveUserBusinessValidator.validateSaveRequestParams(moveUserRequestDTO);
        moveUserBusinessValidator.validateDomainAndEmail(moveUserRequestDTO);
        moveUserBusinessValidator.populateMoveUser(id, moveUserRequestDTO);
        MoveUserRequest moveUserRequest = modelMapper.map(moveUserRequestDTO, MoveUserRequest.class);
        moveUserRequest.setPersisted(true);
        moveUserBusinessValidator.validateDuplicateMoveUserRequest(id);
        moveUserRequest = moveUserRepository.save(moveUserRequest);
        LOGGER.info("Successfully stored move user request for UserOid: {}, MoveRequestId:{} ", moveUserRequest.getUserOid(),
                moveUserRequest.getId());
        String publishStatus = operationsUtil.publishMoveUserMessage(moveUserRequest.getId(), ConstantUtils.OPERATIONS);
        if(StringUtils.equalsIgnoreCase(publishStatus, ConstantUtils.ERROR)) {
            moveUserRepository.deleteById(moveUserRequest.getId());
            LOGGER.error("createMoveUserRequest:: move user job sqs publish failed. deleted move user request for userOid {} ",id);
            throw new SystemException(CustomMessageSource.getMessage(ErrorConstants.MOVE_USER_SQS_PUBLISH_FAILED));
        }
        return moveUserRequest.getId();
    }

    /**
     * Returns the List of Move User Requests matching the Search Criteria
     * @param searchRequest
     * @param pageable
     * @return map
     */
    @Override
    @LogExecutionTime
    public Map<String, Object> getAllMoveRequests(SearchRequest searchRequest, Pageable pageable) {
        mergeSupplierBusinessValidator.validateDateParams(searchRequest.getSubmittedDateFrom(), searchRequest.getSubmittedDateTo());
        Map<String, Object> moveRequestDetails = new HashMap<>();
        try {
            List<MoveUserRequestDTO> allMoveRequests = moveUserRepositoryCustom.findAllMoveRequests(searchRequest, pageable);
            long moveRequestsCount = moveUserRepositoryCustom.findMoveRequestsCount(searchRequest);
            
            moveRequestDetails.put(ConstantUtils.MOVE_REQUESTS, allMoveRequests);
            moveRequestDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, moveRequestsCount);

            List<MoveUserRequestDTO> moveRequestList = Arrays
                    .asList(new ObjectMapper().convertValue(allMoveRequests, MoveUserRequestDTO[].class));
            LOGGER.info("getAllMoveRequests:: Move Requests fetched: {}, total requests found: {}", moveRequestList.size(), moveRequestsCount);
        } catch (MongoException ex) {
            LOGGER.error("getAllMoveRequests:: Exception occurred while fetching Move User Requests");
            throw new BusinessException(ex);
        }
        return moveRequestDetails;
    }

    /**
     * Export Move User requests, Supported type - PDF,XLS,CSV
     * @param exportType
     * @param searchRequest
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public void exportMoveUserRequests(String exportType, SearchRequest searchRequest, HttpServletResponse response) throws IOException {
        searchRequest.setPageable(searchRequest.getPageable().getPageSize() >= exportMoveUserLimit
                ? PageRequest.of(searchRequest.getPageable().getPageNumber(), exportMoveUserLimit, searchRequest.getPageable().getSort())
                : searchRequest.getPageable());
        List<MoveUserRequestDTO> moveRequests = (List<MoveUserRequestDTO>) getAllMoveRequests(searchRequest, searchRequest.getPageable())
                .get(ConstantUtils.MOVE_REQUESTS);
        reportUtils.exportMoveUserRequests(exportType, response, moveRequests, resourceLoader, ConstantUtils.MOVE_USER_REQUEST_FILENAME);
    }

}
