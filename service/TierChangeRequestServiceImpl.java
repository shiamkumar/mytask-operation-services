package com.ghx.api.operations.service;

import static com.ghx.api.operations.messagesource.CustomMessageSource.getMessage;
import static com.ghx.api.operations.util.ErrorConstants.TIERCHANGE_REQUEST_NOT_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.AccountsDTO;
import com.ghx.api.operations.dto.IdnsResponseDTO;
import com.ghx.api.operations.dto.ProviderDetailsDTO;
import com.ghx.api.operations.dto.SupplierDetailsDTO;
import com.ghx.api.operations.dto.TierChangeRequesFetchAllResponseDTO;
import com.ghx.api.operations.dto.TierChangeRequestDTO;
import com.ghx.api.operations.dto.TierChangeRequestDetailsDTO;
import com.ghx.api.operations.dto.TierChangeRequestSearchDTO;
import com.ghx.api.operations.dto.TierChangeRequestUpdateDTO;
import com.ghx.api.operations.dto.TierChangeRequestValidationResponseDTO;
import com.ghx.api.operations.enums.TierChangeRequestStatus;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.exception.SystemException;
import com.ghx.api.operations.feign.client.ProfileServiceClient;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.model.PricingTierConfig;
import com.ghx.api.operations.model.TierChangeRequest;
import com.ghx.api.operations.repository.LookupRepository;
import com.ghx.api.operations.repository.PricingConfigRepository;
import com.ghx.api.operations.repository.TierChangeRepositoryCustom;
import com.ghx.api.operations.repository.TierChangeRequestRepository;
import com.ghx.api.operations.repository.UserVMRepositoryCustom;
import com.ghx.api.operations.repository.VCRelationRepositoryCustom;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.DateUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.util.SecurityUtils;
import com.ghx.api.operations.util.TierChangeRequestMessageServiceUtil;
import com.ghx.api.operations.util.TierChangeRequestUtil;
import com.ghx.api.operations.validation.business.TierChangeRequestBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.messagecenter.client.dto.message.MessageDTO;
import com.ghx.messagecenter.client.dto.message.MessageResponseDTO;
import com.ghx.messagecenter.client.dto.message.RecipientDetailDTO;
import com.ghx.messagecenter.client.http.MessageServiceClient;
import com.mongodb.MongoException;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @category Service Implementation
 * @since 18/08/2021
 */
@Component
public class TierChangeRequestServiceImpl implements TierChangeRequestService {

    /** Logger Instance */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TierChangeRequestServiceImpl.class);

    /** tierChangeRequestUtil */
    @Autowired
    private TierChangeRequestUtil tierChangeRequestUtil;

    /** Tier Change Request Repository Instance */
    @Autowired
    private transient TierChangeRequestRepository tierchangeRequestRepository;

    /** intialize TierChangeRepositoryCustom */
    @Autowired
    private TierChangeRepositoryCustom tierChangeRepositoryCustom;

    /** Tier Change Request Validator Instance */
    @Autowired
    private TierChangeRequestBusinessValidator tierChangeRequestBusinessValidator;

    /** intialize ProfileServiceClient */
    @Autowired
    private transient ProfileServiceClient profileServiceClient;

    /** The lookup repository. */
    @Autowired
    private transient LookupRepository lookupRepository;

    /** The pricing config repository. */
    @Autowired
    private PricingConfigRepository pricingConfigRepository;

    /** The vendor detail repository. */
    @Autowired
    private VendorDetailRepositoryCustom vendorDetailRepositoryCustom;

    /** Mapper Instance */
    @Autowired
    private transient MapperFacade mapper;

    /** Security Util Instance Object */
    @Autowired
    private SecurityUtils securityUtils;
    
    /** Operations Util Instance Object */
    @Autowired
    private OperationsUtil operationsUtil;

    /** vcRelationCustomRepository Instance */
    @Autowired
    private VCRelationRepositoryCustom vcRelationRepository;

    /** The UserVMRepositoryCustom */
    @Autowired
    private transient UserVMRepositoryCustom userVMRepository;

    /** MessageService Client */
    @Autowired
    private MessageServiceClient messageServiceClient;

    /** Tier Request Message service util Instance */
    @Autowired
    private transient TierChangeRequestMessageServiceUtil tierRequestMessageServiceUtil;
    
    /**
     * Fetches the TierChange Request Details
     * @param tierChangeRequestId
     * @return Tier Change Request Details
     */
    @SuppressWarnings("unchecked")
    @Override
    @LogExecutionTime
    public TierChangeRequestDetailsDTO getTierChangeRequestById(String tierChangeRequestId) {
        TierChangeRequest tierDetails = tierchangeRequestRepository.findById(tierChangeRequestId)
                .orElseThrow(() -> new BusinessException(getMessage(TIERCHANGE_REQUEST_NOT_FOUND)));
        TierChangeRequestDetailsDTO details = mapper.map(tierDetails, TierChangeRequestDetailsDTO.class);
        Map<String, Object> paidUsersResponse = new HashMap<>();
        Map<String, Object> idnsResponse = new HashMap<>();
        try {
            paidUsersResponse = profileServiceClient.searchUsers(ConstantUtils.USER_STATUS_ACTIVE, ConstantUtils.PAID,
                    details.getSupplier().getFein(), PageRequest.of(0, 10));
        } catch (Exception e) {
            LOGGER.error(getMessage(ErrorConstants.FETCH_REPS_ERROR));
            throw new BusinessException(getMessage(ErrorConstants.FETCH_REPS_ERROR));
        }
        details.setRepCount(MapUtils.getIntValue(paidUsersResponse, ConstantUtils.TOTAL_NO_OF_RECORDS));
        if (!StringUtils.equalsIgnoreCase(details.getStatus().getStatus(), ConstantUtils.COMPLETED)) {
            try {
            	int idnCount = vcRelationRepository.getIDNCount(details.getSupplier().getOid(), true);
				if (idnCount > 0) {
					idnsResponse = profileServiceClient.getUsersAccounts(details.getSupplier().getOid(),
							PageRequest.of(0, idnCount), ConstantUtils.PAID);
				}
            } catch (Exception e) {
                LOGGER.error(getMessage(ErrorConstants.FETCH_IDNS_ERROR));
                throw new BusinessException(getMessage(ErrorConstants.FETCH_IDNS_ERROR));
            }
            details.setIdnCountBefore(MapUtils.getIntValue(idnsResponse, ConstantUtils.TOTAL_NO_OF_RECORDS));
            List<AccountsDTO> accountsList = Objects.nonNull(MapUtils.getObject(idnsResponse, ConstantUtils.IDNS_LIST))
                    ? Arrays.asList(new ObjectMapper().convertValue(MapUtils.getObject(idnsResponse, ConstantUtils.IDNS_LIST), AccountsDTO[].class))
                    : ListUtils.EMPTY_LIST;
            List<String> idnsList = CollectionUtils.isNotEmpty(accountsList)
                    ? accountsList.stream().map(AccountsDTO::getCustomerOid).distinct().collect(Collectors.toList())
                    : ListUtils.EMPTY_LIST;
            List<String> idn = ListUtils.subtract(idnsList,
                    CollectionUtils.isNotEmpty(details.getEliminatedIdns()) ? details.getEliminatedIdns() : ListUtils.EMPTY_LIST);
            details.setIdnCountAfter(idn.size());
        }
        // CREDMGR-79519 to show both paid and unpaid count
        populateTotalIDNCount(details);
        Map<String, Object> vendorDetails = vendorDetailRepositoryCustom.getDetailsByVendorOid(details.getSupplier().getOid());
        details.setSupplierCurrentTierCode(MapUtils.getString(vendorDetails, ConstantUtils.TIER_PLAN_CODE));
        details.setSupplierStatus(MapUtils.getString(vendorDetails, ConstantUtils.VENDORSTATUS));
        return details;
    }

	@SuppressWarnings("unchecked")
	private void populateTotalIDNCount(TierChangeRequestDetailsDTO details) {
		Map<String, Object> totalIDNCount = new HashMap<>();
		if (!StringUtils.equalsIgnoreCase(details.getStatus().getStatus(), ConstantUtils.COMPLETED)) {
			try {
				int idnCount = vcRelationRepository.getIDNCount(details.getSupplier().getOid(), false);
				if (idnCount > 0) {
					totalIDNCount = profileServiceClient.getUsersAccounts(details.getSupplier().getOid(),
							PageRequest.of(0, idnCount), ConstantUtils.ALL);
				}
			} catch (Exception e) {
				LOGGER.error(getMessage(ErrorConstants.FETCH_IDNS_ERROR));
				throw new BusinessException(getMessage(ErrorConstants.FETCH_IDNS_ERROR));
			}
			details.setTotalIDNCountBefore(MapUtils.getIntValue(totalIDNCount, ConstantUtils.TOTAL_NO_OF_RECORDS));
			List<AccountsDTO> totalAccountsList = Objects
					.nonNull(MapUtils.getObject(totalIDNCount, ConstantUtils.IDNS_LIST))
							? Arrays.asList(new ObjectMapper().convertValue(
									MapUtils.getObject(totalIDNCount, ConstantUtils.IDNS_LIST), AccountsDTO[].class))
							: ListUtils.EMPTY_LIST;
			List<String> totalIDNsList = CollectionUtils.isNotEmpty(totalAccountsList) ? totalAccountsList.stream()
					.map(AccountsDTO::getCustomerOid).distinct().collect(Collectors.toList()) : ListUtils.EMPTY_LIST;
			List<String> totalIDN = ListUtils.subtract(totalIDNsList,
					CollectionUtils.isNotEmpty(details.getEliminatedIdns()) ? details.getEliminatedIdns()
							: ListUtils.EMPTY_LIST);
			details.setTotalIDNCountAfter(totalIDN.size());
		}
	}

    /**
     * Delete service implementation for Tier Change Request
     */
    @Override
    @LogExecutionTime
    public void deleteById(String id) {
        Optional<TierChangeRequest> optional = tierchangeRequestRepository.findById(id);
        if (optional.isPresent()) {
            if (!StringUtils.equals(optional.get().getStatus().getStatus(), TierChangeRequestStatus.PENDING.getStatus())) {
                throw new BusinessException(getMessage(ErrorConstants.TIER_CHANGE_REQUEST_CANNOT_BE_DELETED));
            }
            tierchangeRequestRepository.deleteById(id);
            LOGGER.info("TierChangeRequest DELETED {} ", id);
        } else {
            throw new BusinessException(getMessage(ErrorConstants.TIER_CHANGE_REQUEST_ID_NOT_FOUND));
        }
    }

    /**
     * Saves the given Tier change request Details
     * @param tierChangeRequestDTO
     * @return
     */
    @Override
    @LogExecutionTime
    public String create(TierChangeRequestDTO tierChangeRequestDTO) {
        
        tierChangeRequestBusinessValidator.validateSaveRequestParams(tierChangeRequestDTO);
        tierChangeRequestBusinessValidator.checkAlreadyExists(tierChangeRequestDTO);
        SupplierDetailsDTO supplierDetailsDTO = tierChangeRequestUtil.validateSupplierDetails(tierChangeRequestDTO.getSupplier().getOid());
        tierChangeRequestBusinessValidator.isEqualCurrentAndRequestedTier(tierChangeRequestDTO.getRequestedTierCode(),supplierDetailsDTO.getTierCode());
        tierChangeRequestBusinessValidator.validateRequestedTier(tierChangeRequestDTO);
        TierChangeRequest tierChangeRequest = mapper.map(tierChangeRequestDTO, TierChangeRequest.class);
        tierChangeRequestUtil.populateTierChangeRequest(tierChangeRequest, supplierDetailsDTO);
        tierChangeRequest = tierchangeRequestRepository.save(tierChangeRequest);
        sendPendingRequestMail(tierChangeRequest);
        return tierChangeRequest.getId();
    }

    private void sendPendingRequestMail(TierChangeRequest tierChangeRequest) {
        try {
            List<Map<String, String>> impactedRepsData = vcRelationRepository.getTierRequestMessageDetails(tierChangeRequest.getEliminatedIdns(),
                    tierChangeRequest.getSupplier().getOid());
            String requestorName = userVMRepository.getTierChangeRequestorName(tierChangeRequest.getCreatedBy());
            List<RecipientDetailDTO> receipients = new ArrayList<>();
            impactedRepsData.forEach(rep -> {
                List<String> idnNames = Arrays.asList(rep.get(ConstantUtils.IDNS_LIST).split(ConstantUtils.COMMA, -1));
                String emailId = rep.get(ConstantUtils.EMAIL_ID);
                String receiverName = StringUtils.join(rep.get(ConstantUtils.FIRST_NAME), StringUtils.SPACE, rep.get(ConstantUtils.LAST_NAME));
                rep.remove(ConstantUtils.EMAIL_ID);
                String finalIdnContent = StringUtils.EMPTY;
                for (String idn : idnNames) {
                    finalIdnContent = StringUtils.join(finalIdnContent, StringUtils.join("<li>", idn, "</li>"));
                }

                rep.remove(ConstantUtils.IDNS_LIST);
                rep.put(ConstantUtils.IDN_DETAILS, StringUtils.join("<ul>", finalIdnContent, "</ul>"));
                rep.put(ConstantUtils.REQUESTOR_NAME, requestorName);
                RecipientDetailDTO recipient = RecipientDetailDTO.builder().messageVariable(rep).receiverId(emailId).receiverName(receiverName)
                        .build();
                receipients.add(recipient);
            });
            List<MessageDTO> messageRequest = tierRequestMessageServiceUtil.populateDowngradePendingRequest(receipients);

            List<MessageResponseDTO> messageResponse = messageServiceClient.sendMessage(messageRequest);
            if (CollectionUtils.isNotEmpty(messageResponse) && CollectionUtils.isNotEmpty(messageResponse.get(0).getReceivers())) {
                // Mail sent to impacted reps successfully
                LOGGER.info("Mail sent to Impacted Reps for Pending Tier Downgrade Request : {}, group message Id : {}, number of reps : {}",
                        tierChangeRequest.getId(), messageResponse.get(0).getGroupMessageId(), messageResponse.get(0).getReceivers().size());
            } else {
                LOGGER.info("Error occurred while sending mail Impacted Reps for Pending Tier Downgrade Request : {}", tierChangeRequest.getId());
            }
        } catch (Exception ex) {
            LOGGER.error("Send Mail to Impacted Reps for Pending Tier Downgrade Request {} Failed Exception {} ::: ", tierChangeRequest.getId(),
                    ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * 
     */
    @Override
    @LogExecutionTime
    public TierChangeRequesFetchAllResponseDTO getAllTierChangeRequest(TierChangeRequestSearchDTO searchTierChangeRequestDTO, Pageable pageable, boolean export) {
        tierChangeRequestBusinessValidator.validateDateParams(searchTierChangeRequestDTO);
        TierChangeRequesFetchAllResponseDTO response;
        try {
            
            /* Get Active Tier Config to be used for Projection fields */
            Page<PricingTierConfig> tierConfig = export
                    ? pricingConfigRepository.findActiveByType(ConstantUtils.CREDIT_CARD, DateUtils.getISODate(new Date(), ConstantUtils.UTC), PageRequest.of(0, 12))
                    : Page.empty();
            Map<String, String> tierConfigMap = tierConfig.getContent().stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(config -> config.getTierCode(),
                                    config -> StringUtils.join(config.getTierType(), " - ", config.getTierName())),
                            Collections::<String, String> unmodifiableMap));
            
            List<TierChangeRequestDTO> allTierChangeRequest = tierChangeRepositoryCustom.findAllTierChangeRequest(searchTierChangeRequestDTO, pageable, export, tierConfigMap);
            int tierChangeRequestCount = tierChangeRepositoryCustom.findTierChangeRequestCount(searchTierChangeRequestDTO);

            List<TierChangeRequestDTO> tierRequestList = Arrays
                    .asList(new ObjectMapper().convertValue(allTierChangeRequest, TierChangeRequestDTO[].class));
            response = TierChangeRequesFetchAllResponseDTO.builder().requestList(tierRequestList).totalNoOfRecords(tierChangeRequestCount)
                    .build();
            LOGGER.info("getAllTierChangeRequest:: Tier Change Requests fetched: {}, total requests found: {}", tierRequestList.size(),
            		tierChangeRequestCount);
        } catch (MongoException ex) {
            LOGGER.error("getAllTierChangeRequest:: Exception occurred while fetching Tier Change Requests");
            throw new BusinessException(ex);
        }
        return response;
    }

    /**
     * Update Tier Change Request Status with notes
     * @param tierChangeRequestId
     * @param updateTierChangeRequestDTO
     * @return TierChangeRequestDTO
     */
    @Override
    public TierChangeRequestDTO updateById(String tierChangeRequestId, TierChangeRequestUpdateDTO updateTierChangeRequestDTO) {
        TierChangeRequest tierChangeRequest = tierchangeRequestRepository.findById(tierChangeRequestId)
                .orElseThrow(() -> new BusinessException(getMessage(TIERCHANGE_REQUEST_NOT_FOUND)));
        TierChangeRequestDTO existingRequest = mapper.map(tierChangeRequest, TierChangeRequestDTO.class);
        tierChangeRequestBusinessValidator.validateUpdateRequest(existingRequest, updateTierChangeRequestDTO);
        existingRequest.setStatus(updateTierChangeRequestDTO.getStatus());
        existingRequest.setNotes(updateTierChangeRequestDTO.getNotes());
        /* Set Reviewer Details */
        existingRequest.setReviewedOn(DateUtils.today());
        existingRequest.setReviewedBy(securityUtils.getCurrentUser());
        TierChangeRequest updatedRequest = tierchangeRequestRepository.save(mapper.map(existingRequest, TierChangeRequest.class));
        LOGGER.info("updateById:: TierChange Request updated successfully for id: {} with status: {} ", updatedRequest.getId(),
                updatedRequest.getStatus().getStatus());
        /* Publish SQS Message for Realtime TierChangeRequest Execution only for Approved Status */
        if (Objects.equals(updatedRequest.getStatus(), TierChangeRequestStatus.APPROVED)) {
            String publishStatus = operationsUtil.publishTierChangeRequestMessage(tierChangeRequestId, ConstantUtils.OPERATIONS);
            if (StringUtils.equalsIgnoreCase(publishStatus, ConstantUtils.ERROR)) {
                LOGGER.error("updateById:: Tier Change Request job SQS Publish failed for tierChangeRequestId: {} ", tierChangeRequestId);
                throw new SystemException(getMessage(ErrorConstants.TIERCHANGE_REQUEST_SQS_PUBLISH_FAILED, tierChangeRequestId));
            }
        } else if (Objects.equals(updatedRequest.getStatus(), TierChangeRequestStatus.REJECTED)) {
            /* Send rejected mail to impacted reps */
            sendRejectedRequestMail(tierChangeRequest);
        }
        return mapper.map(updatedRequest, TierChangeRequestDTO.class);
    }

    private void sendRejectedRequestMail(TierChangeRequest tierChangeRequest) {
        try {
            List<Map<String, String>> impactedRepsData = vcRelationRepository.getTierRequestMessageDetails(tierChangeRequest.getEliminatedIdns(),
                    tierChangeRequest.getSupplier().getOid());
            List<RecipientDetailDTO> receipients = new ArrayList<>();
            impactedRepsData.forEach(rep -> {
                String emailId = rep.get(ConstantUtils.EMAIL_ID);
                String receiverName = StringUtils.join(rep.get(ConstantUtils.FIRST_NAME), StringUtils.SPACE, rep.get(ConstantUtils.LAST_NAME));
                rep.remove(ConstantUtils.EMAIL_ID);
                rep.remove(ConstantUtils.IDNS_LIST);
                RecipientDetailDTO recipient = RecipientDetailDTO.builder().messageVariable(rep).receiverId(emailId).receiverName(receiverName)
                        .build();
                receipients.add(recipient);
            });
            List<MessageDTO> messageRequest = tierRequestMessageServiceUtil.populateDowngradeRejectedRequest(receipients);

            List<MessageResponseDTO> messageResponse = messageServiceClient.sendMessage(messageRequest);
            if (CollectionUtils.isNotEmpty(messageResponse) && CollectionUtils.isNotEmpty(messageResponse.get(0).getReceivers())) {
                // Mail sent to impacted reps successfully
                LOGGER.info("Mail sent to Impacted Reps for Rejected Tier Downgrade Request : {}, group message Id : {}, number of reps : {}",
                        tierChangeRequest.getId(), messageResponse.get(0).getGroupMessageId(), messageResponse.get(0).getReceivers().size());
            } else {
                LOGGER.info("Error occurred while sending mail Impacted Reps for Rejected Tier Downgrade Request : {}", tierChangeRequest.getId());
            }
        } catch (Exception ex) {
            LOGGER.error("Send Mail to Impacted Reps for Rejected Tier Downgrade Request {} Failed Exception {} ::: ", tierChangeRequest.getId(),
                    ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Validates the Tier Change Request Details
     * @param id
     * @return responseDTO
     */
    @Override
    @LogExecutionTime
    public TierChangeRequestValidationResponseDTO validateTierChangeRequest(String id) {
        TierChangeRequestDetailsDTO tierDetails = getTierChangeRequestById(id);
        TierChangeRequestValidationResponseDTO response = new TierChangeRequestValidationResponseDTO();
        response.setId(tierDetails.getId());
        TierChangeRequestDTO details = new TierChangeRequestDTO();
        details.setSupplier(tierDetails.getSupplier());
        details.setCurrentTierCode(tierDetails.getCurrentTierCode());
        details.setStatus(tierDetails.getStatus());
        details.setRequestedTierCode(tierDetails.getRequestedTierCode());
        response.setDetails(details);
        /* Check supplier is INACT and validate */
        if (!StringUtils.equalsIgnoreCase(tierDetails.getSupplierStatus(), ConstantUtils.USER_STATUS_ACTIVE)) {
            populateValidateResponse(response, ConstantUtils.C01, ErrorConstants.SUPPLIER_INACT_ERROR, ConstantUtils.SUPPLIER_INACT);
            return response;
        }
        /* Check supplier is Prepaid and validate */
        List<String> prepaidTierCodes = lookupRepository.findCodeByCategory(ConstantUtils.PREPAID);
        if (prepaidTierCodes.contains(tierDetails.getSupplierCurrentTierCode())) {
            populateValidateResponse(response, ConstantUtils.C01, ErrorConstants.SUPPLIER_PREPAID_ERROR, ConstantUtils.PREPAID_CONTRACT);
            return response;
        }
        /* Check IDN slot available after eliminate idns */
        PricingTierConfig requestedTierDetails = pricingConfigRepository.findActiveByTierCode(tierDetails.getRequestedTierCode(),
                DateUtils.getISODate(new Date(), ConstantUtils.UTC));
        if (tierDetails.getIdnCountAfter() > requestedTierDetails.getAllowedMaxIdn()) {
            populateValidateResponse(response, ConstantUtils.C01, ErrorConstants.TIER_CHANGE_IDNS_ERROR, ConstantUtils.IDNS_EXCEED);
        } else {
            populateValidateResponse(response, ConstantUtils.C00, ConstantUtils.TIER_CHANGE_VALID, ConstantUtils.VALID_REQUEST);
        }
        return response;
    }

    private void populateValidateResponse(TierChangeRequestValidationResponseDTO response, String code, String message, String reason) {
        response.setCode(code);
        response.setSummary(StringUtils.equals(code, ConstantUtils.C00) ? ConstantUtils.DOWNGRADE_ALLOWED : ConstantUtils.DOWNGRADE_NOT_ALLOWED);
        response.setMessage(getMessage(message));
        response.setReason(reason);
    }

    /**
     * Get eliminated idns for tier change request
     * @param id
     * @param pageable
     * @return
     */
    @Override
    @LogExecutionTime
    public IdnsResponseDTO getEliminatedIdns(String id, Pageable pageable) {
        TierChangeRequest tierDetails = tierchangeRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(getMessage(TIERCHANGE_REQUEST_NOT_FOUND)));
        List<String> eliminatedIdns = tierDetails.getEliminatedIdns();
        if (CollectionUtils.isEmpty(eliminatedIdns)) {
            return IdnsResponseDTO.builder().idnsList(ListUtils.EMPTY_LIST).totalNoOfRecords(NumberUtils.INTEGER_ZERO).build();
        }
        List<ProviderDetailsDTO> idnsList = vendorDetailRepositoryCustom.getIdnsDetails(eliminatedIdns, pageable);
        return IdnsResponseDTO.builder().idnsList(idnsList).totalNoOfRecords(eliminatedIdns.size()).build();
    }
}
