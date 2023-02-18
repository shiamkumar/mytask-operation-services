package com.ghx.api.operations.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Lal
 *
 */
@Getter
@Setter
public class MigrationAuditDTO {
	/** Id */
    private String eventId;

    /** status */
    private String status;

    /** message */
    private String message;

    /** Start time */
    private Date startTime;
    
    /** End time */
    private Date endTime;
}
