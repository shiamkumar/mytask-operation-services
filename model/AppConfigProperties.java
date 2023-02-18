package com.ghx.api.operations.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Subamathi
 *
 */
@Document(collection = "app_config_prop")
@Getter
@Setter
public class AppConfigProperties implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5307383244155988459L;

    /** The id. */
    private String id;

    /** The propertyName. */
    private String propertyName;

    /** The propertyValue. */
    private String propertyValue;

    /** The isEnabled. */
    private boolean isEnabled;

}
