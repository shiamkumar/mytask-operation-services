package com.ghx.api.operations.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.dto.ContractStatus;
import com.ghx.api.operations.dto.PrepaidContractDTO;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.dto.PricingTierConfigDto;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SubscriptionRequestDTO;
import com.ghx.api.operations.dto.SubscriptionResponse;
import com.ghx.api.operations.enums.PricingTierCode;
import com.ghx.api.operations.events.OpsEvent;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.feign.client.DocumentServiceClient;
import com.ghx.api.operations.feign.client.ProfileServiceClient;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.AppConfigProperties;
import com.ghx.api.operations.model.PrepaidContract;
import com.ghx.api.operations.model.PrepaidContractAudit;
import com.ghx.api.operations.model.PrepaidContractSuppliers;
import com.ghx.api.operations.repository.AppConfigPropertiesRepository;
import com.ghx.api.operations.repository.PrepaidContractAuditRepository;
import com.ghx.api.operations.repository.PrepaidContractRepository;
import com.ghx.api.operations.repository.PrepaidContractRepositoryCustom;
import com.ghx.api.operations.repository.PrepaidContractSuppliersRepository;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.DateUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.util.ReportUtils;
import com.ghx.api.operations.validation.business.PrepaidContractBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.common.search.engine.SearchEngineClient;
import com.ghx.common.search.model.SearchResult;
import com.ghx.common.search.query.builder.ElasticQuery;
import com.ghx.common.search.query.builder.ElasticQuery.Builder;
import com.ghx.common.search.query.builder.ElasticQuery.SortBuilder;

import ma.glasnost.orika.MapperFacade;

/**
 * The Class PrepaidContractServiceImpl.
 */
@Component
public class PrepaidContractServiceImpl implements PrepaidContractService {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(PrepaidContractServiceImpl.class);

    private static final String LEGAL_NAME = "legal_name";
    private static final String EIN_OR_SSN = "ein_or_ssn";

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private transient PrepaidContractAuditRepository prepaidContractAuditRepository;

    /** The prepaid contract repository. */
    @Autowired
    public transient PrepaidContractRepository prepaidContractRepository;

    /** The Prepaid contract business validator. */
    @Autowired
    private transient PrepaidContractBusinessValidator prepaidContractBusinessValidator;

    @Autowired
    private transient OperationsUtil operationsUtil;
    
    /**
     * initailze search engine client
     */
    @Autowired
    private transient SearchEngineClient searchEngineClient;
    
    /**intialize prepaid contract repository custom */
    @Autowired
    private transient PrepaidContractRepositoryCustom prepaidContractRepostoryCustom;
    
    /** VendorDetail Repo Instance */
    @Autowired
    private transient VendorDetailRepositoryCustom vendorDetailRepositoryCustom;
    
    /** PrepaidContractSupplier Repository Instance */
    @Autowired
    private transient PrepaidContractSuppliersRepository prepaidContractSuppliersRepository;
    
    /** The profileServiceClient */
    @Autowired
    private transient DocumentServiceClient documentServiceClient;

    /** The PricingConfigService */
    @Autowired
    private transient PricingConfigService pricingConfigService;

    /** intialize ProfileServiceClient */
    @Autowired
    private transient ProfileServiceClient profileServiceClient;
    
    /** AppConfigProperties Repository Instance */
    @Autowired
    private transient AppConfigPropertiesRepository appConfigPropertiesRepository;

    /**
     * This method will fetch all Prepaid Contract details available with prepaid
     * plan.
     *
     * @param prepaidDetails
     *            the prepaid details DTO
     * @param pageable
     *            the pageable
     * @return the map
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> fetchPrepaidGridContracts(PrepaidContractDTO prepaidDetails, Pageable pageable, String requestType) {

        long start = System.currentTimeMillis();
        LOGGER.info("fetchPrepaidGridContracts start {}", start);

        // start and end date validation
        prepaidContractBusinessValidator.validateDateParams(prepaidDetails);

        Map<String, Object> prepaidContractsDetails = new HashMap<>();

        try {
            List<PrepaidContractDTO> allPrepaidContracts = prepaidContractRepository.findAllPrepaidContracts(prepaidDetails, pageable, requestType);
            String prepaidContractsCount = prepaidContractRepository.findPrepaidContractCount(prepaidDetails);
            		
            prepaidContractsDetails.put(ConstantUtils.PREPAID_CONTRACTS_LIST, allPrepaidContracts);
            prepaidContractsDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, prepaidContractsCount);
            LOGGER.info("fetchPrepaidGridContracts end {}", System.currentTimeMillis() - start);

        } catch (HibernateException ex) {
            LOGGER.error("fetchPrepaidGridContracts:: Exception occurred while fetching PrepaidContracts", ex);
            throw new BusinessException(ex);
        }
        return prepaidContractsDetails;

    }

    /**
     * Fetch contract details by id
     * @param id
     * @return the dto
     */
    @LogExecutionTime
    @Override
    public PrepaidContractDTO getById(String oid) {

        PrepaidContractDTO prepaidContractDTO = prepaidContractRepository.findDetailsByOid(oid);
        if (Objects.isNull(prepaidContractDTO)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.PREPAIDCONTRACT_NOT_FOUND, oid));
        }
        AppConfigProperties appConfig = appConfigPropertiesRepository.findByPropertyName(ConstantUtils.SERVICE_BUNDLING_FEATURE_FLAG);
        if(StringUtils.equalsIgnoreCase(ConstantUtils.TRUE_STR, appConfig.getPropertyValue())) {
            try {
                SubscriptionResponse subscription = profileServiceClient.searchSubscriptions(prepaidContractDTO.getOid());
                prepaidContractDTO.setPackageOid(subscription.getPlanOid());
            } catch (Exception exception) {
                LOGGER.error("Error in fetching package subscription for PP contract" + exception.getMessage());
            }
        }
        List<String> feins = Arrays.asList(prepaidContractDTO.getFeinsList().split(","));
        Pageable pageable = PageRequest.of(ConstantUtils.ZERO_INDEX, feins.size(), Sort.by(Sort.Direction.ASC, ConstantUtils.LEGALNAME));
        List<PrepaidContractSupplierDTO> supplierDetails = prepaidContractRepository.fetchSupplierDetails(feins, oid, pageable);
        List<Map<String, Object>> mapList = prepaidContractRepostoryCustom.findPrepadiOidByFein(feins);
        supplierDetails.forEach(newlist -> {
            mapList.forEach(map -> {
                if (StringUtils.equalsIgnoreCase((String) map.get(ConstantUtils.FEIN), newlist.getFein())) {
                    newlist.setContractExists(true);
                }
            });
        });
        prepaidContractDTO.setSuppliers(supplierDetails);
        return prepaidContractDTO;
    }

    @Override
    @LogExecutionTime
    public PrepaidContractDTO save(PrepaidContractDTO prepaidContractDTO) {
        prepaidContractBusinessValidator.isEmpty(prepaidContractDTO);
        prepaidContractBusinessValidator.validateFeinsForContract(prepaidContractDTO, false);
        setIDNAndUserCount(prepaidContractDTO, Arrays.asList(prepaidContractDTO.getFeinsList().split(",")));
        prepaidContractBusinessValidator.validatePrepaidContract(prepaidContractDTO);
        prepaidContractDTO.setStatus(ContractStatus.ACTIVE.getStatus());
        PrepaidContract prepaidContract = prepaidContractRepository.save(mapper.map(prepaidContractDTO, PrepaidContract.class));
        prepaidContractDTO.setOid(prepaidContract.getOid());
        saveAudit(prepaidContractDTO, prepaidContract.isDeleted());
        savePrepaidContractSuppliers(prepaidContractDTO);
        
        AppConfigProperties appConfig = appConfigPropertiesRepository.findByPropertyName(ConstantUtils.SERVICE_BUNDLING_FEATURE_FLAG);
        if(StringUtils.equalsIgnoreCase(ConstantUtils.TRUE_STR, appConfig.getPropertyValue())) {
            try {
                profileServiceClient.createSubscription(populateSubscription(prepaidContractDTO));
            } catch (Exception exception) {
                LOGGER.error("Error occured while processing package subscription");
            }
        }
        /* Mark the users as paid for all the prepaid suppliers */
        List<String> feins = Arrays.asList(prepaidContractDTO.getFeinsList().split(","));
        if (CollectionUtils.isNotEmpty(feins)) {
            int count = prepaidContractRepository.markUsersPaid(feins, operationsUtil.getCurrentUser(), prepaidContractDTO.getPricingTierCode());
            LOGGER.info("Users marked as Paid count : {}, contractOid: {}", count, prepaidContract.getOid());
            /* Publish User update SQS for all feins */
            publishUserESSynctoSQS(feins);
            /* Publish SQS to update Rep Index by feins (update paiduser flag in rep index) */
            publishRepESSynctoSQS(feins);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(prepaidContractDTO.getFeinsList());
        }
        /* update prpaid tier plan */
        int vendorUpdateCount = vendorDetailRepositoryCustom.updateVendorPricingTier(feins, operationsUtil.getCurrentUser(), prepaidContractDTO.getPricingTierCode());
        LOGGER.info("Vendor tier updated : {}, contractOid: {}", vendorUpdateCount, prepaidContract.getOid());
        LOGGER.info("save:: completed Successfully for contractOid: {}", prepaidContract.getOid());
        return prepaidContractDTO;

    }

    /**
     * 
     * @param prepaidContractDTO
     * @return
     */
    private SubscriptionRequestDTO populateSubscription(PrepaidContractDTO prepaidContractDTO) {
        SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
        subscriptionRequestDTO.setDateOfExpiry(prepaidContractDTO.getContractEndDate());
        subscriptionRequestDTO.setDateOfPurchase(prepaidContractDTO.getContractStartDate());
        subscriptionRequestDTO.setParentOid(prepaidContractDTO.getOid());
        subscriptionRequestDTO.setPlanOid(prepaidContractDTO.getPackageOid());
        subscriptionRequestDTO.setSubscriptionPlan(ConstantUtils.PACKAGE);
        return subscriptionRequestDTO;
    }

    private void publishRepESSynctoSQS(List<String> feins) {
        List<String> vendorOids = vendorDetailRepositoryCustom.getVendorByFein(feins);
        // Publish Rep Sync only for Registered Feins
        if (CollectionUtils.isNotEmpty(vendorOids)) {
            OpsEvent event = new OpsEvent();
            event.setDomainType(ConstantUtils.VENDOR);
            event.setProcessType(ConstantUtils.MERGE_VENDOR);
            event.setSqsType(ConstantUtils.ES_REP_SYNC);
            event.setMessageBody(vendorOids.stream().collect(Collectors.joining("','")));
            operationsUtil.publishEventsToSQS(event);
            LOGGER.info("publishRepESSynctoSQS:: Event Sent successfully on save PP contract fein : {}", event.getMessageBody());
        }
    }

    /**
     * 
     * @param prepaidContractDTO
     * @return 
     */
    private List<PrepaidContractSuppliers> savePrepaidContractSuppliers(PrepaidContractDTO prepaidContractDTO) {
        List<PrepaidContractSuppliers> contractSuppliers = new ArrayList<>();
        prepaidContractDTO.getSuppliers().forEach(supplier -> {
            contractSuppliers.add(PrepaidContractSuppliers.builder().prepaidContractOid(prepaidContractDTO.getOid()).fein(supplier.getFein())
                    .supplierName(supplier.getLegalName()).build());
        });
        List<PrepaidContractSuppliers> savedPrepaidSuppliers = prepaidContractSuppliersRepository.saveAll(contractSuppliers);        
        LOGGER.info("savePrepaidContractSuppliers:: Prepaid Contract Suppliers saved successfully for contractOid: {} suppliersAdded: {}",
                prepaidContractDTO.getOid(), contractSuppliers.size());
        return savedPrepaidSuppliers;
    }

    private PrepaidContractDTO saveAudit(PrepaidContractDTO contractDTO, boolean deleted) {
        if (Objects.nonNull(contractDTO)) {
            LOGGER.info("saveAudit oid: {}", contractDTO.getOid());
            List<String> feins = new ArrayList<>();
            List<PrepaidContractSupplierDTO> prepaidSupplierList = contractDTO.getSuppliers().stream().filter(contract -> {
                if (StringUtils.isBlank(contract.getLegalName())) {
                    feins.add(contract.getFein());
                }
                return StringUtils.isNotBlank(contract.getLegalName());
            }).collect(Collectors.toList());
            prepaidSupplierList.addAll(vendorDetailRepositoryCustom.getVendorDetailsByFein(feins));
            contractDTO.setSuppliers(prepaidSupplierList);
            PrepaidContractAudit prepaidContractAudit = mapper.map(contractDTO, PrepaidContractAudit.class);
            prepaidContractAudit.setDeleted(deleted);
            prepaidContractAudit.setPrepaidContractOid(contractDTO.getOid());
            prepaidContractAuditRepository.save(prepaidContractAudit);
        }
        return contractDTO;
    }

    /**
     * Update contract by oid
     * @param id
     * @param the dto
     */
    @LogExecutionTime
    @Override
    public PrepaidContractDTO update(String oid, PrepaidContractDTO prepaidContractDTO) {
        PrepaidContractDTO prepaidContract = getById(oid);
        prepaidContractBusinessValidator.validateForExpiredContract(prepaidContract, prepaidContractDTO);
        List<String> existingFeins = prepaidContract.getSuppliers().stream().map(PrepaidContractSupplierDTO::getFein).distinct().collect(Collectors.toList());
        List<String> feinsToUpdate = prepaidContractDTO.getSuppliers().stream().map(PrepaidContractSupplierDTO::getFein).distinct().collect(Collectors.toList());
        List<String> deleteFeins = ListUtils.subtract(existingFeins, feinsToUpdate);
        List<String> newFeins = ListUtils.subtract(feinsToUpdate, existingFeins);
        List<PrepaidContractSupplierDTO> newSuppliers = prepaidContractDTO.getSuppliers().stream().filter(supplier -> newFeins.contains(supplier.getFein())).collect(Collectors.toList());
        prepaidContractDTO.setSuppliers(newSuppliers);
        prepaidContractBusinessValidator.validateFeinsForContract(prepaidContractDTO, true);
        setIDNAndUserCount(prepaidContractDTO, feinsToUpdate);
        prepaidContractBusinessValidator.validatePrepaidContract(prepaidContractDTO);

        prepaidContract.setOid(oid);

        if (Objects.nonNull(prepaidContractDTO.getContractStartDate())) {
            prepaidContract.setContractStartDate(new Timestamp(prepaidContractDTO.getContractStartDate().getTime()));
        }
        if (Objects.nonNull(prepaidContractDTO.getContractEndDate())) {
            prepaidContract.setContractEndDate(new Timestamp(prepaidContractDTO.getContractEndDate().getTime()));
        }
        if (Objects.nonNull(prepaidContractDTO.getMaxIDNCount())) {
            prepaidContract.setMaxIDNCount(prepaidContractDTO.getMaxIDNCount());
        }
        if (Objects.nonNull(prepaidContractDTO.getMaxUserCount())) {
            prepaidContract.setMaxUserCount(prepaidContractDTO.getMaxUserCount());
        }
        if (Objects.nonNull(prepaidContractDTO.getNotes())) {
            prepaidContract.setNotes(prepaidContractDTO.getNotes());
        }
        if (Objects.nonNull(prepaidContractDTO.getPricingTierCode())) {
            prepaidContract.setPricingTierCode(prepaidContractDTO.getPricingTierCode());
        }
        
        if(prepaidContract.getOasContract()) {
        	prepaidContract.setOasContract(false);
        }
        prepaidContract.setCurrentIDNCount(prepaidContractDTO.getCurrentIDNCount());
        prepaidContract.setCurrentUserCount(prepaidContractDTO.getCurrentUserCount());
        prepaidContract.setUpdatedBy(operationsUtil.getCurrentUser());
        prepaidContract.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        prepaidContract.setUnlimitedReps(prepaidContractDTO.isUnlimitedReps());
        if (!DateUtils.todaysDate(DateUtils.formattedDate(prepaidContractDTO.getContractEndDate()))
                .before(DateUtils.todaysDate(DateUtils.todayStr()))) {
            prepaidContract.setStatus(ContractStatus.ACTIVE.getStatus());
        }
        prepaidContractRepository.save(mapper.map(prepaidContract, PrepaidContract.class));
        savePrepaidContractSuppliers(prepaidContractDTO);
        /* If any supplier removed from contract, update the users as Unpaid */
        if (CollectionUtils.isNotEmpty(deleteFeins)) {
            prepaidContractSuppliersRepository.deleteByFeins(deleteFeins, prepaidContract.getOid());
            int usersMarkedUnpaid = prepaidContractRepository.markUsersUnpaid(deleteFeins, operationsUtil.getCurrentUser());
            LOGGER.info("Users marked as Unpaid count : {}, contractOid: {}", usersMarkedUnpaid, prepaidContractDTO.getOid());
            publishUserESSynctoSQS(deleteFeins);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(deleteFeins.stream().collect(Collectors.joining(",")));
            /* Publish SQS to update Rep Index by feins (update paiduser flag in rep index) */
            publishRepESSynctoSQS(deleteFeins);
            publishSQSToRemoveRRPDef(deleteFeins);
            LOGGER.info("updatePrepaidContractSuppliers:: Prepaid Contract Suppliers removed successfully for contractOid: {} suppliersRemoved: {}, feins: {}",
                    prepaidContract.getOid(), deleteFeins.size(), deleteFeins);
            prepaidContractDTO.setUsersMarkedUnpaid(usersMarkedUnpaid);

            /* Convert deleted suppliers tier to Credit card */
            convertSupplierToCreditCard(deleteFeins, prepaidContract);
        }
        saveAudit(prepaidContractDTO, prepaidContract.getDeleted());

        /* Mark the users as paid for all the existing/new suppliers in the contract */
        if (CollectionUtils.isNotEmpty(feinsToUpdate)) {
            int count = prepaidContractRepository.markUsersPaid(feinsToUpdate, operationsUtil.getCurrentUser(), prepaidContractDTO.getPricingTierCode());
            LOGGER.info("Users marked as Paid count : {}, contractOid: {}", count, prepaidContract.getOid());
            /* Publish User update SQS for all feins */
            publishUserESSynctoSQS(feinsToUpdate);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(feinsToUpdate.stream().collect(Collectors.joining(",")));
            /* Publish SQS to update Rep Index by feins (update paiduser flag in rep index) */
            publishRepESSynctoSQS(feinsToUpdate);
            prepaidContractDTO.setUsersMarkedPaid(count);
        }

        /* update prepaid tier plan */
        int vendorUpdateCount = vendorDetailRepositoryCustom.updateVendorPricingTier(feinsToUpdate, prepaidContract.getUpdatedBy(), prepaidContractDTO.getPricingTierCode());
        
        /* update contract subscription plan */
        updateContractWithServicePlan(prepaidContractDTO);
       
        LOGGER.info("Vendor tier updated : {}, contractOid: {}", vendorUpdateCount, prepaidContract.getOid());
        LOGGER.info("Update:: completed Successfully for contractOid: {}", prepaidContract.getOid());
        return prepaidContractDTO;
    }

    private void updateContractWithServicePlan(PrepaidContractDTO prepaidContractDTO) {
        AppConfigProperties appConfig = appConfigPropertiesRepository.findByPropertyName(ConstantUtils.SERVICE_BUNDLING_FEATURE_FLAG);
        if(StringUtils.equalsIgnoreCase(ConstantUtils.TRUE_STR, appConfig.getPropertyValue()) 
                && ObjectUtils.anyNotNull(prepaidContractDTO.getPackageOid(),prepaidContractDTO.getContractStartDate(),prepaidContractDTO.getContractEndDate())) {
            try {
                profileServiceClient.updateSubscription(populateSubscriptionForUpdate(prepaidContractDTO));
            } catch (Exception e) {
                LOGGER.error("Error occured while update package subscription");
            }
        }
    }

    private SubscriptionRequestDTO populateSubscriptionForUpdate(PrepaidContractDTO prepaidContractDTO) {
        SubscriptionRequestDTO subscriptionRequestDTO = new SubscriptionRequestDTO();
        if(Objects.nonNull(prepaidContractDTO.getContractEndDate())) { 
            subscriptionRequestDTO.setDateOfExpiry(prepaidContractDTO.getContractEndDate());
        }
        if(Objects.nonNull(prepaidContractDTO.getContractStartDate())) {
        subscriptionRequestDTO.setDateOfPurchase(prepaidContractDTO.getContractStartDate());
        }
        if(Objects.nonNull(prepaidContractDTO.getPackageOid())) {
            subscriptionRequestDTO.setPlanOid(prepaidContractDTO.getPackageOid());
        }
        subscriptionRequestDTO.setParentOid(prepaidContractDTO.getOid());
        return subscriptionRequestDTO;
    }

    /**
     * Delete contract by oid
     * @param id
     * @return the dto
     */
    @LogExecutionTime
    @Override
    public PrepaidContractDTO delete(String oid) {
        PrepaidContractDTO prepaidContractDTO = getById(oid);
        if (Objects.isNull(prepaidContractDTO)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.PREPAIDCONTRACT_NOT_FOUND));
        }
        List<String> feins = Arrays.asList(prepaidContractDTO.getFeinsList().split(","));
        int updatedContract = prepaidContractRepository.updatePrepaidContract(oid, true, false, operationsUtil.getCurrentUser(),
                new Timestamp(System.currentTimeMillis()));
        int usersMarkedUnpaid = 0;
        /* Mark users unpaid for all prepaid feins */
        LOGGER.info("Prepaid contract : {} to be deleted, feins associated : {} ", prepaidContractDTO.getOid(), feins);
        if (CollectionUtils.isNotEmpty(feins)) {
            usersMarkedUnpaid = prepaidContractRepository.markUsersUnpaid(feins, operationsUtil.getCurrentUser());
            LOGGER.info("Users marked as Unpaid count : {}, contractOid: {}", usersMarkedUnpaid, prepaidContractDTO.getOid());
            publishSQSToRemoveRRPDef(feins);
            publishUserESSynctoSQS(feins);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(feins.stream().collect(Collectors.joining(",")));
            /* Publish SQS to update Rep Index by feins (update paiduser flag in rep index) */
            publishRepESSynctoSQS(feins);
            /* Convert deleted suppliers tier to Credit card */
            convertSupplierToCreditCard(feins, prepaidContractDTO);
        }
        prepaidContractDTO.setUpdatedContract(updatedContract);
        prepaidContractDTO.setUsersMarkedUnpaid(usersMarkedUnpaid);
        if (updatedContract > 0) {
            prepaidContractDTO.setDeleted(true);
            saveAudit(prepaidContractDTO, true);
        }
        LOGGER.info("Prepaid contract : {} deleted, users {} marked as unpaid", prepaidContractDTO.getOid(), usersMarkedUnpaid);
        return prepaidContractDTO;
    }

    /**
     * Search Vendor by Fein
     * @param fein
     * @return the dto
     */
    @LogExecutionTime
    @Override
    public PrepaidContractDTO getByFein(String fein) {
        LOGGER.info("getByFein start fein {}:", fein);
        PrepaidContractDTO prepaidContractDTO = prepaidContractRepository.findDetailsByFein(fein);
        if (Objects.nonNull(prepaidContractDTO)) {
            return prepaidContractDTO;
        }
        List<Map<String, Object>> vendorList = prepaidContractRepository.searchVendorByFein(fein);
        if (CollectionUtils.isNotEmpty(vendorList)) {
            Map<String, Object> vendorMap = vendorList.get(0);
            prepaidContractDTO = new PrepaidContractDTO();
            prepaidContractDTO.setFein((String) vendorMap.get(EIN_OR_SSN));
            prepaidContractDTO.setSupplierName((String) vendorMap.get(LEGAL_NAME));
        } else {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.PREPAIDCONTRACT_FEIN_DOESNOT_EXISTS));
        }
        LOGGER.info("getByFein end {}: ", fein);
        return prepaidContractDTO;
    }

    /**
     * Get Prepaid Contract Audits By Id
     * @param oid
     * @param pageable
     * @return
     */
    @Override
    public Page<PrepaidContractDTO> getAuditsByPrepaidContractId(String oid, Pageable pageable) {
        Pageable pageableReq = pageable;
        if (pageable.getSort().isUnsorted()) {
            pageableReq = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(ConstantUtils.CREATED_ON).descending());
        }
        Page<PrepaidContractAudit> contractAudits = prepaidContractAuditRepository.findByPrepaidContractOid(oid, pageableReq);
        if (CollectionUtils.isEmpty(contractAudits.getContent())) {
            LOGGER.error("getAuditsByPrepaidContractId:: Prepaid contract Audits not found for Contract id: {}", oid);
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.PREPAIDCONTRACT_AUDIT_NOT_FOUND));
        }
        List<PrepaidContractDTO> prepaidContractConfigDTOList = new ArrayList<>();
        for (PrepaidContractAudit prepaidContractAudit : contractAudits) {
            PrepaidContractDTO prepaidContractDTO = mapper.map(prepaidContractAudit, PrepaidContractDTO.class);
            prepaidContractDTO.setOid(prepaidContractAudit.getPrepaidContractOid());
            prepaidContractDTO.setUpdatedBy(prepaidContractAudit.getCreatedBy());
            prepaidContractDTO
                    .setAuditUpdatedOn(DateUtils.convertDateToStringFormat(prepaidContractAudit.getCreatedOn(), ConstantUtils.YYYY_MM_DD_KK_MM_A));
            prepaidContractConfigDTOList.add(prepaidContractDTO);
        }
        LOGGER.info("getAuditsByPrepaidContractId:: completed for prepaidContractId: {} , numberOfAudits: {}", oid, prepaidContractConfigDTOList.size());
        return new PageImpl<>(prepaidContractConfigDTOList, pageableReq, contractAudits.getTotalElements());
    }

    /**
     * 
     * @param prepaidContractDTO
     * @param feins
     */
    private void setIDNAndUserCount(PrepaidContractDTO prepaidContractDTO, List<String> feins) {
        Map<String, Object> userIdnCountDetails = vendorDetailRepositoryCustom.getUserAndIdnCountByFein(feins);
        prepaidContractDTO.setCurrentIDNCount(MapUtils.getIntValue(userIdnCountDetails, "totalIdnCount"));
        prepaidContractDTO.setCurrentUserCount(MapUtils.getIntValue(userIdnCountDetails, "totalUserCount"));
        LOGGER.info("setCurrentIDNAndUserCount:: completed for feins: {} ", feins);
    }

    /**
     * Convert the deleted suppliers/ expire contract supplier to CC
     */
    private void convertSupplierToCreditCard(List<String> feins, PrepaidContractDTO prepaidContractDTO) {
        /* If Prepaid contract is in National plan, directly place CCN as supplier plan */
        if (StringUtils.equalsAnyIgnoreCase(prepaidContractDTO.getPricingTierCode(), PricingTierCode.PREPAID_NATIONAL.getCode())) {
            int updatedCount = vendorDetailRepositoryCustom.updateVendorPricingTier(feins, operationsUtil.getCurrentUser(),
                    PricingTierCode.CREDIT_CARD_NATIONAL.getCode());
            LOGGER.info("convertSupplierToCreditCard:: Suppliers to be removed from contract: {}, Updated Suppliers to Credit card : {} plan : {}", feins,
                    PricingTierCode.CREDIT_CARD_NATIONAL.getCode(), updatedCount);
        } else {
            /* For PPL,PPS,PPR plans, need to validate IDN count for placing CC Tier code */
            /* Filter out new suppliers with NA as IDN count and suppliers to be convert to CC */
            Map<String, Integer> suppliersToConvert = prepaidContractDTO.getSuppliers().stream()
                    .filter(supplier -> !StringUtils.equalsIgnoreCase(supplier.getIdnCount(), "NA") && feins.contains(supplier.getFein()))
                    .collect(Collectors.toMap(PrepaidContractSupplierDTO::getFein, supplier -> Integer.valueOf(supplier.getIdnCount())));
            List<PricingTierConfigDto> activePricingTiers = pricingConfigService.getActiveByType(ConstantUtils.CREDIT_CARD);

            /* Check IDN slot for each supplier and update the tier */
            suppliersToConvert.entrySet().forEach(supplier -> {
                AtomicBoolean eligiblePlan = new AtomicBoolean(false);
                activePricingTiers.forEach(tier -> {
                    if (BooleanUtils.isFalse(eligiblePlan.get()) && supplier.getValue() <= tier.getAllowedMaxIdn()) {
                        eligiblePlan.set(true);
                        vendorDetailRepositoryCustom.updateVendorPricingTier(Arrays.asList(supplier.getKey()), operationsUtil.getCurrentUser(),
                                tier.getTierCode());
                        LOGGER.info("convertSupplierToCreditCard:: Converted supplier: {}, to Credit card : {} plan", supplier.getKey(),
                                tier.getTierCode());
                    }
                });
                /* If no Slot available as per IDN count then set the supplier tier as CCN */
                if (BooleanUtils.isFalse(eligiblePlan.get())) {
                    vendorDetailRepositoryCustom.updateVendorPricingTier(Arrays.asList(supplier.getKey()), operationsUtil.getCurrentUser(),
                            PricingTierCode.CREDIT_CARD_NATIONAL.getCode());
                    LOGGER.info("convertSupplierToCreditCard:: Converted supplier: {}, to Credit card : {} plan", supplier.getKey(),
                            PricingTierCode.CREDIT_CARD_NATIONAL.getCode());
                }
            });
        }
    }

    /**
     * Fetch suppliers
     */
    @LogExecutionTime
    @Override
    public Map<String, Object> searchSuppliers(SearchRequest searchRequest) {
        ElasticQuery query = new ElasticQuery().query(populateESQuery(searchRequest)).rawOption(ConstantUtils.TRACK_TOTAL_HITS, true)
                .from((int) (Objects.nonNull(searchRequest.getPageable().getOffset()) ? searchRequest.getPageable().getOffset() : 0))
                .size(Objects.nonNull(searchRequest.getPageable().getPageSize()) ? searchRequest.getPageable().getPageSize() : 20)
                .sort(populateSortingBuilder(searchRequest)).build();
        SearchResult supplierList = searchEngineClient.search(query, ConstantUtils.VS_VENDOR_DETAIL);
        return enhanceSupplierList(supplierList);
    }

    private SortBuilder populateSortingBuilder(SearchRequest searchRequest) {
        boolean defaulSorting = true;
        SortBuilder sortBuilder = new SortBuilder();
        for (Order order : searchRequest.getPageable().getSort()) {
            defaulSorting = false;
            String sortField = order.getProperty();
            String sortType = order.getDirection().name();
            switch (sortField) {
                case ConstantUtils.FEIN:
                    sortBuilder.sort(StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.FEIN,
                            ConstantUtils.DOT_OPERATOR, ConstantUtils.FEIN, ConstantUtils.UNDERSCORE_UNTOUCHED), sortType);
                    break;
                case ConstantUtils.LEGALNAME:
                    sortBuilder.sort(StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME,
                            ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME, ConstantUtils.UNDERSCORE_UNTOUCHED), sortType);
                    break;
                default:
                    sortBuilder.sort(StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME,
                            ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME, ConstantUtils.UNDERSCORE_UNTOUCHED), sortType);
                    break;
            }
        }
        if (defaulSorting) {
            sortBuilder.sort(StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME, ConstantUtils.DOT_OPERATOR,
                    ConstantUtils.LEGALNAME, ConstantUtils.UNDERSCORE_UNTOUCHED), ConstantUtils.ASC);
        }
        return sortBuilder;
    }
    
    private Map<String, Object> enhanceSupplierList(SearchResult supplierList) {
        if (CollectionUtils.isEmpty(supplierList.getResponseDocuments())) {
            LOGGER.error("supplier list is empty");
        }
        List<PrepaidContractSupplierDTO> enhancedVendorsList = supplierList.getResponseDocuments().stream()
                .map(doc -> new ObjectMapper().convertValue(doc.get(ConstantUtils.PAYLOAD), PrepaidContractSupplierDTO.class)).collect(Collectors.toList());
        List<String> feinList = new ArrayList<>();
        enhancedVendorsList.forEach(list -> {
            feinList.add(list.getFein());
        });
        List<Map<String, Object>> mapList = prepaidContractRepostoryCustom.findPrepadiOidByFein(feinList);
        enhancedVendorsList.forEach(newlist -> {
            mapList.forEach(map -> {
                if (StringUtils.equalsIgnoreCase((String) map.get(ConstantUtils.FEIN), newlist.getFein())) {
                    newlist.setContractExists(true);
                }
            });
        });
        Map<String, Object> supplierDetailsMap = new HashMap<>();
        supplierDetailsMap.put(ConstantUtils.TOTAL_NO_OF_RECORDS, supplierList.getResponseMetaData().get("TotalRecords"));
        supplierDetailsMap.put(ConstantUtils.SUPPLIER_LIST, enhancedVendorsList);
        return supplierDetailsMap;
    }
    
    private Builder populateESQuery(SearchRequest searchRequest) {
        Builder builder = new Builder().and(ConstantUtils.MATCH,
                StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.ENABLE_PROGRAM_CHANGE), ConstantUtils.TRUE)
                .and(ConstantUtils.MATCH, StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.VENDORSTATUS),
                        ConstantUtils.USER_STATUS_ACTIVE);
        if (StringUtils.isNotEmpty(searchRequest.getFein())) {
            builder.and(ConstantUtils.MATCH, StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.FEIN),
                    searchRequest.getFein());
        }
        if (StringUtils.isNotEmpty(searchRequest.getLegalName())) {
            builder.querystring(StringUtils.join(ConstantUtils.STAR_SYMBOL,
                    operationsUtil.normalizeSearchString(searchRequest.getLegalName().toLowerCase(Locale.ENGLISH)), ConstantUtils.STAR_SYMBOL),
                    StringUtils.join(ConstantUtils.PAYLOAD, ConstantUtils.DOT_OPERATOR, ConstantUtils.LEGALNAME), ConstantUtils.AND);
        }
        return builder;
    }
    
    /**
     * Fetch all suppliers associated with a prepaid contract
     * @param id
     * @param pageable
     * @return the response entity
     */
    @LogExecutionTime
    @Override
    public Map<String, Object> getSupplierDetails(String id, Pageable pageable) {
        if(BooleanUtils.isFalse(prepaidContractRepository.existsById(id))) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.PREPAIDCONTRACT_NOT_FOUND, id));
        }
        Map<String, Object> supplierDetails = new HashMap<>();

        List<String> feins = prepaidContractRepository.getPrepaidFeins(id);

        List<PrepaidContractSupplierDTO> prepaidFeins = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(feins)) {
            prepaidFeins = prepaidContractRepository.fetchSupplierDetails(feins, id, pageable);
        }
        supplierDetails.put(ConstantUtils.SUPPLIERS_LIST, prepaidFeins);
        supplierDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, feins.size());
        return supplierDetails;
    }
    
    /**
     * Updates users as unpaid for expired contract
     * @param id(prepaidContractOid)
     * @return updated count
     */
    @LogExecutionTime
    @Override
    public PrepaidContractDTO updateExpiredContract(String prepaidContractOid) {
        LOGGER.info("updateExpiredContract start  {} prepaidContractOid {}:", System.currentTimeMillis(), prepaidContractOid);
        PrepaidContractDTO prepaidContract = getById(prepaidContractOid);
        prepaidContractBusinessValidator.validatePrepaidContract(prepaidContract, prepaidContractOid);

        List<String> feins = prepaidContract.getSuppliers().stream().map(PrepaidContractSupplierDTO::getFein).distinct().collect(Collectors.toList());
        PrepaidContractDTO details = new PrepaidContractDTO();
        details.setFein(feins.toString());
        details.setOid(prepaidContractOid);
        
        /* Mark all users under the fein as unpaid.. */
        int usersMarkedUnpaid  = 0; 
        if (CollectionUtils.isNotEmpty(feins)) {
             usersMarkedUnpaid  = prepaidContractRepository.markUsersUnpaid(feins, operationsUtil.getCurrentUser());
            LOGGER.info("Users marked as Unpaid count : {}, contractOid: {}", usersMarkedUnpaid, prepaidContractOid);
            publishSQSToRemoveRRPDef(feins);
            publishUserESSynctoSQS(feins);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(feins.stream().collect(Collectors.joining(",")));
            /* Publish SQS to update Rep Index by feins */
            publishRepESSynctoSQS(feins);
            /* Convert suppliers of expired contract to Credit card */
            convertSupplierToCreditCard(feins, prepaidContract);
        }
        details.setUsersMarkedUnpaid(usersMarkedUnpaid);

        /* Update the Status of contract as EXPIRED */
        PrepaidContract contract = prepaidContractRepository.findByOidAndDeleted(prepaidContractOid, false);
        contract.setStatus(ContractStatus.EXPIRED.getStatus());
        contract.setOasContract(false);
        prepaidContractRepository.save(contract);
        return details;
    }

    /**
     * Remove the rrp_def for the users
     * @param feins
     */
    private void removeRRPDef(List<String> feins) {
        List<String> userOids = vendorDetailRepositoryCustom.activeUsersForPrepaid(feins);
        int size = 10;
        AtomicInteger counter = new AtomicInteger();
        if (CollectionUtils.isNotEmpty(userOids)) {
            final Collection<List<String>> partitionedList = userOids.stream()
                    .collect(Collectors.groupingBy(userOid -> counter.getAndIncrement() / size)).values();
            for (List<String> list : partitionedList) {
                String userOidStr = String.join(",", list);
                documentServiceClient.removeRrpDef(userOidStr, ConstantUtils.USER);
            }
        }
    }

    private void publishUserESSynctoSQS(List<String> feins) {
        int size = 5;
        AtomicInteger counter = new AtomicInteger();
        final Collection<List<String>> partitionedList = feins.stream().collect(Collectors.groupingBy(fein -> counter.getAndIncrement() / size))
                .values();
        OpsEvent event = new OpsEvent();
        event.setDomainType(ConstantUtils.TYPE_SUPPLIER);
        event.setProcessType(ConstantUtils.UPDATE);
        event.setSqsType(ConstantUtils.ES_USER_SYNC);
        for (List<String> subList : partitionedList) {
            event.setMessageBody(subList.stream().collect(Collectors.joining("','")));
            operationsUtil.publishEventsToSQS(event);
        }
    }

    /**
     * Saves the given Suppliers in a Prepaid contract
     * @param suppliers
     * @param id the prepaid contract id
     */
    @Override
    public PrepaidContractDTO addPrepaidContractSuppliers(List<PrepaidContractSupplierDTO> suppliers, String id) {
        PrepaidContractDTO prepaidContract = getById(id);
        PrepaidContractDTO requestDTO = PrepaidContractDTO.builder().oid(id).suppliers(suppliers).build();
        prepaidContractBusinessValidator.validateFiensForUpdate(suppliers, prepaidContract);
        prepaidContractBusinessValidator.validateFeinsForContract(requestDTO, true);
        List<PrepaidContractSuppliers> contractSuppliers = savePrepaidContractSuppliers(requestDTO);
        LOGGER.info("addPrepaidContractSuppliers:: PrepaidContract Suppliers saved successfully for contractId: {} , supliersCount: {}", id,
                suppliers.size());
        List<PrepaidContractSupplierDTO> existingSuppliers = prepaidContract.getSuppliers();
        existingSuppliers.addAll(contractSuppliers.stream()
                .map(supplier -> PrepaidContractSupplierDTO.builder().fein(supplier.getFein()).legalName(supplier.getSupplierName()).build())
                .collect(Collectors.toList()));
        prepaidContract.setSuppliers(existingSuppliers);
        saveAudit(prepaidContract, false);
        return prepaidContract;
    }

    /**
     * Delete Suppliers in Prepaid Contract
     * @param suppliers
     * @param id
     */
    @Override
    public PrepaidContractDTO deletePrepaidContractSuppliers(List<PrepaidContractSupplierDTO> suppliers, String id) {
        PrepaidContractDTO prepaidContract = getById(id);
        List<String> deleteFeins = suppliers.stream().map(PrepaidContractSupplierDTO::getFein).collect(Collectors.toList());
        prepaidContractBusinessValidator.validateFiensForDelete(deleteFeins, prepaidContract);
        if (deleteFeins.size() == prepaidContract.getSuppliers().size()) {
            return delete(id);
        } else {
            int deletedCount = prepaidContractSuppliersRepository.deleteByFeins(deleteFeins, id);
            int usersMarkedUnpaid = prepaidContractRepository.markUsersUnpaid(deleteFeins, operationsUtil.getCurrentUser());
            LOGGER.info("deletePrepaidContractSuppliers:: Users marked as Unpaid count : {}, contractOid: {}, deletedFeins: {}", usersMarkedUnpaid, id, deletedCount);
            publishUserESSynctoSQS(deleteFeins);
            /* CREDMGR-85049 Publish universal scheduler to sync real-time vendor and principal queue */
            publishToVendorAndPrincipalIndex(deleteFeins.stream().collect(Collectors.joining(",")));
            /* Publish SQS to update Rep Index by feins */
            publishRepESSynctoSQS(deleteFeins);
            List<PrepaidContractSupplierDTO> updatedSuppliers = prepaidContract.getSuppliers().stream().filter(supplier -> !deleteFeins.contains(supplier.getFein()))
                    .collect(Collectors.toList());
            prepaidContract.setSuppliers(updatedSuppliers);
            saveAudit(prepaidContract, false);
        }
        return prepaidContract;
    }

    /**
     * export Prepaid Contracts Service Implementation
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void exportPrepaidContracts(PrepaidContractDTO prepaidDetails, ResourceLoader resourceLoader, String filename, Pageable pageable,
            String exportType, HttpServletResponse response) throws IOException {
        List<PrepaidContractDTO> prepaidContractDTOList = (List<PrepaidContractDTO>) fetchPrepaidGridContracts(prepaidDetails, pageable,
                ConstantUtils.EXPORT).get(ConstantUtils.PREPAID_CONTRACTS_LIST);
        prepaidContractDTOList.forEach(prepaidContractDTO -> prepaidContractDTO
                .setSupplierName(prepaidContractDTO.getSupplierName().replaceAll(ConstantUtils.RELATIONSHIP_LIST_SPLITTER, ConstantUtils.COMMA)));
        List transactionList = Arrays.asList(new ObjectMapper().convertValue(prepaidContractDTOList, PrepaidContractDTO[].class));
        ReportUtils.generateReport(exportType, transactionList, response, resourceLoader, ConstantUtils.FILENAME, null,ConstantUtils.PREPAIDCONTRACTS_EXPORT);
    }

    /**
     * CREDMGR-85049
     * publish to vendorAndPrincipalIndex job to update paidUser count in index
     * @param feins
     */
    private void publishToVendorAndPrincipalIndex(String feins) {
        operationsUtil.publishToUniversalScheduler(feins, ConstantUtils.SUPPLIER_OFFICER_CONTACT_SANCTION);
    }
    
    /**
     * publish remove rrp def
     * @param feins
     */
	private void publishSQSToRemoveRRPDef(List<String> feins) {
		List<String> vendorOids = vendorDetailRepositoryCustom.getVendorByFein(feins);
		if (CollectionUtils.isNotEmpty(vendorOids)) {
			OpsEvent event = new OpsEvent();
			event.setDomainType(ConstantUtils.USER);
			event.setProcessType(ConstantUtils.DELETE_RRP_DEF);
			event.setSqsType(ConstantUtils.DELETE_RRP_DEF);
			event.setMessageBody(vendorOids.stream().collect(Collectors.joining(",")));
			operationsUtil.publishEventsToSQS(event);
			LOGGER.info("Event Sent successfully on remove rrp def for feins : {}", event.getMessageBody());
		}

	}
}
