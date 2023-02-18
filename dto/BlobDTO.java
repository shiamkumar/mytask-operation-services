package com.ghx.api.operations.dto;

import java.util.Date;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
public class BlobDTO {

    /** The id */
    private String id;

    /** The fileName */
    private String fileName;

    /** The createdOn */
    private Date createdOn;

    /** The size */
    private long size;

    /** The mimeType */
    private String mimeType;

    /** The data */
    private byte[] data;

}
