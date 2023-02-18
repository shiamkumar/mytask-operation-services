package com.ghx.api.operations.feign.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghx.api.operations.dto.SubscriptionRequestDTO;
import com.ghx.api.operations.dto.SubscriptionResponse;
import com.ghx.api.operations.dto.UserDetailDTO;
import com.ghx.api.operations.dto.ValidateRequestDTO;

/**
 * This class ProfileServiceClient
 * @author ananth
 *
 */
@FeignClient(name = "profileApi", url = "${credentialingApi.baseUrl}")
public interface ProfileServiceClient {

	/**
	 * compare vendors
	 * @param sourceVendorOid
	 * @param destinationVendorOid
	 * @return
	 */
	@PostMapping("/vendors/compare")
	Map<String, Object> compareVendors(@RequestBody ValidateRequestDTO validateRequestDTO);

    /**
     * move user prevalidate
     * @param id
     * @param sourceSupplierOid
     * @param destinationSupplierOid
     * @param retainIdns
     * @param userId
     * @return
     */
    @PostMapping("/users/{id}/move/validate")
    Map<String, Object> validateMove(@PathVariable String id, @RequestBody ValidateRequestDTO validateRequestDTO);

    /**
     * validate for email already existing or not
     * @param validations
     * @param email
     * @param fein
     * @return
     */
    @GetMapping("/users/validate")
    UserDetailDTO checkUserExist(@RequestParam String validations, @RequestParam(required = false) String emailId,
            @RequestParam(required = false) String verificationCode, @RequestParam(required = false) String vendorOid);

    /**
     * Get paid reps for a supplier(by fein)
     * @param userStatus
     * @param userType
     * @param fein
     * @param pageable
     * @return
     */
    @GetMapping("/users")
    Map<String, Object> searchUsers(@RequestParam String userStatus, @RequestParam String userType, @RequestParam String fein,
            @RequestParam Pageable pageable);

    /**
     * Get paid users accounts for a supplier(by oid)
     * @param id
     * @param pageable
     * @return
     */
    @GetMapping("/vendors/{id}/accounts/{paidStatus}")
    Map<String, Object> getUsersAccounts(@PathVariable String id, Pageable pageable, @PathVariable String paidStatus);

    /**
     * 
     * @param subscriptionRequestDTO
     * @return
     */
    @PostMapping("/subscriptions")
    void createSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO);
    
    /**
     * 
     * @param subscriptionRequestDTO
     * @return
     */
    @GetMapping("/subscriptions/plans")
    SubscriptionResponse searchSubscriptions(@RequestParam String parentOid);
    
    /**
     * 
     * @param subscriptionRequestDTO
     * @return
     */
    @PutMapping("/subscriptions")
    void updateSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO);
}
