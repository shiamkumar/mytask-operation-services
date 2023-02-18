package com.ghx.api.operations.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author Rajasekar Jayakumar
 *
 */

@Data
@SuperBuilder
@NoArgsConstructor
@Document(collection = "pricing_tier_config")
public class PricingTierConfig {

    @Id
    private String id;

    @NotEmpty(message = "{pricing.tiercode.empty}")
    private String tierCode;

    @NotEmpty(message = "{pricing.tiername.empty}")
    private String tierName;

    private Integer tierSeq;

    @NotEmpty(message = "{pricing.tiertype.empty}")
    private String tierType;

    private Date effectiveFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD)
    private Date expiredOn;

    private Double pricePerUser;

    private Integer allowedMaxIdn;

    private Integer maxUserCount;

    private Integer minUserCount;

    @CreatedDate
    private Date createdOn;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Date updatedOn;

    @LastModifiedBy
    private String updatedBy;

}
