package com.ghx.api.operations.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class DeleteUserAudit.
 * @author Ajith
 *
 */
@Getter
@Setter
@Document(collection = "delete_user_audit")
public class DeleteUserAudit {

    /** The id. */
    private String id;

    /** The user id. */
    private String userId;

    /** The is recovery. */
    private boolean isRecovery;

    /** The deleted on. */
    private Date deletedOn;

}
