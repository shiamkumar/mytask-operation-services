package com.ghx.api.operations.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class AWSHelper {

    private static AmazonSQS sqsClient;

    public static AmazonSQS getSQSClient() {
        if (Objects.isNull(sqsClient)) {
            synchronized (AWSHelper.class) {
                if (Objects.isNull(sqsClient)) {
                    sqsClient = AmazonSQSClientBuilder.defaultClient();
                }
            }
        }
        return sqsClient;
    }

    public static String publishToSqs(String queueUrl, String body, Map<String, String> attributes) {
        SendMessageRequest request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(body)
                .withMessageAttributes(getMessageAttributeValues(attributes));
        SendMessageResult result = getSQSClient().sendMessage(request);
        return result.getMessageId();
    }

    private static Map<String, MessageAttributeValue> getMessageAttributeValues(Map<String, String> input) {
        final Map<String, MessageAttributeValue> attributes = new HashMap<>();
        input.entrySet().forEach(entry -> {
            attributes.put(entry.getKey(), new MessageAttributeValue().withDataType(String.class.getSimpleName()).withStringValue(entry.getValue()));
        });
        return attributes;
    }
}
