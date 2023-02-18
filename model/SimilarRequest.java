package com.ghx.api.operations.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarRequest {

    private String requestedBy;
    
    private Date requestedOn;
    
    private boolean initialRequest;

}
