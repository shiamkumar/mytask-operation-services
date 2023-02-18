package com.ghx.api.operations.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * Class ValidateRequestDTO:  this class using for create request body for move and merge preview api.
 * @author ananth.k
 *
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateRequestDTO {

	/** source vendor oid */
	private String sourceVendorOid;

	/** destination vendor oid */
	private String destinationVendorOid;

	/** retainIdns */
	private List<String> retainIdns;

	/** userId */
	private String userId;

}
