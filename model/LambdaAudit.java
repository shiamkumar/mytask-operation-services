package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "lambda_audit")
public class LambdaAudit {
    @Id
    private String id;

    private String messageId;

    private String messageBody;

    private String messageStatus;

    private String jobName;

    private String jobType;

    @LastModifiedDate
    private Date updatedOn;

    @CreatedDate
    private Date createdOn;

    private String errorDetails;

    private String lambdaId;

    private List<RecordStatus> recordStatus;
}
