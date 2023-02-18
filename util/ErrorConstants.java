package com.ghx.api.operations.util;

/**
 * This class is ErrorConstants
 *
 */
public final class ErrorConstants {

    public static final String COMMON_EMPTY = "common.empty";
    public static final String PREPAIDCONTRACT_COMPARE_MAXIDN_AND_CURRENTIDN_COUNT = "prepaidcontract.compare.maxidn.and.currentidn.count";
    public static final String PREPAIDCONTRACT_COMPARE_MAXUSER_AND_CURRENUSER_COUNT = "prepaidcontract.compare.maxuser.and.currentuser.count";
    public static final String PREPAIDCONTRACT_MAXUSER_ZERO_COUNT = "prepaidcontract.maxuser.zero.check";
    public static final String PREPAIDCONTRACT_MAXIDN_ZERO_COUNT = "prepaidcontract.maxidn.zero.check";
    public static final String PREPAIDCONTRACT_STARTDATE_COMPARE_ENDDATE = "prepaidcontract.startdate.compare.enddate";
    public static final String PREPAIDCONTRACT_USER_COUNT_LESSER = "prepaidcontract.user.count.lesser";
    public static final String PREPAIDCONTRACT_NOT_FOUND = "prepaidcontract.not.found";
    public static final String PREPAIDCONTRACT_AUDIT_NOT_FOUND = "prepaidcontract.audit.not.found";
    public static final String PREPAIDCONTRACT_FEIN_DOESNOT_EXISTS = "prepaidcontract.fein.doesnot.exist";
    public static final String PREPAIDCONTRACT_INVALID_PRICING_TIER_CODE = "prepaidcontract.tiercode.invalid";
    public static final String PREPAIDCONTRACT_FEIN_EXISTS = "prepaidcontract.fein.exist";
    /** Prepaid Contract Without Supplier */
    public static final String PREPAIDCONTRACT_WITHOUT_SUPPLIER = "prepaidcontract.without.supplier";
    /** Prepaid Contract with Invalid fein */
    public static final String PREAPIDCONTRACT_NEW_FEIN_INVALID = "prepaidcontract.new.fein.invalid";
    /** Prepaid Contract Suppier Does not Exist in Contract */
    public static final String PREPAIDCONTRACT_FEIN_NOT_EXIST = "prepaidcontract.supplier.not.exist";
    /** Invalid Delete Supplier Request in Prepaid Contract */
    public static final String PREPAIDCONTRACT_UPDATE_SUPPLIER_REQUEST_INVALID = "prepaidcontract.update.supplier.request.invalid";
    public static final String TICKET_INVALID_IDS = "ticket.invalid.ids";
    public static final String TICKET_ALREADY_CLOSED = "ticket.closed.already";
    public static final String TICKET_SIMILAR_REQUEST_NOT_FOUND = "ticket.similarrequest.not.found";
    public static final String CATGEGORY_NOT_EXIST = "lookup.category.not.exist";
    public static final String LOOKUP_NOT_EXIST = "lookup.not.exist";
    public static final String PREPAIDCONTRACT_STARTDATE_COMPARE_CURRENTDATE = "prepaidcontract.startdate.compare.currentdate";
    public static final String MERGE_REQUESTID_NOT_FOUND="merge.requestid.not.found";
    public static final String FROM_DATE_GREATER_EXCEPTION = "request.submitted.fromDate.greater";
    public static final String MERGE_REQUEST_SUPPLIER_CANNOT_BE_DELETED= "merge.request.supplier.cannot.be.deleted";
    public static final String MERGE_REQUEST_DUPLICATE = "merge.request.duplicate.check";
    public static final String SALESFORCE_ID_INVALID = "salesforce.id.invalid";
    public static final String MERGE_REQUEST_CANNOT_CREATE = "merge.request.cannot.create";
    /** constant for move user request duplicate message*/
    public static final String MOVE_USER_REQUEST_DUPLICATE = "move.user.request.duplicate.check";
    /** constant for email id existing validation message*/
    public static final String EMAILID_ALREADY_EXISTING = "email.already.existing";
    /** constant for email id validation message*/
    public static final String EMAILID_NOT_VALID = "email.not.valid";
    /** constant for move user request error message*/
    public static final String MOVE_REQUEST_CANNOT_ALLOW = "move.request.cannot.allow";
    /**constant for move user request id not found message */
    public static final String MOVE_REQUESTID_NOT_FOUND="move.requestid.not.found";
    /** Error message for parsing the date*/
    public static final String PARSE_DATE_EXCEPTION="exception.occured.while.parse.date";
    /** Error message for move user sqs publish failed*/
    public static final String MOVE_USER_SQS_PUBLISH_FAILED = "move.user.sqs.publish.failed";
    /** Error message for document not found */
    public static final String DOCUMENT_NOT_FOUND = "document.not.found";
    /** Error message for no active rep */
    public static final String NO_ACTIVE_REP = "no.active.rep";
    /** Error message for Invalid file */
    public static final String INVALID_FILE = "invalid.file";
    /** Error message for users export exceeding limit */
    public static final String USERS_IMPORT_LIMIT_EXCEED = "users.import.limit.exceeded";
    /** Error message for Users Id missing */
    public static final String USER_ID_MISSING = "users.id.missing";
    /** Error message for no records found */
    public static final String NO_RECORDS_FOUND_FILE = "no.records.found.file";
    /** Error message for no prepaid contract found */
    public static final String PREPAID_CONTRACT_NOT_FOUND = "no.prepaid.contract.found";
    /** Error message for prepaid contract not expired */
    public static final String CONTRACT_NOT_EXPIRED = "prepaid.contract.not.expired";
    /** Error message for invalid vendor tier plan code */
    public static final String VENDOR_INVALID_TIER = "invalid.tier.code.vendor";
    /**Error message for expired prepaid contract updated not allow */
    public static final String EXPIRED_PREPAID_UPDATE_NOT_ALLOW = "expired.prepaid.contract.update.not.allowed";
    /** Error message for choosing donwstairs value not allow */
    public static final String DOWNGRADE_CONTRACT_TIER_NOT_ALLOWED="downgrade.contract.tier.not.allowed";
    /** Tier change request id not found */
    public static final String TIER_CHANGE_REQUEST_ID_NOT_FOUND = "tierchange.requestid.not.found";
    /** Vendor detail not found */
    public static final String VENDOR_DETAIL_NOT_FOUND = "vendor.detail.not.found";
    /** Tier change request cannot be deleted */
    public static final String TIER_CHANGE_REQUEST_CANNOT_BE_DELETED = "tierchange.request.cannot.deleted";
    /** Tier change request already Exists */
    public static final String TIER_CHANGE_REQUEST_ALREADY_EXISTS = "tierchange.requestid.already.exist";
    /** Tier change request idn exceeded */
    public static final String TIER_CHANGE_REQUEST_IDN_EXCEEDED = "tierchange.request.idn.exceeded";
    /** Requested From Date greater - Exception msg */
    public static final String REQUESTED_DATE_EXCEPTION = "ticket.request.date.greater.message";
    /** Processed From Date greater - Exception msg */
    public static final String PROCESSED_DATE_EXCEPTION = "ticket.processed.date.greater.message";
    /** Reviwed From Date greater - Exception msg */
    public static final String REVIEWED_DATE_EXCEPTION = "request.reviewed.date.greater.message";
    /** Fetch IDNs error */
    public static final String FETCH_IDNS_ERROR = "fetch.idns.error";
    /** Fetch Paid Reps error */
    public static final String FETCH_REPS_ERROR = "fetch.paid.reps.error";
    /** Downgrade Request Update with Invalid Status */
    public static final String TIERCHANGE_REQUEST_UPDATE_STATUS_INVALID = "tierchange.request.update.invalid.status";    
    /** Downgrade Request Update with Invalid notes */
    public static final String TIERCHANGE_REQUEST_UPDATE_NOTES_INVALID = "tierchange.request.update.notes.missing";
    /** Downgrade Request Update with Blank Status */
    public static final String TIERCHANGE_REQUEST_UPDATE_STATUS_MISSING = "tierchange.request.update.status.blank";
    /** Downgrade Request Update Not allowed  */
    public static final String TIERCHANGE_REQUEST_UPDATE_NOT_ALLOWED = "tierchange.request.update.status.not.allowed";
    /** Tier Change Request not found */
    public static final String TIERCHANGE_REQUEST_NOT_FOUND = "tierchange.request.not.found";
    /** Tier config is invalid */
    public static final String REQUESTED_TIER_INVALID = "requested.tier.config.invalid";
    /** Supplier Inact error */
    public static final String SUPPLIER_INACT_ERROR = "tierchange.request.supplier.inact.error";
    /** PREPAID Supplier error */
    public static final String SUPPLIER_PREPAID_ERROR = "tierchange.request.prepaid.supplier.error";
    /** Tier Change Idns error */
    public static final String TIER_CHANGE_IDNS_ERROR = "tierchange.request.supplier.idns.error";
    /** Tier change Request Should not be the same*/
    public static final String TIERCHANGE_REQUEST_SHOULD_NOT_BE_SAME = "tierchange.request.should.not.same";    
    /** Tier change Request SQS Publish Failed Error */
    public static final String TIERCHANGE_REQUEST_SQS_PUBLISH_FAILED = "tierchange.request.sqs.publish.failed";
    /** Error message for suppliers migration exceeding limit */
    public static final String SUPPLIER_MIGRATION_LIMIT_EXCEED = "suppliers.migration.limit.exceeded";
    /** Error message for Inprogress migration request */
    public static final String ANOTHER_MIGRATION_REQUEST_IN_PROGRESS = "migration.request.inprogress";
    /** Error message for users delete exceeding limit */
    public static final String USERS_DELETE_LIMIT_EXCEED = "users.delete.limit.exceeded";
    /** Error message for users delete exceeding limit */
    public static final String INVALID_FILE_FORMAT = "invalid.file.format";
    /** Error message for empty file */
    public static final String EMPTY_FILE = "empty.file";
    /** Error message for RESOURCE_NOT_FOUND */
    public static final String RESOURCE_NOT_FOUND = "resource.not.found";
    /** Error message for REQUEST_ALREADY_IN_PROGRESS */
    public static final String REQUEST_ALREADY_IN_PROGRESS = "request.already.inprogress";
    /** Error message for unsupported report type */
    public static final String UNSUPPORTED_REPORT_TYPE = "report.unsupported.type";

    private ErrorConstants() {
    }
}
