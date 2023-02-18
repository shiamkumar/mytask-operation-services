package com.ghx.api.operations.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * The class ImportRepRequestDTO
 * @author Krishnan M
 *
 */
@Getter
@Setter
public class ImportRepRequestDTO {

    /** The Id */
    private String id;
    /** The parentoid */
    private String oid;
    /** The mongoKey */
    private String mongoKey;
    /** The salesforceId */
    private String salesforceId;
    /** The emailId */
    private String emailId;
    /** The fileName */
    private String fileName;
    /** The uploadedBy */
    private String uploadedBy;
    /** The uploadedOn */
    private Date uploadedOn;
    /** The requestType */
    private String requestType;
    /** The fein */
    private String fein;
    /** The status */
    private String status;

}
