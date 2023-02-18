package com.ghx.api.operations.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * @author Rajasekar Jayakumar
 *
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PricingTierConfigDto implements Comparable {

    private String id;
    private String tierCode;
    private String tierName;
    private Integer tierSeq;
    private String tierType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD)
    private Date effectiveFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD)
    private Date expiredOn;

    private Double pricePerUser;
    private Integer allowedMaxIdn;
    private Integer maxUserCount;
    private Integer minUserCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD)
    private Date createdOn;
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.YYYY_MM_DD)
    private Date updatedOn;
    private String updatedBy;

    @Override
    public int compareTo(Object obj) {
        return this.getTierName().compareTo(((PricingTierConfigDto) obj).getTierName());
    }

}
