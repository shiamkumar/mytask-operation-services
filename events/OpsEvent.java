package com.ghx.api.operations.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value object for Operation Events to encapsulate the messaging data
 * 
 * @author Kanakaraj S
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpsEvent {
    
    private String processType;
    private String domainType;
    private String sqsType;
    private String messageBody;
}
