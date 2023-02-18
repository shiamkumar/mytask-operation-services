package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Krishnan M
 *
 */

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;

    private String ticketNumber;
    
    private String fein;
    
    private String type;
    
    private String subType;
    
    private String status;

    private String organizationName;
    
    private String requestDetails;
    
    private String requestOrigin;
    
    private String processedBy;

    private Date processedOn;

    private String closureNotes;
    
    private String closureResult;
    
    private List<SimilarRequest> similarRequests;

    private AuditField created;

    private AuditField updated;
    
    @Transient
    private int similarRequestCount;

}
