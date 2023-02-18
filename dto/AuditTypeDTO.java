package com.ghx.api.operations.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 * DTO for Audit Types
 */
@Data
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class AuditTypeDTO {

    /** The Audit Type */
    private String type;

    /** The Audit Name */
    private String name;
}
