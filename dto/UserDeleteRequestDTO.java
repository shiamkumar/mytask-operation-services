package com.ghx.api.operations.dto;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghx.api.operations.enums.UserDeleteRequestStatus;
import com.ghx.api.operations.util.ConstantUtils;

import lombok.Data;

/**
 * Class UserDeleteRequestDTO
 * 
 * @author ananth.k
 *
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDeleteRequestDTO {
	/**id */
	@Id
	private String id;

	/**mongoKey */
	private String mongoKey;

	/** status*/
	private String status;

	/** salesForceId*/
	private String salesForceId;

	/**uploadedBy */
	private String uploadedBy;

	/**uploadedOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY_HH_MM_SS_A, timezone = ConstantUtils.EST)
	private Date uploadedOn;

	/** totalCount*/
	private long totalCount;

	/**successCount */
	private long successCount;

	/** failedCount*/
	private long failedCount;

	/**deletedBy */
	private String deletedBy;

	/**deletedOn */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstantUtils.MM_DD_YYYY_HH_MM_SS, timezone = ConstantUtils.EST)
	private Date deletedOn;

}
