package com.ghx.api.operations.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 06/21/2022
 */
@Data
@Builder
public class AuditExportDTO {

    /** Export Data in Bytes */
    private byte[] exportData;
    
    /** Export Content Type */
    private String contentType;
    
    /** Export Content Disposition */
    private String contentDisposition;
    
}
