package com.ghx.api.operations.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghx.api.operations.config.InfraProperties;
import com.ghx.api.operations.events.OpsEvent;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.model.BaseAudit;
import com.ghx.api.operations.model.LambdaAudit;
import com.ghx.api.operations.model.PricingTierConfig;
import com.ghx.api.operations.repository.BaseAuditRepository;
import com.ghx.api.operations.repository.LambdaAuditRepository;
import com.ghx.api.operations.repository.LookupRepository;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.ngcommons.security.model.CustomAuthenticationToken;
import com.ghx.ngcommons.security.model.Principal;

/**
 * This class OperationsUtil
 *
 */
@Component
public class OperationsUtil {

    /** Logger instance */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(OperationsUtil.class);

    /** pricing component name */
    @Value("${scheduler.jobs.pricing.moveUser.componentName}")
    private String pricingComponentName;

    /** pricing job name */
    @Value("${scheduler.jobs.pricing.moveUser.jobName}")
    private String pricingJobName;

    /** pricing domain name */
    @Value("${scheduler.jobs.pricing.domainName}")
    private String pricingDomainName;

    /** importrep domain name */
    @Value("${scheduler.jobs.operations.importRep.componentName}")
    private String importRepComponentName;

    /** importrep domain name */
    @Value("${scheduler.jobs.operations.importRep.jobName}")
    private String importRepJobName;

    /** operation domain name */
    @Value("${scheduler.jobs.operations.domainName}")
    private String operationDomainName;

    /** Tier Change Request Processor Job name*/
    @Value("${scheduler.jobs.operations.tier-change-processor.jobName:tier-change-processor-job}")
    private String tierChangeProcessorJob;

    /** Tier Change Request Processor Component name*/
    @Value("${scheduler.jobs.operations.tier-change-processor.componentName:realtime-operations}")
    private String tierChangeProcessorComponenet;

    /** vendorOfficeContact Component name */
    @Value("${scheduler.jobs.operations.vendorOfficeContactSanction.componentName}")
    private String vendorOfficeContactComponentName;

    /** vendorOfficeContact job name */
    @Value("${scheduler.jobs.operations.vendorOfficeContactSanction.jobName}")
    private String vendorOfficeContactJobName;
    
    /** massUserDelete Component Name */
    @Value("${scheduler.jobs.operations.massUserDelete.componentName}")
    private String massUserDeleteComponentName;

    /** massUserDelete job name */
    @Value("${scheduler.jobs.operations.massUserDelete.jobName}")
    private String massUserDeleteJobName;

    /** Base Audit Repository Insance */
    @Autowired
    private BaseAuditRepository baseAuditRepository;

    /** Infra Properties */
    @Autowired
    private transient InfraProperties infraProperties;

    /** Lambda Audit Repository Instance */
    @Autowired
    private transient LambdaAuditRepository lambdaAuditRepository;
    
    /** Lookup Repository Instance */
    @Autowired
    private transient LookupRepository lookupRepository;

	public void saveAuditTrial(String id, PricingTierConfig pricingTierConfig) {
		BaseAudit baseAudit = createPricingBaseAudit(id, pricingTierConfig);
		baseAuditRepository.save(baseAudit);
	}

	private BaseAudit createPricingBaseAudit(String id, PricingTierConfig pricingTierConfig) {
		BaseAudit baseAudit = new BaseAudit();
		Map<String, Object> details = new HashMap<String, Object>();
		baseAudit.setAuditType(ConstantUtils.PRICING_TIER_CONFIG);
		details.put(ConstantUtils.PRICING_TIER_CONFIG_ID, id);
		details.put(ConstantUtils.TIER_CODE, pricingTierConfig.getTierCode());
		details.put(ConstantUtils.TIER_NAME, pricingTierConfig.getTierName());
		details.put(ConstantUtils.TIER_TYPE, pricingTierConfig.getTierType());
		details.put(ConstantUtils.TIER_SEQ, pricingTierConfig.getTierSeq());
		details.put(ConstantUtils.EFFECTIVE_FROM, pricingTierConfig.getEffectiveFrom());
		details.put(ConstantUtils.ALLOWED_MAX_IDN, pricingTierConfig.getAllowedMaxIdn());
		if (pricingTierConfig.getExpiredOn() != null) {
			details.put(ConstantUtils.EXPIRED_ON, pricingTierConfig.getExpiredOn());
		}
		if (pricingTierConfig.getTierType().equalsIgnoreCase(ConstantUtils.CREDIT_CARD)) {
			details.put(ConstantUtils.MAX_USER_COUNT, pricingTierConfig.getMaxUserCount());
			details.put(ConstantUtils.PRICE_PER_USER, pricingTierConfig.getPricePerUser());
		} else {
			details.put(ConstantUtils.MIN_USER_COUNT, pricingTierConfig.getMinUserCount());
		}
		baseAudit.setDetails(details);
		return baseAudit;
	}
	  
    /*
     * Retrieves the email for the logged in user
     * @return {String} logged In User Email Id
     */
    public String getCurrentUser() {
        Principal principal = (Principal) ((CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return StringUtils.defaultIfBlank(principal.getEmail(), principal.getUsername());
    }

    /**
     * Publish events to sqs
     *
     * @param opsEvent
     * @return the Lambda audit
     */
    @LogExecutionTime
    public LambdaAudit publishEventsToSQS(OpsEvent opsEvent) {
        String messageId = StringUtils.EMPTY;
        StringBuilder sqsUrl = new StringBuilder(infraProperties.getAwsSqsUri());
        if (StringUtils.equalsAnyIgnoreCase(opsEvent.getSqsType(), ConstantUtils.PREPAID_CONTRACT_UPDATION)) {
            sqsUrl.append(infraProperties.getPrepaidContractFunction());
        } else if (StringUtils.equalsAnyIgnoreCase(opsEvent.getSqsType(), ConstantUtils.ES_USER_SYNC)) {
            sqsUrl.append(infraProperties.getEsUserDetailSyncFunction());
        } else if (StringUtils.equalsAnyIgnoreCase(opsEvent.getSqsType(), ConstantUtils.ES_REP_SYNC)) {
            sqsUrl.append(infraProperties.getEsRepSyncFunction());
        } else if (StringUtils.equalsAnyIgnoreCase(opsEvent.getSqsType(), ConstantUtils.DELETE_RRP_DEF)) {
            sqsUrl.append(infraProperties.getDocAlertFunction());
        }
        Map<String, String> messageAttributes = new HashMap<>();
        messageAttributes.put(ConstantUtils.DOMAIN_TYPE, opsEvent.getDomainType());
        messageAttributes.put(ConstantUtils.PROCESS_TYPE, opsEvent.getProcessType());
        try {
            messageId = AWSHelper.publishToSqs(sqsUrl.toString(), opsEvent.getMessageBody(), messageAttributes);
        } catch (AmazonClientException e) {
            LOGGER.error("Exception occured while SQS publish {} {} exception - {}", opsEvent.getDomainType(), opsEvent.getProcessType(),
                    ExceptionUtils.getStackTrace(e));
            return lambdaAuditRepository.save(populateSQSLamdaAudit(messageId, opsEvent.getMessageBody(), opsEvent.getDomainType(),
                    opsEvent.getProcessType(), ExceptionUtils.getStackTrace(e)));
        }
        LOGGER.info("Successfully published {}  {} event to SQS for  oid:: {}, messageId:: {}  ", opsEvent.getDomainType(),
                opsEvent.getProcessType(), opsEvent.getMessageBody(), messageId);
        return lambdaAuditRepository.save(populateSQSLamdaAudit(messageId, opsEvent.getMessageBody(), opsEvent.getDomainType(),
                opsEvent.getProcessType(), StringUtils.EMPTY));
    }

    private static LambdaAudit populateSQSLamdaAudit(String messageId, String messageBody, String domainType, String processType,
            String exceptionString) {
        Date createdDate = Calendar.getInstance().getTime();
        LambdaAudit lambdaAudit = new LambdaAudit();
        if (StringUtils.isNotEmpty(exceptionString)) {
            lambdaAudit.setErrorDetails(exceptionString);
            lambdaAudit.setMessageId(StringUtils.EMPTY);
            lambdaAudit.setMessageStatus(ConstantUtils.ERROR);
        } else {
            lambdaAudit.setMessageId(messageId);
            lambdaAudit.setMessageStatus(ConstantUtils.PUBLISHED);
        }
        lambdaAudit.setJobName(domainType);
        lambdaAudit.setJobType(processType);
        lambdaAudit.setCreatedOn(createdDate);
        lambdaAudit.setUpdatedOn(createdDate);
        lambdaAudit.setMessageBody(messageBody);
        return lambdaAudit;
    }
    
    /**
     * Email validation
     * @param email
     * @return
     */
	public static boolean isValid(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
		return email.matches(regex);
	}

	/**
	 * get domain from email
	 * @param emailId
	 * @return
	 */
	public static String getDomainFromEmail(String emailId) {
		return emailId.substring(emailId.indexOf('@') + 1);
	}
	
    /**
     * sqs publish universal schedular
     * @param moveRequestID
     * @param sqsType
     */
    public String publishMoveUserMessage(String moveRequestId, String sqsType) {
        String messageId = StringUtils.EMPTY;
        StringBuilder sqsUrl = new StringBuilder(infraProperties.getAwsSqsUri());
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.OPERATIONS)) {
            sqsUrl.append(infraProperties.getUniversalSchedulerFunction());
        }
        Map<String, String> sqsData = new HashMap<>();
        sqsData.put(ConstantUtils.MOVEREQUEST_ID, moveRequestId);
        Map<String, String> messageAttributes = getUSMessageAttributes(sqsType);
        try {
            messageId = AWSHelper.publishToSqs(sqsUrl.toString(), new ObjectMapper().writeValueAsString(sqsData), messageAttributes);
            LOGGER.info("Successfully published move user Job moveRequestID {} messageId {} ", moveRequestId, messageId);
        } catch (JsonProcessingException | AmazonClientException e) {
            LOGGER.error("Exception occured while SQS publishing move user message {} ", ExceptionUtils.getStackTrace(e));
            return ConstantUtils.ERROR;
        }
        return messageId;
    }

    private Map<String, String> getUSMessageAttributes(String sqsType) {
        Map<String, String> messageAttributes = new HashMap<>();
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.OPERATIONS)) {
            messageAttributes.put(ConstantUtils.JOB_NAME, pricingJobName);
            messageAttributes.put(ConstantUtils.DOMAIN_NAME, pricingDomainName);
            messageAttributes.put(ConstantUtils.COMPONENT_NAME, pricingComponentName);
        }
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.IMPORT_REP)) {
            messageAttributes.put(ConstantUtils.JOB_NAME, importRepJobName);
            messageAttributes.put(ConstantUtils.DOMAIN_NAME, operationDomainName);
            messageAttributes.put(ConstantUtils.COMPONENT_NAME, importRepComponentName);
        }
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.SUPPLIER_OFFICER_CONTACT_SANCTION)) {
            messageAttributes.put(ConstantUtils.JOB_NAME, vendorOfficeContactJobName);
            messageAttributes.put(ConstantUtils.DOMAIN_NAME, operationDomainName);
            messageAttributes.put(ConstantUtils.COMPONENT_NAME, vendorOfficeContactComponentName);
        }
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.RETRY_MASS_USER_DELETE)) {
            messageAttributes.put(ConstantUtils.JOB_NAME, massUserDeleteJobName);
            messageAttributes.put(ConstantUtils.DOMAIN_NAME, operationDomainName);
            messageAttributes.put(ConstantUtils.COMPONENT_NAME, massUserDeleteComponentName);
        }
        return messageAttributes;
    }
	
    /**
     * get Tier Full Name
     * CREDMGR-72108 Implement tier name changes
     * @param tierCode
     * @return
     */
    public String getTierFullName(String tierCode) {
        return lookupRepository.findCategoryAndDescriptionByCode(tierCode);
    }
    
    /**
     * Method to publish importrep request id to jobs SQS
     * @param messageBody
     * @param sqsType
     */
    public void publishToUniversalScheduler(String messageBody, String sqsType) {
        String messageId = StringUtils.EMPTY;
        StringBuilder sqsUrl = new StringBuilder(infraProperties.getAwsSqsUri());
        Map<String, String> sqsData = new HashMap<>();
        sqsUrl.append(infraProperties.getUniversalSchedulerFunction());
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.IMPORT_REP)) {
            sqsData.put(ConstantUtils.IMPORT_REP_REQUEST_ID, messageBody);
        }
        if(StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.SUPPLIER_OFFICER_CONTACT_SANCTION)) {
            sqsData.put(ConstantUtils.FEIN, messageBody);
        }
        if(StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.RETRY_MASS_USER_DELETE)) {
            sqsData.put(ConstantUtils.USER_DELETE_REQUEST_ID, messageBody);
        }
        Map<String, String> messageAttributes = getUSMessageAttributes(sqsType);
        try {
            messageId = AWSHelper.publishToSqs(sqsUrl.toString(), new ObjectMapper().writeValueAsString(sqsData), messageAttributes);
            LOGGER.info("Successfully published {} messageBody {} messageId {} ",sqsType, messageBody, messageId);
        } catch (JsonProcessingException | AmazonClientException e) {
            LOGGER.error("Exception occured while SQS publishing {} message {} ", sqsType,ExceptionUtils.getStackTrace(e));
        }
    }
    
    /**
     * remove unnecessary values for ES query
     * @param searchValue
     * @return
     */
    public static String normalizeSearchString(String searchValue) {
        String replacedValue = searchValue;
        replacedValue = replacedValue.replaceAll("'(?=[\\d])|(?<=[\\d])'", StringUtils.SPACE).trim();
        replacedValue = replacedValue.replaceAll("[^a-zA-Z0-9._' ]", StringUtils.SPACE).trim();
        return replacedValue;
    }
    
    /**
     * Sqs publish Universal Scheduler for Tier Change Request
     * @param tierChangeRequestId
     * @param sqsType
     */
    public String publishTierChangeRequestMessage(String tierChangeRequestId, String sqsType) {
        String messageId = StringUtils.EMPTY;
        StringBuilder sqsUrl = new StringBuilder(infraProperties.getAwsSqsUri());
        if (StringUtils.equalsAnyIgnoreCase(sqsType, ConstantUtils.OPERATIONS)) {
            sqsUrl.append(infraProperties.getUniversalSchedulerFunction());
        }
        Map<String, String> sqsData = new HashMap<>();
        sqsData.put(ConstantUtils.TIER_CHANGE_REQUEST_ID, tierChangeRequestId);
        Map<String, String> messageAttributes = new HashMap<>();
        messageAttributes.put(ConstantUtils.JOB_NAME, tierChangeProcessorJob);
        messageAttributes.put(ConstantUtils.DOMAIN_NAME, operationDomainName);
        messageAttributes.put(ConstantUtils.COMPONENT_NAME, tierChangeProcessorComponenet);
        try {
            messageId = AWSHelper.publishToSqs(sqsUrl.toString(), new ObjectMapper().writeValueAsString(sqsData), messageAttributes);
            LOGGER.info("Successfully published TierChangeRequest Processor Message tierChangeRequestId {} messageId {} ", tierChangeRequestId, messageId);
        } catch (JsonProcessingException | AmazonClientException e) {
            LOGGER.error("Exception occured while SQS TierChangeRequest Processor Message {} ", ExceptionUtils.getStackTrace(e));
            return ConstantUtils.ERROR;
        }
        return messageId;
    }
}
