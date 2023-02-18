package com.ghx.api.operations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ghx.api.operations.model.UserSecurity;

/**
 * 
 * @author Loganathan.M
 *
 */
@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, String> {

	UserSecurity findByUserIdAndUserStatusCode(String userId, String status);

}
