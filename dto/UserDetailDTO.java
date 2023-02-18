package com.ghx.api.operations.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * This class UserDetailDTO
 * 
 * @author Ananth Kandasamy
 *
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailDTO {

	/** The temp user exists. */
	private boolean tempUserExists;

	/** The tpm user exists. */
	private boolean tpmUserExists;

	/** The cm user exists. */
	private boolean cmUserExists;

	/** The deleted user exists. */
	private boolean deletedUserExists;

	/** The external verification user exists. */
	private boolean externalVerificationUserExists;

	/** The fein status. */
	private String feinStatus;

	/** The cm supplier exists. */
	private boolean cmSupplierExists;

	/** The tpm supplier exists. */
	private boolean tpmSupplierExists;

	/** The external verification supplier exists. */
	private boolean externalVerificationSupplierExists;

	/** verification link expired */
	private boolean verificationLinkExpired;

	/** The verification code. */
	private String verificationCode;

	/** The customer oid. */
	private String customerOid;

	/** The customer names for InviteRep . */
	private String customerNames;

	/** The reg type. */
	private String regType;

	/** The source. */
	private String source;

	/** The Eui Match key . */
	private String euiMatchKey;

	/** The is Data Change History Need */
	private boolean isDataChangeHistoryNeed;

	/** Express registred */
	private String expressRegistered;

	/** vendor oid */
	private String vendorOid;

	/** paid amount */
	private Double paidAmount;

	/** disocunt amount */
	private Double discountAmount;

	/** coupon code */
	private String couponCode;

	/** paid tier plan code */
	private String paidTierPlanCode;

	/** prepadi supplier */
	private boolean prepaidSupplier;

	/** The valid domain */
	private boolean validDomain;

	/** renewal date */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate renewalDate;

	/** paid user */
	private boolean paidUser;

	/** actory type */
	private String actorType;

	/** vendor name */
	private String vendorName;

	/** associate WithVendor */
	private boolean assoWithVendor;

	/** active user */
	private boolean inActiveUser;

	/** actor description */
	private String actorDescription;

	/** Invite rep */
	private boolean inviteRep;

	/** send notification */
	private boolean sendNotification;

	/** account on boarding */
	private boolean accountOnBoarding;

}
