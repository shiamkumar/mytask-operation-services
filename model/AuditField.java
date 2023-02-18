package com.ghx.api.operations.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Audit fields For collection
 * @author Mari Muthu Muthukrishnan
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class AuditField {

    /** Audit By */
    public String by;

    /** Audit on */
    public Date on;
}
