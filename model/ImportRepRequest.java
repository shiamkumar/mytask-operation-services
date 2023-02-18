package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.ghx.api.operations.dto.ImportRepDetailsDTO;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
@Document(collection = "import_rep_request")
public class ImportRepRequest {

    /** The id */
    private String id;

    /** The oid */
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

    /** The status */
    private String status = ConstantUtils.UPLOADED;

    /** The fein */
    private String fein;

    /** The customerOid */
    private String customerOid;

    /** The supplierName */
    private String supplierName;

    /** The providerName */
    private String providerName;

    /** The userDetails */
    private List<ImportRepDetailsDTO> userDetails;

}
