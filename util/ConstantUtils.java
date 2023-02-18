package com.ghx.api.operations.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Rajasekar Jayakumar
 * 
 *         Class to maintain constants.
 *
 */
public class ConstantUtils {
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORIZE_API_ROLE = "SCP Admin";
    public static final String ROLE_VMRM = "VMRM";
    public static final String ROLE_SCP_ADMIN = "SCP ADMIN";
    public static final String ROLE_VENDOR_ADMIN = "VENDOR_ADMIN";
    public static final String ROLE_VENDOR_SUPER_ADMIN = "VENDOR_SUPER_ADMIN";
    public static final String ROLE_VENDOR_ADMIN_ID_CONFIRM = "VENDOR_ADMIN_ID_CONFIRM";
    public static final String ROLE_VENDOR_REP = "VENDOR_REP";
    public static final String ROLE_VREP = "VREP";
    public static final String ROLE_ADMIN_PROGRAM_CHANGE = "ROLE_ADMIN_PROGRAM_CHANGE";
    public static final String VM_SYSTEM_USER = "vm_system_user";
    public static final String ROLE_SYSTEM_USER = "SYSTEM_USER";
    public static final String ADMIN = "Admin";
    public static final String USER_FIELD_USERNAME = "userName";
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_STAGE = "stg";
    public static final String SPRING_PROFILE_PRODUCTION = "prd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String USER_STATUS_ACTIVE = "ACT";
    public static final Integer MAXIMUM_TIER_COUNT = 2;
    public static final String EFFECTIVE_FROM = "effectiveFrom";
    public static final String PRICING_TIER_CONFIG = "PricingTierConfig";
    public static final String PRICING_TIER_CONFIG_ID = "PricingTierConfigId";
    public static final String TIER_CODE = "tierCode";
    public static final String TIER_NAME = "tierName";
    public static final String TIER_TYPE = "tierType";
    public static final String TIER_SEQ = "tierSeq";
    public static final String PRICE_PER_USER = "pricePerUser";
    public static final String ALLOWED_MAX_IDN = "allowedMaxIdn";
    public static final String MAX_USER_COUNT = "maxUserCount";
    public static final String MIN_USER_COUNT = "minUserCount";
    public static final String CREDIT_CARD = "Creditcard";
    public static final String EXPIRED_ON = "expiredOn";
    public static final int PAGE_START_INDEX = 0;
    public static final int PAGE_END_INDEX = 20;
    public static final int ZERO_INDEX = 0;
    public static final String MON_DD_YYYY = "Mon dd, yyyy";
    public static final String CREATED_ON = "createdOn";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_KK_MM_A = "yyyy-MM-dd KK:mm a";
    public static final String MMM_DD_YYYY_KK_MM_A = "MMM dd, yyyy KK:mm a";
    /** MM/dd/yyyy HH:mm:ss */
    public static final String MM_DD_YYYY_HH_MM_SS= "MM/dd/yyyy HH:mm:ss";

    public static final Integer MAXIMUM_USER_COUNT = 999999;
	
    /* Prepaid Contract Constants starts **/
    public static final String MM_DD_YYYY = "MM/dd/yyyy";
    public static final String ALL = "All";
    public static final String ACTIVE = "Active";
    public static final String PENDING = "Pending";
    public static final String EXPIRED = "Expired";

    /* Prepaid Contract Report Constants starts **/
    public static final String REPORT_TYPE_PDF = "PDF";
    public static final String REPORT_TYPE_CSV = "CSV";
    public static final String REPORT_TYPE_XLS = "XLS";
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    public static final String CONTENT_TYPE_CSV = "text/csv";

    public static final String JASPER_PROPERTIES = "classpath:jasperreport.properties";
    public static final String JASPER_PDF_JRXML = "jrxml.pdf.location";
    public static final String JASPER_CSV_JRXML = "jrxml.csv.location";
    public static final String JASPER_XLS_JRXML = "jrxml.xls.location";
    public static final String JASPER_PDF_TICKET_JRXML = "jrxml.pdf.ticket.location";
    public static final String JASPER_CSV_TICKET_JRXML = "jrxml.csv.ticket.location";
    public static final String JASPER_XLS_TICKET_JRXML = "jrxml.xls.ticket.location";
    /** Import rep user detail export file name */
    public static final String FILENAME_IMPORT_REP_USER_DETAIL = "ImportRepUserDetail";

    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    public static final String BASE64 = "base64";
    public static final String PREPAID_CONTRACTS_LIST = "prepaidContractsList";
    public static final String TOTAL_NO_OF_RECORDS = "totalNoOfRecords";
    public static final String FILENAME = "prepaidContracts-report";
    public static final String PREPAIDCONTRACTS_EXPORT = "PrepaidContracts";   
    /* Prepaid Contract Report Constants ends **/

    /* Contract Tickets Constants Starts ***/
    public static final String STATUS = "status";
    public static final String FEIN = "fein";
    public static final String ORGANIZATION_NAME = "organizationName";
    public static final String TICKET_NUMBER = "ticketNumber";
    public static final String REQUEST_ORIGIN = "requestOrigin";
    public static final String REQUEST_DETAILS = "requestDetails";
    public static final String REQUESTED_BY = "requestedBy";
    public static final String REQUESTED_ON = "requestedOn";
    public static final String CLOSURE_RESULT = "closureResult";
    public static final String CLOSURE_NOTES = "closureNotes";
    public static final String PROCESSED_BY = "processedBy";
    public static final String PROCESSED_ON = "processedOn";
    public static final String INITIAL_REQUEST = "initialRequest";
    public static final String TYPE = "type";
    public static final String SUBTYPE = "subType";
    public static final String SIMILAR_REQUESTED_ON = "similarRequests.requestedOn";
    public static final String SIMILAR_INITIAL_REQUEST = "similarRequests.initialRequest";
    public static final String SIMILAR_REQUESTED_BY = "similarRequests.requestedBy";
    public static final String SIMILAR_REQUESTS = "similarRequests";
    public static final String ID = "_id";
    public static final Boolean TRUE = true;
    public static final String DOLLAR = "$";
    public static final String PROJECT = "$project";
    public static final String COUNT = "count";
    public static final String TICKETS_LIST = "ticketsList";
    public static final String REQUEST_COUNT = "requestCount";
    public static final String SIMILAR_REQUEST_COUNT = "similarRequestCount";
    public static final String ADD_FIELDS = "addFields";
    public static final String SIZE = "size";
    public static final String ISO = "ISO";
    public static final String EST = "US/Eastern";
    public static final String FILENAME_TICKET = "ContractTickets-report";
    public static final String JASPER_STATUS = "jasperStatus";
    public static final String TICKETS_EXPORT = "Tickets"; 
    /* Contract Tickets Constants Ends ***/

    /* Support Tickets Constants starts **/
    public static final String OPEN = "Open";
    public static final String CLOSED = "Closed";
    public static final String PRICING_CONTRACT = "PRICING_CONTRACT";
    public static final String SLASH = "\\";
    public static final String CASE_INSENSITIVE = "i";

    public static final Map<String, String> SORT_MAP = initPrepaidContractSortMap();

    public static final Map<String, String> SORT_TICKET = initTicketSortMap();
    
    public static final String ROLE_EXTERNAL_CLIENT = "EXTERNAL_CLIENT";
    public static final String SUB_IDENTIFIER = "SubjectIdentifier";
    public static final String EXTERNAL_USER = "registration@externalclient.com";
    public static final String USER = "User";
    public static final String COMMA = ",";
    public static final String STATE = "STATE";
    public static final String AWS_SQS_MESSAGE_ENABLED = "aws.sqs.messageenabled";
    public static final String SQS_PREPAID_CONTRACT_LAMBDA = "aws.sqs.functions.prepaidcontract";
    public static final String AWS_SQS_URL = "aws.sqs.url";
    public static final String PREPAID_CONTRACT_UPDATION = "PREPAID_CONTRACT_UPDATION";
    public static final String DOMAIN_TYPE = "DOMAIN_TYPE";
    public static final String PROCESS_TYPE = "PROCESS_TYPE";
    public static final String ERROR = "Error";
    public static final String PUBLISHED = "PUBLISHED";
    public static final String PREPAID_CONTRACT="PREPAID_CONTRACT";
    public static final String CREATE="CREATE";
    public static final String UPDATE="UPDATE";
    public static final String DELETE="DELETE";
    public static final String MERGE_REQUESTS = "mergeRequestsList";
    public static final String MERGE_SUPPLIER_COLLECTION = "merge_supplier_request";
    public static final String CREATED_BY = "createdBy";
    public static final String NOTES = "notes";
    public static final String SALESFORCE_ID = "salesforceId";
    public static final String RETAIN_SUPPLIER = "retainSupplier";
    public static final String DELETE_SUPPLIER = "deleteSupplier";
    public static final String DELETED = "DELETED";
    public static final String DELETE_SUPPLIER_FEIN = "deleteSupplier.fein";
    public static final String DELETE_SUPPLIER_NAME= "deleteSupplier.legalName";
    public static final String RETAIN_SUPPLIER_FEIN = "retainSupplier.fein";
    public static final String RETAIN_SUPPLIER_NAME= "retainSupplier.legalName";
    public static final String CREATED = "CREATED";
    public static final String ERROR_MSG = "errorMessage";
    public static final String MERGE_REQUEST_ID = "mergeSupplierRequestId";
    public static final String JASPER_XLS_SUPPLIER_MERGE_REQUEST_JRML = "jrxml.xls.supplier.mergerequest.location";
    public static final String SUPPLIER_MERGEREQUEST_FILE = "MergeSupplierRequest-report";
    public static final String UTC = "UTC";
    public static final String PREPAID = "Prepaid";
    public static final String SEQ = "seq";
    /** IMport rep request collection name */
    public static final String IMPORT_REP_REQUEST_COLLECTION = "import_rep_request";
    /** Uploaded on */
    public static final String UPLOADED_DATE = "uploadedOn";
    /** Uploaded by */
    public static final String UPLOADED_BY = "uploadedBy";
    /** The mongoKey */
    public static final String MONGO_KEY = "mongoKey";
    /** The fileName */
    public static final String FILE_NAME = "fileName";
    /** The oid */
    public static final String OID = "oid";
    /** The failed */
    public static final String FAILED = "failed";
    /** The success */
    public static final String SUCCESS = "success";
    /** The User Uploaded */
    public static final String USER_UPLOADED = "User Uploaded";
    /**constant cmUserExists */
    public static final String CM_USER = "cmUserExists";
    /** SQS component name*/
    public static final String OPERATIONS="OPERATIONS";
    /**move request id */
    public static final String MOVEREQUEST_ID = "moveRequestId";
    /**unviersal scheduler lambda */
    public static final String UNIVERSAL_SCHEDULER_LAMBDA = "aws.sqs.functions.universal-scheduler";
    /**sqs publish job name */
    public static final String JOB_NAME = "jobName";
    /** sqs publish domain name*/
    public static final String DOMAIN_NAME = "domainName";
    /** sqs publish component name */
    public static final String COMPONENT_NAME = "componentName";
    /** constant movesuer request file name */
    public static final String MOVEUSER_REQUEST_FILE = "MoveUserRequest-report";
    /** constant move user file location*/
    public static final String JASPER_XLS_MOVE_USER_REQUEST_JRML = "jrxml.xls.moveuser.request.location";
    /**constant for date format */
    public static final String MMM_DD_YYYY = "MMM dd, yyyy";

    /** source supplier */
    public static final String SOURCE_SUPPLIER = "sourceSupplier";

    /** destination supplier */
    public static final String DESTINATION_SUPPLIER = "destinationSupplier";

    /** source supplier fein */
    public static final String SOURCE_SUPPLIER_FEIN = "sourceSupplier.fein";

    /** source supplier legal name */
    public static final String SOURCE_SUPPLIER_NAME = "sourceSupplier.legalName";

    /** destination supplier fein */
    public static final String DESTINATION_SUPPLIER_FEIN = "destinationSupplier.fein";

    /** destination supplier legal name */
    public static final String DESTINATION_SUPPLIER_NAME = "destinationSupplier.legalName";

    /** Email id */
    public static final String EMAIL_ID = "emailId";

    /** Updated email id */
    public static final String UPDATED_EMAIL_ID = "updatedEmailId";

    /** Name */
    public static final String NAME = "name";

    /** Move User list */
    public static final String MOVE_REQUESTS = "moveRequestsList";

    /** Move User Collection */
    public static final String MOVE_USER_COLLECTION = "move_user_request";

    public static final String CERTIFICATION_AGENCY = "CERTIFICATION_AGENCY";

    /** Import reps */
    public static final String IMPORT_REPS = "import_rep_files";

    /** Days */
    public static final int DAYS = 365;
    /** Content Type */
    public static final String CONTENT_TYPE = "contentType";
    /** yet to start status */
    public static final String YET_TO_START = "Yet to start";
    /** IDN constant */
    public static final String IDN = "IDN";
    /** Delete user status */
    public static final String DELETED_USER = "Deleted user";
    /** Inactive user status */
    public static final String INACTIVE_USER = "Inactive user";
    /** Vendor oid */
    public static final String VENDOR_OID = "vendorOid";
    /** USERS COUNT AFTER IMPORT */
    public static final String USERS_COUNT_AFTER_IMPORT = "usersCountAfterImport";
    /** USERS COUNT BEFORE EXPORT */
    public static final Object CURRENT_USER_COUNT = "currentUserCount";
    /** Import Rep Request */
    public static final String IMPORT_REP_REQUEST = "importRepRequest";
    /** No record found */
    public static final String NO_RECORDS_FOUND = "no.records.found";
    /** Uploaded Constant */
    public static final String UPLOADED = "UPLOADED";
    /** Import Rep User Detail */
    public static final String IMPORT_REP_USER_DETAIL = "importRepUserDetail";
    /** Email*/
    public static final String EMAIL = "email";
    /** lastName*/
    public static final String LAST_NAME = "lastName";
    /** firstName*/
    public static final String FIRST_NAME = "firstName";
    /** user details dot*/
    public static final String USER_DETAILS_DOT = "userDetails.";
    /** user details */
    public static final String USER_DETAILS = "userDetails";
     
    /** Suppliers list constant */
    public static final String SUPPLIERS_LIST = "suppliersList";
    /** XLS Mimetype */
    public static final String XLS_MIMETYPE = "application/vnd.ms-excel";
    /** match*/
    public static final String MATCH = "match";
    /**pay load */
    public static final String PAYLOAD = "payload";
    /** dot */
    public static final String DOT_OPERATOR = ".";
    /** enable program change*/
    public static final String ENABLE_PROGRAM_CHANGE = "enableProgramChange";
    /**legal name */
    public static final String LEGALNAME="legalName";
    /**supplier list */
    public static final String SUPPLIER_LIST = "supplierList";
    /**untouched for ES */
    public static final String UNDERSCORE_UNTOUCHED = "_untouched";
    /**vendor detail index name */
    public static final String VS_VENDOR_DETAIL = "vs_vendor_detail";
    /**vendor status */
    public static final String VENDORSTATUS= "vendorStatus";

    /** Suppliers name constant */
    public static final String SUPPLIER_NAME = "supplierName";
    
    /** wildcard for ES */
    public static final String WILDCARD = "wildcard";
    /** star symbol */
    public static final String STAR_SYMBOL = "*";
    /** supplier tier plan code */
    public static final String SUPPLIER_TIER_CODE = "supplierTierPlanCode";
    /** asc order */
    public static final String ASC = "ASC";
    /**get */
    public static final String GET = "get";
    /**export */
    public static final String EXPORT = "export";

    /** customer Oid */
    public static final String CUSTOMEROID = "customerOid";

    /** completed Constant */
    public static final String COMPLETED = "Completed";

    /** completed Constant */
    public static final String IMPORT_REP = "ImportRep";
    /** Domain Oid */
    public static final String DOMAIN_OID = "domainOid";
    /**Import Rep request id */
    public static final String IMPORT_REP_REQUEST_ID = "importRepRequestId";
    
    /** prepaid contract oid */
    public static final String CONTRACT_OID = "prepaidContractOid";
    /** marked users unpaid */
    public static final String UNPAID_USERS_COUNT = "markedUsersUnpaid";
    /** PROVIDER NAME constant */
    public static final String PROVIDER_NAME = "providerName";

    /** ES user sync */
    public static final String ES_USER_SYNC = "ES_USER_SYNC";
    /** user detail lambda */
    public static final String SQS_USER_DETAIL_SYNC_LAMBDA = "aws.sqs.functions.es-user-detail-sync";
    /** SUPPLIER constant */
    public static final String TYPE_SUPPLIER = "SUPPLIER";
    /**constat for and operator */
    public static final String AND = "and";
    /** Index one */
    public static final int ONE_INDEX = 1;
    
    /** Tier change request Id */
    public static final String TIER_CHANGE_REQUEST_ID = "tierChangeRequestId";

    /**search export */
    public static final String SEARCH_EXPORT = "searchExport";
    /** empty */
    public static final String EMPTY = "";

    /** Reviewed By */
    public static final String REVIEWED_BY = "reviewedBy";
    /** Reviewed On */
    public static final String REVIEWED_ON = "reviewedOn";
    /** Requested Tier Code */
    public static final String REQUESTED_TIER = "requestedTierCode";
    /** Current Tier Code */
    public static final String CURRENT_TIER = "currentTierCode";
    /** supplier fein */
    public static final String SUPPLIER_FEIN = "supplier.fein";

    /** supplier legal name */
    public static final String SUPPLIER_LEGALNAME = "supplier.legalName";

    /** Tier change request collection */
    public static final String TIER_CHANGE_REQUEST = "tier_change_request";

    /** Supplier - constant */
    public static final String SUPPLIER = "supplier";
    /** VC count - constant */
    public static final String VC_COUNT = "vc_count";

    /** User Count - constant */
    public static final String USER_COUNT = "userCount";

    /** IDN Count - constant */
    public static final String IDN_COUNT = "idnCount";

    /** Idns list count - constant */
    public static final String IDNS_LIST = "activeIDNSList";

    /** Paid - constant */
    public static final String PAID = "paid";

    /** Mock User for Default Security Context Holder in Unit Test */
    public static final String MOCK_USER = "mock_user";
    
    /** Mock User Role for Default Security Context Holder in Unit Test */
    public static final String ROLE_MOCK = "ROLE_MOCK";

    /** Code 00 - constant */
    public static final String C00 = "00";

    /** Code 01 - constant */
    public static final String C01 = "01";

    /** DOWNGRADE_ALLOWED - constant */
    public static final String DOWNGRADE_ALLOWED = "DOWNGRADE_ALLOWED";

    /** DOWNGRADE_NOT_ALLOWED - constant */
    public static final String DOWNGRADE_NOT_ALLOWED = "DOWNGRADE_NOT_ALLOWED";

    /** Tier change valid - constant */
    public static final String TIER_CHANGE_VALID = "tierchange.request.supplier.valid";

    /** SUPPLIER_INACT - constant */
    public static final String SUPPLIER_INACT = "SUPPLIER_INACT";

    /** IDNS_EXCEED - constant */
    public static final String IDNS_EXCEED = "IDNS_EXCEED";

    /** VALID_REQUEST - constant */
    public static final String VALID_REQUEST = "VALID_REQUEST";

    /** Tier Plan Code - constant */
    public static final String TIER_PLAN_CODE = "tierPlanCode";
    
    /**  TierChangeRequest Export file name */
    public static final String TIER_MANAGEMENT_REQUEST_EXPORT_FILE = "TierManagementRequest-Report";
    
    /** TierChangeRequest Export template file location*/
    public static final String JASPER_XLS_TIER_CHANGE_REQUEST_JRXML = "tierchangerequest.export.template.location";

    /** Credit Card String with Space */
    public static final String CREDIT_CARD_WITH_SPACE = "Credit Card";
    
    /** Rep Es sync lambda function */
    public static final String REP_ES_SYNC_LAMBDA = "aws.sqs.functions.rep-es-migration";
    
    /** ES Domain Type */
    public static final String ES_REP_SYNC = "ES_REP_SYNC";
    /** Vendor */
    public static final String VENDOR = "VENDOR";
    /** merge Vendor */
    public static final String MERGE_VENDOR = "MERGE_VENDOR";

    /** National Plan */
    public static final String NATIONAL = "CCN";
    /** CURRENT_PLAN */
    public static final String CURRENT_PLAN = "currentPlan";
    /** PRICING_PLAN */
    public static final String PRICING_PLAN = "pricingPlan";
    /** REGEX */
    public static final String REGEX = "[\\.\\,\\'\\&\\-\\(\\)]";
    /** PRICING_MIGRATION_REQUEST_COLLECTION */
    public static final String PRICING_MIGRATION_REQUEST_COLLECTION = "pricing_migration_request";
    /** MIGRATION_REQUESTS */
    public static final String MIGRATION_REQUESTS = "migrationRequests";
    /** MIGRATION_REQUEST_FILES */
    public static final String MIGRATION_REQUEST_FILES = "pricing_migration_files";
    /** IN_PROGRESS status */
    public static final String IN_PROGRESS = "IN_PROGRESS";
    /** INACT constant */
    public static final String INACT = "INACT";
    /** ACTCR constant */
    public static final String ACTCR = "ACTCR";
    /** RFPMT constant */
    public static final String RFPMT = "RFPMT";
    /** RETAIL_PLAN constant */
    public static final String RETAIL_PLAN = "Retail Plan";
    /** REGULAR_GRP */
    public static final String REGULAR_GRP = "Regular GRP";
    /** GRP_OAS_PLAN Constant */
    public static final String GRP_OAS_PLAN = "GRP-OAS Plan";
    /** GLOBAL_PROFILE_OID Constant */
    public static final String GLOBAL_PROFILE_OID = "globalProfileOid";
    /** Error suppliers */
    public static final String ERROR_SUPPLIERS = "errorSuppliers";
    /** PRICING_PLAN_CODE */
    public static final String PRICING_PLAN_CODE = "pricingPlanCode";

    /** Domain Type USERS */
    public static final String USERS = "USERS";
    /** constant for number one */
    public static final int NUMBER_ONE = 1;
    /** Process type USER_DETAIL_CREATE */
    public static final String USER_DETAIL_CREATE = "USER_DETAIL_CREATE";
    /** IDN Details*/
    public static final String IDN_DETAILS= "idnDetails";

    /** Tier Change Requestor Name */
    public static final String REQUESTOR_NAME = "tierChangeRequestor";
    /** Supplier fein Missing constant */
    public static final String SUPPLIER_FEIN_MISSING = "A supplier FEIN is missing in this file.";
    /** Supplier Does not exist constant */
    public static final String SUPPLIER_DOES_NOT_EXIST = "This supplier does not currently exist in our system.";
    /** Supplier already migrated constant */
    public static final String SUPPLIER_ALREADY_MIGRATED = "This supplier has already migrated.";
    /** Merge Supplier exists constant */
    public static final String MERGE_SUPPLIER_EXIST = "A merge request already exists for this supplier.";
    /** Migration Request In progress constant */
    public static final String MIGRATION_REQUEST_IN_PROGRESS_CONSTANT = "In Progress";
    /** PREPAID_CONTRACT_EXIST constant */
    public static final String PREPAID_CONTRACT_EXIST = "Supplier not available in our system.But prepaid contract exist";
    /** GRP_FEIN_EXIST constant */
    public static final String GRP_FEIN_EXIST = "GRP fein exist";
    /** EXPIRATION_DATE constant */
    public static final String EXPIRATION_DATE = "expirationDate";
    /** INACT constant */
    public static final String INACT_SMALL_CASE = "InACT";
    /** OPEN_ACCESS_PLAN */
    public static final String OPEN_ACCESS_PLAN = "OpenAccessPlan";
    /** GRP_PLAN */
    public static final String GRP_PLAN = "grpPlan";
    /** Reprocess */
    public static final String REPROCESS = "Reprocess";
    /** PAID_USER */
    public static final String PAID_USER = "paidUser";
    /** UNPAID_USER */
    public static final String UNPAID_USER = "unpaidUser";
    /** RELATIONSHIP_LIST_SPLITTER */
    public static final String RELATIONSHIP_LIST_SPLITTER = "#:#";
    /** ACTIVE_USERS */
    public static final String ACTIVE_USERS = "activeUsers";
    /** INACTIVE_USERS */
    public static final String INACTIVE_USERS = "inactiveUsers";
    /** ACTIVE_VCS */
    public static final String ACTIVE_VCS = "activeVcs";
    
    /** PRICING_SUPPLIER_MIGRATION */
    public static final String PRICING_SUPPLIER_MIGRATION = "PRICING_SUPPLIER_MIGRATION";
    
    /** Export Move user template file location*/
    public static final String JASPER_PDF_MOVE_USER_REQUEST_JRXML = "jrxml.pdf.move.user.request.location";
    /** Export Move user template file location */
    public static final String JASPER_XLS_MOVE_USER_REQUEST_JRXML = "jrxml.xls.move.user.request.location";
    /** Export Move user filename */
    public static final String MOVE_USER_REQUEST_FILENAME = "MoveUserRequests";
    /** Export Merge Request template file location */
    public static final String JASPER_PDF_MERGE_SUPPLIER_REQUEST_JRXML = "jrxml.pdf.merge.supplier.request.location";
    /** Export Merge Request template file location */
    public static final String JASPER_XLS_MERGE_SUPPLIER_REQUEST_JRXML = "jrxml.xls.merge.supplier.request.location";
    /** constant for MERGE_SUPPLIER_REQUESTS */
    public static final String MERGE_SUPPLIER_REQUESTS = "MergeSupplierRequests";
    /** constant for TRACK_TOTAL_HITS */
    public static final String TRACK_TOTAL_HITS = "track_total_hits";
    /** autoVerificationStatus */
    public static final String AUTO_VERIFICATION_STATUS = "autoVerificationStatus";
    /** verificationMessage */
    public static final String VERIFICATION_MESSAGE = "verificationMessage";
    /** updatedOn */
    public static final String UPDATED_ON = "updatedOn";
    /** beforeMigrationRequest */
    public static final String BEFORE_MIGRATION_REQUEST = "beforeMigrationRequest";
    /** afterMigrationRequest */
    public static final String AFTER_MIGRATION_REQUEST = "afterMigrationRequest";
    /** input */
    public static final String INPUT = "input";
    /** as */
    public static final String AS = "as";
    /** CONDITION */
    public static final String CONDITION = "cond";
    /** EQUAL */
    public static final String EQUAL = "$eq";
    /** FILTER */
    public static final String FILTER = "$filter";
    /** AUDIT_DETAILS */
    public static final String AUDIT_DETAILS = "auditDetails";
    /** COMPLETED_ON */
    public static final String COMPLETED_ON = "completedOn";
    /** COMPLETED_ON_END_TIME */
    public static final String COMPLETED_ON_END_TIME = "$completedOn.endTime";
    /** AUDIT_DETAILS_EVENTID */
    public static final String AUDIT_DETAILS_EVENTID = "$$auditDetails.eventId";
    /** SUPPLIER_OFFICER_CONTACT_SANCTION Constant */
    public static final String SUPPLIER_OFFICER_CONTACT_SANCTION = "SupplierOfficeContactSanction";

    /** DETAILS */
    public static final String DETAILS = "details";
    /** AUDIT Trail Alias */
    public static final String AUDIT_TRAIL_ALIAS = "audit_trail_all";
    /** TOTAL RECORDS */
    public static final Object ES_TOTAL_RECORDS = "TotalRecords";
    /** AUDIT TYPE */
    public static final String AUDIT_TYPE = "auditType";

    /** Export Audit Trails Reports PDF template file location */
    public static final String JASPER_PDF_EXPORT_AUDIT_TRIALS_REPORTS_JRXML = "jrxml.pdf.document.audit.trails.reports.location";
    /** Export Audit Trails Reports XLS template file location */
    public static final String JASPER_XLS_EXPORT_AUDIT_TRIALS_REPORTS_JRXML = "jrxml.xls.document.audit.trails.reports.location";
    /** constant for EXPORT_AUDIT_TRIALS_REPORTS */
    public static final String EXPORT_AUDIT_TRIALS_REPORTS = "DocumentAuditTrail";
    /** AUDIT_MAPPING_COLLECTION constant */
    public static final String AUDIT_MAPPING_COLLECTION = "audit_mapping";
    /** USER_DELETE_REQUEST_FILES */
    public static final String USER_DELETE_REQUEST_FILES = "user_delete_request_files";
    /** USER_ID */
    public static final String USER_ID = "userId";
    /** VENDOR_NAME */
    public static final String VENDOR_NAME = "vendorName";
    /** AVAILABLE_USER_COUNT */
    public static final String AVAILABLE_USER_COUNT = "availableUserCount";
    /** TOTAL_USER_COUNT */
    public static final String TOTAL_USER_COUNT = "totalUserCount";
    /** USER_VALIDATION_RESPONSE_LIST */
    public static final String USER_VALIDATION_RESPONSE_LIST = "userValidationResponseDTOList";

     /**AUDIT_TYPES constant */
	public static final String AUDIT_TYPES = "auditTypes";
	/** AUDIT_NAME constant */
	public static final String AUDIT_NAME = "name";
	/** userDeleteRequest collectionName constant */
	public static final String USER_DELETE_REQUEST = "user_delete_request";
	
    /** XLSX Mimetype */
    public static final String XLSX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    
    /**
     * Audittype not found error message
     */
    public static final String AUDIT_TYPE_NOT_FOUND = "Audittype not found";
	
    /**
     * Error message for incorrect global search params
     */
    public static final String GLOBAL_SEARCH_PARAMETERS_INCORRECT = "Global search parameters incorrect";
    
    /** Email URL */
    public static final String EMAIL_URL = "emailurl";
    /** Search */
    public static final String SEARCH = "search";
    /** Underscore symbol */
    public static final String UNDERSCORE = "_";
    
    /** Colon Symbol*/
    public static final String COLON = ":";
    
    /**salesForceId */
    public static final String SALES_FORCE_ID = "salesForceId";
    /**totalCount */
    public static final String TOTAL_COUNT = "totalCount";
    /**successCount*/
    public static final String SUCCESS_COUNT = "successCount";
    /**failureCount */
    public static final String FAILURE_COUNT= "failedCount";
    /**deletedOn */
    public static final String DELETED_ON = "deletedOn";
    /**deletedBy*/
    public static final String DELETED_BY = "deletedBy";
    /** massUserDeleteRequests*/
    public static final String MASS_USER_DELETE_REQUEST = "massUserDeleteRequests";
    /** userOid */
    public static final String USER_OID = "userOid";
    /** Pending caps letter */
    public static final String PENDING_CAPS = "PENDING";
    /** Retry Mass User Delete */
    public static final String RETRY_MASS_USER_DELETE = "RetryMassUserDelete";
    /** User Delete RequestId */
    public static final String USER_DELETE_REQUEST_ID = "userDeleteRequestId";
    /** Success Message for Retry API */
    public static final String EVENT_PUBLISHED = "Event Published Successfully";
    /** reason */
    public static final String REASON = "reason";
    /** DELETED_SUCCESSFULLY */
    public static final String DELETED_SUCCESSFULLY = "Deleted Successfully";
    
    /** userDeleteRequest user detail export file name */
    public static final String FILENAME_USERDELETEREQUEST = "UserDeleteRequest";
    
    /** DocUploadRequest report file name */
    public static final String FILENAME_DOCUPLOADREQUEST = "MassDocumentUpload-History";  
    
    /** DocUploadRequest export file name */
    public static final String DOCUPLOADREQUEST_EXPORT = "DocUploadRequest"; 
    
    /** DocUploadRequestDetails report file name */    
    public static final String FILENAME_DOCUPLOADREQUESTDETAILS = "MassDocumentUpload-Details";
    
    /** DocUploadRequest export file name */
    public static final String DOCUPLOADREQUESTDETAILS_EXPORT = "DocUploadRequestDetails";     
    
    /** jasper file location */
    public static final String JRXML_LOCATION = "jrxml.location";
    
    /** jasper pdf file name */
    public static final String JASPER_PDF_USERDELETEREQUEST_JRXML = "jrxml.pdf.userdeleterequest.location";
    
    /** jasper csv file name */
    public static final String JASPER_CSV_USERDELETEREQUEST_JRXML = "jrxml.csv.userdeleterequest.location";
    
    /** jasper xls file name */
    public static final String JASPER_XLS_USERDELETEREQUEST_JRXML = "jrxml.xls.userdeleterequest.location";
    
    /** jasper pdf file name */
    public static final String JASPER_PDF_DOCUPLOADREQUEST_JRXML = "jrxml.pdf.docuploadrequest.location";
    
    /** jasper csv file name */
    public static final String JASPER_CSV_DOCUPLOADREQUEST_JRXML = "jrxml.csv.docuploadrequest.location";
    
    /** jasper xls file name */
    public static final String JASPER_XLS_DOCUPLOADREQUEST_JRXML = "jrxml.xls.docuploadrequest.location";
    
    /** jasper pdf file name */   
    public static final String JASPER_PDF_DOCUPLOADREQUESTDETAILS_JRXML = "jrxml.pdf.docuploadrequestdetails.location";
    
    /** jasper csv file name */
    public static final String JASPER_XLS_DOCUPLOADREQUESTDETAILS_JRXML = "jrxml.xls.docuploadrequestdetails.location";
    
    /** jasper xls file name */
    public static final String JASPER_CSV_DOCUPLOADREQUESTDETAILS_JRXML = "jrxml.csv.docuploadrequestdetails.location";
    
	/** UserDetailResponse */
	public static final String USER_DETAIL_RESPONSE = "UserDetailResponse";
	/** Success */
	public static final String SUCCESS_CAMEL_CASE = "Success";
	/**COMPLETED */
	public static final String COMPLETED_CAPS = "COMPLETED";
	/** FAILED */
	public static final String FAILED_CAPS = "FAILED";
	/** Failure */
	public static final String FAILURE = "Failure";
	/** DELETE_RRP_DEF */
	public static final String DELETE_RRP_DEF = "DELETE_RRP_DEF";
	/** SQS_DOC_ALERT_LAMBDA constant */
	public static final String SQS_DOC_ALERT_LAMBDA = "aws.sqs.functions.doc-alert";
	/** DOUBLE_HASH */
	public static final String DOUBLE_HASH = "##";
	/** MM/dd/yyyy HH:mm:ss a */
    public static final String MM_DD_YYYY_HH_MM_SS_A = "MM/dd/yyyy HH:mm:ss a";
	/** MM/dd/yyyy hh:mm:ss a */
    public static final String EST_DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";        
    /** DocUploadRequest collectionName constant */
	public static final String DOC_UPLOAD_REQUEST = "doc_upload_request";
	/** DocUploadRequest collectionName constant */
	public static final String DOC_UPLOAD_REQUEST_DETAILS = "doc_upload_request_details";
	 /** SortType */
	public static final String SORT_TYPE_DESC = "DESC";
	 /** updatedBy */
    public static final String UPDATED_BY = "updatedBy";
    /** DOC_UPLOAD_REQUEST_FILES */
    public static final String DOC_UPLOAD_REQUEST_FILES = "doc_upload_request";
    /**templateMongoKey */
    public static final String TEMPLATE_MONGO_KEY = "templateMongoKey";
    /**repsMappingMongoKey */
    public static final String REPS_MAPPING_MONGO_KEY = "repsMappingMongoKey";
    /**allReps */
    public static final String ALL_REPS= "allReps";
    /**templateIdName */
    public static final String TEMPLATE_OIDS = "templateOids";
    /**templateStatus */
    public static final String TEMPLATE_STATUS= "templateStatus";
    /**docUploadDetails */
    public static final String DOC_UPLOAD_DETAILS = "docUploadDetails";	
    /**successUserCount*/
    public static final String SUCCESS_USER_COUNT = "successUserCount";
    /**failureCount */
    public static final String FAILURE_USER_COUNT= "failedUserCount";
    /**docStatus */
    public static final String DOC_STATUS = "docStatus";
    /** DocUploadRequest Map constant */
    /**templates */
    public static final String TEMPLATES = "templates";
    /** DocUploadRequest Map constant */
	public static final String DOCUPLOAD_REQUEST = "docUploadRequest";
	/** DocUploadRequest Map constant */
	public static final String DOCUPLOAD_REQUEST_DETAILS = "docUploadRequestDetails";
	/** DocUploadRequestId constant */
	public static final String DOCUPLOAD_REQUEST_ID = "docUploadRequestId";	
    /** DocUploadRequest details dot*/
    public static final String DOC_UPLOAD_REQUEST_DETAILS_DOT = "doc_upload_request_details.";    
	/** TEMPLATE constant */
	public static final String TEMPLATE = "template";
	/** DOCUMENT_OID constant */
	public static final String DOCUMENT_OID = "documentOid";
	/** FAILURE_REASON constant */
	public static final String FAILURE_REASON = "failureReason";
	/** ERROR_MESSAGE constant */
	public static final String ERROR_MESSAGE = "errorMessage";
	/** MAIL_SENT constant */
	public static final String MAIL_SENT = "mailSent";
        /** PACKAGE */
        public static final String PACKAGE = "package";
        /** TRUE STR */
        public static final String TRUE_STR = "true";
        /** SERVICE BUNDLING FEATURE FLAG */
        public static final String SERVICE_BUNDLING_FEATURE_FLAG = "servicebundling.enabled";
	
    private static Map<String, String> initPrepaidContractSortMap() {
        Map<String, String> sortMap = new HashMap<>();
        sortMap.put("contractStartDate", "pc.contract_start_date");
        sortMap.put("contractEndDate", "pc.contract_end_date");
        sortMap.put("currentUserCount", "pc.current_user_count");
        sortMap.put("currentIDNCount", "pc.current_idn_count");
        sortMap.put("maxUserCount", "pc.max_user_count");
        sortMap.put("maxIDNCount", "pc.max_idn_count");
        sortMap.put("updatedBy", "pc.updated_by");

        return sortMap;
    }

    private static Map<String, String> initTicketSortMap() {
        Map<String, String> sortMap = new HashMap<>();
        sortMap.put("fein", "fein");
        sortMap.put("requestCount", "similarRequestCount");
        sortMap.put("requestedBy", "similarRequests.requestedBy");
        sortMap.put("organizationName", "organizationName");
        sortMap.put("requestedOn", "similarRequests.requestedOn");
        sortMap.put("requestOrigin", "requestOrigin");
        sortMap.put("processedOn", "processedOn");

        return sortMap;
    }

}
