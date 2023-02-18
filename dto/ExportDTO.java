package com.ghx.api.operations.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author jeyanthilal.g
 *
 */
@Data
@Builder
public class ExportDTO {
	/** Export Data in Bytes */
    private byte[] exportData;
    
    /** Export Content Type */
    private String contentType;
    
    /** Export Content Disposition */
    private String contentDisposition;
}
