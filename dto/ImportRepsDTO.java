package com.ghx.api.operations.dto;

import lombok.Data;

/**
 * 
 * @author Ajith
 *
 */
@Data
public class ImportRepsDTO {

    /** The mongoKey */
    private String mongoKey;

    /** The salesforceId */
    private String salesforceId;

    /** The emailId */
    private String emailId;

}
