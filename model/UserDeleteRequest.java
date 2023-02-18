package com.ghx.api.operations.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.dto.UserDetailsInfo;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * The class UserDeleteRequest
 * @author jeyanthilal.g
 */
@Data
@Document(collection = "user_delete_request")
public class UserDeleteRequest {
	/** The _id */
	private String id;
	
	/** The mongoKey */
    private String mongoKey;

    /** The salesforceId */
    private String salesForceId;

    /** The uploadedBy */
    private String uploadedBy;
    
    /** The uploadedOn */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date uploadedOn;
    
    /** The updatedOn */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date updatedOn;
    
    /** updated By */
    private String updatedBy;
    
    /** The uploadedBy */
    private String deletedBy;
    
    /** The deleted on */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD,timezone = ConstantUtils.EST)
    private Date deletedOn;
    
    /** The status */
    private String status  = ConstantUtils.UPLOADED;
    
    /** valid user Total uploaded count */
    private Integer totalCount;
    
    /** user deletion success count */
    private Integer successCount;
    
    /** user successfully failure count */
    private Integer failedCount;
    
    /** list of userDetails */
    private List<UserDetailsInfo> userDetails;
    
    /** The deleted request reason */
    private String deletedReason;

}
