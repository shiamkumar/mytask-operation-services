package com.ghx.api.operations.repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ajith
 *
 */
public interface VCRelationRepositoryCustom {
    
    /**
     * 
     * @param idnOid
     * @return Map<String, String>
     */
    Map<String, String> validateOneActiveRep(String idnOid);

    /**
     * Get impacted reps message details
     * @param customerOids
     * @param vendorOid
     */
    List<Map<String, String>> getTierRequestMessageDetails(List<String> customerOids, String vendorOid);

    /**
     * get idn count for the vendorOid based on paid status
     * @param vendorOid
     * @param isPaid
     * @return
     */
    int getIDNCount(String vendorOid, boolean isPaid);
}
