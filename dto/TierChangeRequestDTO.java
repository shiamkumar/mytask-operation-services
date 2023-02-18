package com.ghx.api.operations.dto;
 
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.enums.TierChangeRequestStatus;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/18/2021
 * @category DTO
 *
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierChangeRequestDTO extends TierChangeRequestBaseDTO {
    
    /** Tier Change Request Id */
    private String id;

    /** Supplier's Current Tier Code */
    private String currentTierCode;

    /** Tier Change Request Status */
    @Enumerated(EnumType.STRING)
    private TierChangeRequestStatus status;

    /** Request Created On Date Time */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date createdOn;

    /** Request Raised / Created By */
    private String createdBy;
    
    /** Request Last updated On Date Time */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date updatedOn;

    /** Request Last Updated By */
    private String updatedBy;

    /** Request Reviewed Date Time */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date reviewedOn;

    /** Request Reviewed By */
    private String reviewedBy;

    /** Request Processed Date */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD, timezone = ConstantUtils.EST)
    private Date processedOn;
    
    /** Error Message in case the Request Failed to Execute / Complete */
    private String errorMessage;
    
    /**processed  by */
    private String processedBy;
}
