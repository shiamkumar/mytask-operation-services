package com.ghx.api.operations.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ghx.api.operations.dto.UserDetailsInfo;

/**
 * 
 * @author Ajith
 *
 */
public interface UserVMRepositoryCustom {

    /**
     * get Active Users
     * @param importRepEmailIds
     * @return List<String>
     */
    List<String> getActiveUsers(Set<String> importRepEmailIds);

    /**
     * get Inactive Users
     * @param importRepEmailIds
     * @return List<String>
     */
    List<String> getInactiveUsers(Set<String> importRepEmailIds);

    /**
     * Get Tier Change Requestor Name
     * @param emailId
     * @return String
     */
    String getTierChangeRequestorName(String emailId);
    
    /**
     * 
     * @param userIds
     * @return
     */
    List<Map<String, Object>> fetchUserDetails(Set<String> userIds);

}
