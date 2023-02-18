package com.ghx.api.operations.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ghx.api.operations.dto.UserDeleteRequestDTO;
import com.ghx.api.operations.dto.UserDeleteRequestReasonDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.exception.ResourceNotFoundException;
import com.ghx.api.operations.repository.UserDeleteRepositoryCustom;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.mongodb.MongoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import com.ghx.api.operations.dto.BlobDTO;
import com.ghx.api.operations.dto.ExportDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.UserDeleteRequestValidationDTO;
import com.ghx.api.operations.dto.UserDetailsInfo;
import com.ghx.api.operations.dto.UserValidationResponseDTO;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.UserDeleteRequest;
import com.ghx.api.operations.repository.DocumentRepositoryCustom;
import com.ghx.api.operations.repository.UserDeleteRequestRepository;
import com.ghx.api.operations.repository.UserVMRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.FileUtils;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.util.SecurityUtils;
import com.ghx.api.operations.util.UserDeleteRequestUtil;

/**
 * 
 * @author Ajith
 * 
 *         The class UserDeleteRequestServiceImpl - the class which holds all
 *         the business layer of user delete request
 *
 */
@Service
public class UserDeleteRequestServiceImpl implements UserDeleteRequestService {
	
	/** users delete limit */
    @Value("${users.delete.limit}")
    public int usersDeleteLimit;
	
	/** The Constant LOGGER. */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(UserDeleteRequestServiceImpl.class);
	
	
	/**Initialize  UserDeleteRepositoryCustom*/
	@Autowired
	private UserDeleteRepositoryCustom userDeleteRepositoryCustom;
	
    /** The DocumentRepositoryCustom */
    @Autowired
    private transient DocumentRepositoryCustom documentRepositoryCustom;
    
    /** The UserVMRepositoryCustom */
    @Autowired
    private transient UserVMRepositoryCustom userVMRepositoryCustom;
    
    /** The securityUtils to getCurrent loggin user    */
    @Autowired
    private SecurityUtils securityUtils;
    
    /** The class userDeleteRequestRepository */
    @Autowired
    private transient UserDeleteRequestRepository userDeleteRequestRepository;
    
    /** initalize ResourceLoader */
    @Autowired
    private transient ResourceLoader resourceLoader;
    
	/** The OperationsUtil */
    @Autowired
    private transient OperationsUtil operationsUtil;
    
	/**
	 * save UserDeleteRequest 
	 * @UserDeleteRequestDTO
	 * salesForceId
	 * mongoKey
	 */
	@Override
	public UserDeleteRequest saveUserDeleteRequest(UserDeleteRequestDTO deleteRequestDTO) {
		LOGGER.info("saveUserDeleteRequest started");
		BlobDTO blobDTO = documentRepositoryCustom.getBlob(deleteRequestDTO.getMongoKey(), ConstantUtils.USER_DELETE_REQUEST_FILES);
		UserDeleteRequestUtil.validateMimeType(blobDTO.getMimeType());
		InputStream inputStream = new ByteArrayInputStream(blobDTO.getData());
		Sheet worksheet = prepareWorkSheet(blobDTO.getMimeType(), inputStream);
		UserDeleteRequestUtil.validateWorkSheet(worksheet, usersDeleteLimit);
		Set<String> userIds = populateUsers(worksheet);
		UserDeleteRequestUtil.validateEmptyUsers(userIds);
		List<Map<String, Object>> userDetails = userVMRepositoryCustom.fetchUserDetails(userIds);
		UserDeleteRequest userDeleteRequestDTO = populateUserDeleteRequestDTO(blobDTO, deleteRequestDTO, userDetails);
		userDeleteRequestRepository.save(userDeleteRequestDTO);
		return userDeleteRequestDTO;
	}
	
	/**
	 * populate userDeleteRequest
	 * @param blobDTO 
	 * @param deleteRequestDTO
	 * @param enhanceUserDetails
	 * @param userIds
	 * @return
	 */
	private UserDeleteRequest populateUserDeleteRequestDTO(BlobDTO blobDTO, UserDeleteRequestDTO deleteRequestDTO, List<Map<String, Object>> userDetails) {
		UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
		List<UserDetailsInfo> saveUserDetails = new ArrayList<>();
		enhanceUserDetailsForSave(userDetails, saveUserDetails);
		userDeleteRequest.setId(UUID.randomUUID().toString());
		userDeleteRequest.setSalesForceId(deleteRequestDTO.getSalesForceId());
		userDeleteRequest.setMongoKey(deleteRequestDTO.getMongoKey());
		userDeleteRequest.setUserDetails(saveUserDetails);
		userDeleteRequest.setUploadedBy(securityUtils.getCurrentUser());
		userDeleteRequest.setUploadedOn(blobDTO.getCreatedOn());
		userDeleteRequest.setUpdatedBy(securityUtils.getCurrentUser());
		userDeleteRequest.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
		userDeleteRequest.setTotalCount(saveUserDetails.size());
		return userDeleteRequest;
	}

	private void enhanceUserDetailsForSave(List<Map<String, Object>> userDetails, List<UserDetailsInfo> saveUserDetails) {
			userDetails.forEach(userDetail ->{
				UserDetailsInfo saveUserDetail = new UserDetailsInfo();
				saveUserDetail.setFirstName(userDetail.get(ConstantUtils.FIRST_NAME).toString());
				saveUserDetail.setLastName(userDetail.get(ConstantUtils.LAST_NAME).toString());
				saveUserDetail.setUserId(userDetail.get(ConstantUtils.USER_ID).toString());
				saveUserDetail.setUserOid(userDetail.get(ConstantUtils.USER_OID).toString());
				saveUserDetails.add(saveUserDetail);
			});
	}

	@SuppressWarnings("resource")
	private Sheet prepareWorkSheet(String mimeType, InputStream inputStream) {
        Sheet worksheet = null;
        try {
            if (mimeType.equalsIgnoreCase(ConstantUtils.XLS_MIMETYPE)) {
                Workbook workbook = new HSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            } else {
                Workbook workbook = new XSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            }
        } catch (IOException exception) {
        	LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
        }
        return worksheet;
    }

	/**
	 * get all user delete requests
	 * @param Pageable
	 */
	@Override
	public Map<String, Object> getAllDeleteRequests(Pageable pageable) {
		Map<String, Object> userDeleteRequestDetails = new HashMap<>();
		try {
			List<UserDeleteRequestDTO> allUserDeleteRequests = userDeleteRepositoryCustom
					.findAllUserDeleteRequests(pageable);
			long userDeleteRequestCount = userDeleteRepositoryCustom.findUserDeleteRequestCount();
			userDeleteRequestDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, userDeleteRequestCount);
			userDeleteRequestDetails.put(ConstantUtils.MASS_USER_DELETE_REQUEST, allUserDeleteRequests);
			LOGGER.info(" User delete Requests Fetched: {}, totalcount: {} " , allUserDeleteRequests.size(), userDeleteRequestCount);
		} catch (MongoException e) {
			LOGGER.error("Error occured while fetch all user delete requests ", e);
			throw new BusinessException(e);
		}
		return userDeleteRequestDetails;
	}

    /**
     * precheck User Delete Request
     */
    @LogExecutionTime
    @Override
    public UserValidationResponseDTO precheckUserDeleteRequest(String mongoKey) {

        LOGGER.info("precheckUserDeleteRequest starts for mongoKey - {} at {} ", mongoKey, System.currentTimeMillis());

        BlobDTO blobDTO = documentRepositoryCustom.getBlob(mongoKey, ConstantUtils.USER_DELETE_REQUEST_FILES);
        UserDeleteRequestUtil.validateMimeType(blobDTO.getMimeType());

        InputStream inputStream = new ByteArrayInputStream(blobDTO.getData());

        Sheet worksheet = FileUtils.prepareWorkSheet(blobDTO.getMimeType(), inputStream);
        UserDeleteRequestUtil.validateWorkSheet(worksheet, usersDeleteLimit);

        Set<String> usersToBeDeletedSet = populateUsers(worksheet);
        UserDeleteRequestUtil.validateEmptyUsers(usersToBeDeletedSet);

        List<UserDeleteRequestValidationDTO> userDeleteRequestValidationDTOList = new ArrayList<>();
        List<Map<String, Object>> userDetails = userVMRepositoryCustom.fetchUserDetails(usersToBeDeletedSet);

        populateUserDetails(usersToBeDeletedSet, userDetails, userDeleteRequestValidationDTOList);

        UserValidationResponseDTO userValidationResponseDTO = new UserValidationResponseDTO();
        userValidationResponseDTO.setUserValidationResponseDTOList(userDeleteRequestValidationDTOList);
        userValidationResponseDTO.setAvailableUserCount(
                (int) userDeleteRequestValidationDTOList.stream().filter(UserDeleteRequestValidationDTO::isUserExists).count());
        userValidationResponseDTO.setTotalUserCount(userDeleteRequestValidationDTOList.size());
        LOGGER.info("precheckUserDeleteRequest ends for mongoKey -{} at {} ", mongoKey, System.currentTimeMillis());
        return userValidationResponseDTO;
    }

    /**
     * 
     * @param usersToBeDeletedSet
     * @param userDetails
     * @param userDeleteRequestValidationDTOList
     */
    private void populateUserDetails(Set<String> usersToBeDeletedSet, List<Map<String, Object>> userDetails,
            List<UserDeleteRequestValidationDTO> userDeleteRequestValidationDTOList) {
        usersToBeDeletedSet.forEach(user -> {
            UserDeleteRequestValidationDTO userDeleteRequestValidationDTO = new UserDeleteRequestValidationDTO();
            if (CollectionUtils.isEmpty(userDetails)) {
                userDeleteRequestValidationDTO.setUserExists(false);
                userDeleteRequestValidationDTO.setUserId(user);
            } else {
                userDetails.forEach(userDetail -> populateUserDetail(user, userDeleteRequestValidationDTO, userDetail));
            }
            userDeleteRequestValidationDTOList.add(userDeleteRequestValidationDTO);
        });
    }

    /**
     * @param user
     * @param userDeleteRequestValidationDTO
     * @param userDetail
     */
    private void populateUserDetail(String user, UserDeleteRequestValidationDTO userDeleteRequestValidationDTO, Map<String, Object> userDetail) {
        if (MapUtils.getString(userDetail, ConstantUtils.USER_ID).equalsIgnoreCase(user)) {
            userDeleteRequestValidationDTO.setUserExists(true);
            userDeleteRequestValidationDTO.setUserId(user);
            userDeleteRequestValidationDTO.setPaidUser(MapUtils.getBoolean(userDetail, ConstantUtils.PAID_USER));
            userDeleteRequestValidationDTO.setFein(MapUtils.getString(userDetail, ConstantUtils.FEIN));
            userDeleteRequestValidationDTO.setVendorName(MapUtils.getString(userDetail, ConstantUtils.VENDOR_NAME));
            return;
        }
        if (Objects.isNull(userDeleteRequestValidationDTO.getFein())) {
            userDeleteRequestValidationDTO.setUserExists(false);
            userDeleteRequestValidationDTO.setUserId(user);
        }
    }

    /**
     * 
     * @param worksheet
     * @return
     */
    private Set<String> populateUsers(Sheet worksheet) {
        Set<String> usersToBeDeletedSet = new LinkedHashSet<>();
        if (Objects.nonNull(worksheet)) {
            worksheet.forEach(row -> {
                if (row.getRowNum() == ConstantUtils.ZERO_INDEX) {
                    return;
                }
                String userId = populateUserId(row);
                if (StringUtils.isNotBlank(userId)) {
                    usersToBeDeletedSet.add(userId);
                }
            });
        }
        return usersToBeDeletedSet;
    }

    /**
     * 
     * @param row
     * @return
     */
    private String populateUserId(Row row) {
        String userId = null;
        DataFormatter formatter = new DataFormatter();
        if (BooleanUtils.isFalse(FileUtils.isRowEmpty(row))) {
            userId = formatter.formatCellValue(row.getCell(0)).trim().toLowerCase(Locale.getDefault());
        }
        return userId;
    }
    
    /**
     * Publish SQS event to process retry delete user request in real time.
     * 
     */
    @Override
    public String publishEventToRetryRequest(String id) {
        UserDeleteRequest userDeleteRequest = userDeleteRequestRepository.findByIdAndStatus(id);
        if(Objects.isNull(userDeleteRequest)) {
            LOGGER.error(" PublishEventToRetryRequest ::: Resource not exists ::: {}",id);
            throw new ResourceNotFoundException(CustomMessageSource.getMessage(ErrorConstants.RESOURCE_NOT_FOUND));
        }
        userDeleteRequest.setUpdatedBy(securityUtils.getCurrentUser());
        userDeleteRequest.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        userDeleteRequestRepository.save(userDeleteRequest);
        operationsUtil.publishToUniversalScheduler(userDeleteRequest.getId(), ConstantUtils.RETRY_MASS_USER_DELETE);
        return ConstantUtils.EVENT_PUBLISHED;
    }

	/**
	 * get users details by id
	 * 
	 * @param searchRequest Example: status = ALL
	 * @param pageable      Example: page = 0, size = 1
	 */
	@Override
	public Map<String, Object> getUserDetailRequestsById(SearchRequest searchRequest, Pageable pageable) {
		Map<String, Object> userDetails = new HashMap<>();
		try {
			Optional<UserDeleteRequest> userDeleteRequestOptional = userDeleteRequestRepository
					.findById(searchRequest.getOid());
			if (userDeleteRequestOptional.isPresent() && !StringUtils.equalsIgnoreCase(ConstantUtils.DELETED,
					userDeleteRequestOptional.get().getStatus())) {
				List<UserDetailsInfo> repDetailsDTOs = userDeleteRepositoryCustom.fetchAllUserDetails(searchRequest,
						pageable);
				long userDetailsCount = repDetailsDTOs.size() > 0 ? userDeleteRepositoryCustom.findUserDetailsCount(searchRequest) : 0;
				userDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, userDetailsCount);
				userDetails.put(ConstantUtils.USER_DETAIL_RESPONSE, repDetailsDTOs);
				LOGGER.info(" User details Fetched: {}, totalcount: {}, user delte request id: {} ",
						repDetailsDTOs.size(), userDetailsCount, searchRequest.getOid());
			} else {
				throw new ResourceNotFoundException(CustomMessageSource.getMessage(ErrorConstants.RESOURCE_NOT_FOUND));
			}
		} catch (BusinessException e) {
			LOGGER.error("Exception occured while get user detils for request id: {} ", searchRequest.getOid());
			throw new BusinessException(e);
		}
		return userDetails;
	}
	
    /**
     * delete User Delete Request
     */
    @Override
    @LogExecutionTime
    public String deleteUserDeleteRequest(String id, UserDeleteRequestReasonDTO deleteRequestReason) {
        Optional<UserDeleteRequest> userDeleteRequestOptional = userDeleteRequestRepository.findById(id);
        if (!userDeleteRequestOptional.isPresent()) {
            throw new ResourceNotFoundException(CustomMessageSource.getMessage(ErrorConstants.RESOURCE_NOT_FOUND));
        }
        UserDeleteRequest userDeleteRequest = userDeleteRequestOptional.get();
        if (!StringUtils.equalsIgnoreCase(ConstantUtils.UPLOADED, userDeleteRequest.getStatus())) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.REQUEST_ALREADY_IN_PROGRESS));
        }
        userDeleteRequest.setStatus(ConstantUtils.DELETED);
        userDeleteRequest.setDeletedReason(deleteRequestReason.getReason());
        userDeleteRequest.setDeletedBy(securityUtils.getCurrentUser());
        userDeleteRequest.setDeletedOn(new Timestamp(System.currentTimeMillis()));
        userDeleteRequestRepository.save(userDeleteRequest);
        return ConstantUtils.DELETED_SUCCESSFULLY;
    }

    /** 
	 * export user delete reqeust userDetails
	 * @param exportType
	 * @param searchRequest
	 * @param pageable
	 * @param resourceLoader
	 * @param httpResponse
     * @return 
	 * @throws IOException 
	 */
	@Override
	public ExportDTO exportUserDeleteUserDetailsInfo(String exportType, SearchRequest searchRequest, Pageable page,
			HttpServletResponse response) throws IOException {
		List<UserDetailsInfo> repDetailsDTOs = userDeleteRepositoryCustom.fetchAllUserDetails(searchRequest, page);
		return ReportUtils.generateUserDeleteRequestReport(exportType, repDetailsDTOs, response, this.resourceLoader,
				ConstantUtils.FILENAME_USERDELETEREQUEST);
	}
}
