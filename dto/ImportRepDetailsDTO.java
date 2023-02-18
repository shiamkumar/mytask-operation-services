package com.ghx.api.operations.dto;

import java.sql.Timestamp;

import lombok.Data;

/**
 *
 * @author Ajith
 *
 */
@Data
public class ImportRepDetailsDTO {

    /** The rowNum */
    private int rowNum;

    /** The status */
    private String status;

    /** The firstName */
    private String firstName;

    /** The lastName */
    private String lastName;

    /** The email */
    private String email;

    /** The title */
    private String title;

    /** The middleInitial */
    private String middleInitial;

    /** The workPhone */
    private String workPhone;

    /** The dob */
    private Timestamp dob;

    /** The suffix */
    private String suffix;

    /** The professionalDesignation */
    private String professionalDesignation;

    /** The professionalLicense */
    private String professionalLicense;

    /** The cellPhone */
    private String cellPhone;

    /** The fax */
    private String fax;

    /** The homePhone */
    private String homePhone;

    /** The salutation */
    private String salutation;

    /** The nickName */
    private String nickName;

    /** The residenceCounty */
    private String residenceCounty;

    /** The residenceState */
    private String residenceState;

    /** The residenceZip */
    private String residenceZip;

    /** The locations */
    private String locations;

    /** The departments */
    private String departments;

    /** The businessAddressLine1 */
    private String businessAddressLine1;

    /** The businessAddressLine2 */
    private String businessAddressLine2;

    /** The businessAddressLine3 */
    private String businessAddressLine3;

    /** The businessAddressCity */
    private String businessAddressCity;

    /** The businessAddressState */
    private String businessAddressState;

    /** The businessAddressZip */
    private String businessAddressZip;

    /** The businessAddressCountry */
    private String businessAddressCountry;

    /** The onSite */
    private String onSite;

    /** The repRiskQuestion1 */
    private boolean repRiskQuestion1;

    /** The repRiskQuestion2 */
    private boolean repRiskQuestion2;

    /** The repRiskQuestion3 */
    private boolean repRiskQuestion3;

    /** The repRiskQuestion4 */
    private boolean repRiskQuestion4;

    /** The repRiskQuestion5 */
    private boolean repRiskQuestion5;

}
