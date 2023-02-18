package com.ghx.api.operations.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.messagecenter.client.dto.message.MessageDTO;
import com.ghx.messagecenter.client.dto.message.RecipientDetailDTO;

/**
 * Util class for Tier Downgrade Mail Requests
 * @author Sundari
 *
 */
@Component
public class TierChangeRequestMessageServiceUtil {

    /** Logger Instance */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TierChangeRequestMessageServiceUtil.class);

    /** Message Email Sender Name default */
    @Value("${message.email.senderName}")
    private String emailSenderName;

    /** Default Sender Id for Email */
    @Value("${message.email.senderId}")
    private String emailSenderId;

    /** Default Product Name for Email */
    @Value("${message.email.product}")
    private String emailProductName;

    /** Pending Request - Tier Downgrade Module Name */
    @Value("${message.template.tierDowngrade.pendingRequest.module}")
    private String pendingRequestModule;

    /** Pending request - Tier Downgrade Template Name */
    @Value("${message.template.tierDowngrade.pendingRequest.name}")
    private String pendingRequestTemplate;

    /** Rejected Request - Tier Downgrade Module Name */
    @Value("${message.template.tierDowngrade.rejectedRequest.module}")
    private String rejectedRequestModule;

    /** Rejected request - Tier Downgrade Template Name */
    @Value("${message.template.tierDowngrade.rejectedRequest.name}")
    private String rejectedRequestTemplate;

    /**
     * Populate pending tier downgrade mail request
     * @param recipientDetail
     *            list
     * @return
     */
    public List<MessageDTO> populateDowngradePendingRequest(List<RecipientDetailDTO> recipientDetail) {
        MessageDTO messageRequest = MessageDTO.builder().product(emailProductName).module(pendingRequestModule).templateName(pendingRequestTemplate)
                .receivers(recipientDetail).messageType(ConstantUtils.EMAIL.toUpperCase(Locale.getDefault())).senderId(emailSenderId)
                .senderName(emailSenderName).build();
        LOGGER.debug("Message Request details for Downgrade Pending Request ::: {} ", messageRequest);
        return Arrays.asList(messageRequest);
    }

    /**
     * Populate rejected tier downgrade mail request
     * @param recipientDetail
     *            list
     * @return
     */
    public List<MessageDTO> populateDowngradeRejectedRequest(List<RecipientDetailDTO> recipientDetail) {
        MessageDTO messageRequest = MessageDTO.builder().product(emailProductName).module(rejectedRequestModule).templateName(rejectedRequestTemplate)
                .receivers(recipientDetail).messageType(ConstantUtils.EMAIL.toUpperCase(Locale.getDefault())).senderId(emailSenderId)
                .senderName(emailSenderName).build();
        LOGGER.debug("Message Request details for Downgrade Rejected Request ::: {} ", messageRequest);
        return Arrays.asList(messageRequest);
    }
}
