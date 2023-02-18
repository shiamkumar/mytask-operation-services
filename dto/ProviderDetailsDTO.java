package com.ghx.api.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class ProviderDetailsDTO.
 * @author Sundari V
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderDetailsDTO {

    /** The oid. */
    private String oid;

    /** The company name. */
    private String companyName;
}
