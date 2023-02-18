package com.ghx.api.operations.repository;

import com.ghx.api.operations.dto.BlobDTO;
/**
 * 
 * @author Ajith
 *
 */
public interface DocumentRepositoryCustom {

    /**
     * 
     * @param id
     * @param directory
     * @return
     */
    BlobDTO getBlob(String id, String directory);

}
