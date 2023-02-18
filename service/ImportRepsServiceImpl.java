package com.ghx.api.operations.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ghx.api.operations.audit.ChangeAudit;
import com.ghx.api.operations.dto.BlobDTO;
import com.ghx.api.operations.dto.ImportRepDetailsDTO;
import com.ghx.api.operations.dto.ImportRepRequestDTO;
import com.ghx.api.operations.dto.ImportRepsDTO;
import com.ghx.api.operations.dto.RepDetailsDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierDetailsDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.logger.LogExecutionTime;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.DeleteUserAudit;
import com.ghx.api.operations.model.ImportRepRequest;
import com.ghx.api.operations.repository.DeleteUserAuditRepository;
import com.ghx.api.operations.repository.DocumentRepositoryCustom;
import com.ghx.api.operations.repository.ImportRepRequestRepository;
import com.ghx.api.operations.repository.UserVMRepositoryCustom;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.DateUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.OperationsUtil;
import com.ghx.api.operations.validation.business.ImportRepsBusinessValidator;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;


/**
 * 
 * @author Ajith
 *
 */
@Service
public class ImportRepsServiceImpl implements ImportRepsService {

    /** GHX logger */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(ImportRepsServiceImpl.class);
    /** ACTIVE_USER_IDS constant */
    private static final String ACTIVE_USER_IDS = "activeUserIds";
    /** INACTIVE_USER_IDS constant */
    private static final String INACTIVE_USER_IDS = "inactiveUserIds";
    /** DELETED_USER_AUDIT_USER_IDS constant */
    private static final String DELETED_USER_AUDIT_USER_IDS = "deletedUserAuditUserIds";
    /** NEW_USER_IDS constant */
    private static final String NEW_USER_IDS = "newUserIds";
    /** The DocumentRepositoryCustom */
    @Autowired
    private transient DocumentRepositoryCustom documentRepositoryCustom;
    /** The ImportRepsBusinessValidator */
    @Autowired
    private transient ImportRepsBusinessValidator importRepsBusinessValidator;
    /** The UserVMRepositoryCustom */
    @Autowired
    private transient UserVMRepositoryCustom userVMRepository;
    /** The DeleteUserAuditRepository */
    @Autowired
    private transient DeleteUserAuditRepository deleteUserAuditRepository;
    /** The ImportRepRequestRepository */
    @Autowired
    private transient ImportRepRequestRepository importRepRequestRepository;
    /** The VendorDetailRepositoryCustom */
    @Autowired
    private transient VendorDetailRepositoryCustom vendorDetailRepositoryCustom;
    /** The ChangeAudit */
    @Autowired
    private transient ChangeAudit changeAudit;

    /** The OperationsUtil */
    @Autowired
    private transient OperationsUtil operationsUtil;

    /**
     * importReps service - To import reps from given excel file and upload it in mongo
     *
     */
    @Override
    @LogExecutionTime
    public SupplierDetailsDTO importReps(String idnOid, ImportRepsDTO importRepsDTO) {
        LOGGER.info("ImportRepsServiceImpl - importReps starts in {}", System.currentTimeMillis());

        /** validating idn having one active rep and getting fein and vendorOid from return */
        Map<String, String> vcRelation = importRepsBusinessValidator.validateIdn(idnOid);

        /** validating salesforceId and Email */
        importRepsBusinessValidator.validateSalesforceId(importRepsDTO.getSalesforceId());
        importRepsBusinessValidator.validateEmailId(importRepsDTO.getEmailId());

        /** fetching document content with mongo key from import reps directory */
        BlobDTO blobDTO = documentRepositoryCustom.getBlob(importRepsDTO.getMongoKey(), ConstantUtils.IMPORT_REPS);
        InputStream inputStream = new ByteArrayInputStream(blobDTO.getData());
        Sheet worksheet = prepareWorkSheet(blobDTO.getMimeType(), inputStream);
        /** validating users import limit */
        importRepsBusinessValidator.validateWorkSheet(worksheet);
        Map<String, ImportRepDetailsDTO> repsMap = new HashMap<>();
        List<ImportRepDetailsDTO> importRepDetailsDTOList = new ArrayList<>();
        populateRepsMap(worksheet, repsMap, importRepDetailsDTOList);

        /** validating no records while importing reps */
        importRepsBusinessValidator.validateEmptyRepsImport(importRepDetailsDTOList);

        Set<String> importRepEmailIds = new HashSet<>();
        importRepEmailIds.addAll(repsMap.keySet());
        Map<String, List<String>> userIdsMap = validateActiveAndInactiveUser(importRepEmailIds);
        List<String> deletedUserIds = userIdsMap.get(DELETED_USER_AUDIT_USER_IDS);
        List<String> inactiveUserIds = userIdsMap.get(INACTIVE_USER_IDS);
        populateDeletedUserIds(importRepDetailsDTOList, deletedUserIds);
        populateInactiveUserIds(importRepDetailsDTOList, inactiveUserIds);
        SupplierDetailsDTO supplierDetailsDTO = vendorDetailRepositoryCustom.getVendorDetailsByVendorOid(vcRelation.get(ConstantUtils.VENDOR_OID));
        int newUserCount = CollectionUtils.isNotEmpty(userIdsMap.get(NEW_USER_IDS)) ? userIdsMap.get(NEW_USER_IDS).size() : ConstantUtils.ZERO_INDEX;
        if (supplierDetailsDTO.isPrepaidSupplier()) {
            supplierDetailsDTO.setCumulativeCurrentUsersCount(
                    vendorDetailRepositoryCustom.activeUserCountForPrepaid(supplierDetailsDTO.getPrepaidContractOid()));
            supplierDetailsDTO.setCumulativeUserCountAfterImport(newUserCount + supplierDetailsDTO.getCumulativeCurrentUsersCount());
            supplierDetailsDTO.setCurrentUsersCount(vendorDetailRepositoryCustom.getPaidUserCount(vcRelation.get(ConstantUtils.VENDOR_OID)));
            supplierDetailsDTO.setUserCountAfterImport(newUserCount + supplierDetailsDTO.getCurrentUsersCount());
            if (supplierDetailsDTO.getMaxUserCount() < (newUserCount + supplierDetailsDTO.getCumulativeCurrentUsersCount())) {
                supplierDetailsDTO.setTierExceeded(true);
                return supplierDetailsDTO;
            }
        } else {
            supplierDetailsDTO.setCurrentUsersCount(vendorDetailRepositoryCustom.getPaidUserCount(vcRelation.get(ConstantUtils.VENDOR_OID)));
        }
        ImportRepRequest importRepRequest = populateImportRepRequest(blobDTO, importRepDetailsDTOList, importRepsDTO, idnOid, vcRelation);
        importRepRequestRepository.save(importRepRequest);
        publishUniversalScheduler(importRepRequest.getId());
        return supplierDetailsDTO;
    }

    /**
     *
     * Publish message to UniversalScheduler for Import rep
     * @param oid
     */
    private void publishUniversalScheduler(String oid) {
        operationsUtil.publishToUniversalScheduler(oid, ConstantUtils.IMPORT_REP);
    }

    /**
     *
     * @param worksheet
     * @param repsMap
     * @param importRepDetailsDTOList
     */
    private void populateRepsMap(Sheet worksheet, Map<String, ImportRepDetailsDTO> repsMap, List<ImportRepDetailsDTO> importRepDetailsDTOList) {
        if (Objects.nonNull(worksheet)) {
            worksheet.forEach(row -> {
                if (row.getRowNum() == ConstantUtils.ZERO_INDEX) {
                    return;
                }
                ImportRepDetailsDTO importRepDetailDTO = populateImportRepDetails(row);
                if (Objects.nonNull(importRepDetailDTO)) {
                    importRepDetailsDTOList.add(importRepDetailDTO);
                    repsMap.put(importRepDetailDTO.getEmail(), importRepDetailDTO);
                }
            });
        }
    }

    /**
     *
     * populating worksheet using input stream
     * @param mimeType
     * 
     * @param inputStream
     * @return Sheet
     * 
     */
    @SuppressWarnings("resource")
    private Sheet prepareWorkSheet(String mimeType, InputStream inputStream) {
        Sheet worksheet = null;
        try {
            if (mimeType.equalsIgnoreCase(ConstantUtils.XLS_MIMETYPE)) {
                Workbook workbook = new HSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            } else {
                Workbook workbook = new XSSFWorkbook(inputStream);
                worksheet = workbook.getSheetAt(ConstantUtils.ZERO_INDEX);
            }
        } catch (IOException exception) {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.INVALID_FILE), exception);
        }
        return worksheet;
    }

    /**
     * populating status as inactive user for inactive userIds
     * @param importRepDetailsDTOList
     * @param inactiveUserIds
     */
    private void populateInactiveUserIds(List<ImportRepDetailsDTO> importRepDetailsDTOList, List<String> inactiveUserIds) {
        inactiveUserIds.forEach(inactiveUser -> importRepDetailsDTOList.forEach(importRepDetailsDTO -> {
            if (inactiveUser.equalsIgnoreCase(importRepDetailsDTO.getEmail())) {
                importRepDetailsDTO.setStatus(ConstantUtils.INACTIVE_USER);
            }
        }));
    }

    /**
     * populating status as inactive user for deleted user audit userIds
     * @param importRepDetailsDTOList
     * @param deletedUserIds
     */
    private void populateDeletedUserIds(List<ImportRepDetailsDTO> importRepDetailsDTOList, List<String> deletedUserIds) {
        deletedUserIds.forEach(deletedUser -> importRepDetailsDTOList.forEach(importRepDetailsDTO -> {
            if (deletedUser.equalsIgnoreCase(importRepDetailsDTO.getEmail())) {
                importRepDetailsDTO.setStatus(ConstantUtils.DELETED_USER);
            }
        }));
    }

    /**
     * populating ImportRepRequest
     * @param blobDTO
     * @param importRepDetailsDTOList
     * @param importRepsDTO
     * @param idnOid
     * @param fein
     * @return ImportRepRequest
     */
    private ImportRepRequest populateImportRepRequest(BlobDTO blobDTO, List<ImportRepDetailsDTO> importRepDetailsDTOList, ImportRepsDTO importRepsDTO,
            String idnOid, Map<String, String> vcRelation) {
        ImportRepRequest importRepRequest = new ImportRepRequest();
        importRepRequest.setEmailId(importRepsDTO.getEmailId());
        importRepRequest.setOid(idnOid);
        importRepRequest.setFileName(blobDTO.getFileName());
        importRepRequest.setMongoKey(importRepsDTO.getMongoKey());
        importRepRequest.setSalesforceId(importRepsDTO.getSalesforceId());
        importRepRequest.setUserDetails(importRepDetailsDTOList);
        importRepRequest.setUploadedBy(changeAudit.getCurrentAuditor().orElse(null));
        importRepRequest.setUploadedOn(Calendar.getInstance().getTime());
        importRepRequest.setRequestType(ConstantUtils.IDN);
        importRepRequest.setFein(vcRelation.get(ConstantUtils.FEIN));
        importRepRequest.setCustomerOid(vcRelation.get(ConstantUtils.CUSTOMEROID));
        importRepRequest.setProviderName(vcRelation.get(ConstantUtils.PROVIDER_NAME));
        importRepRequest.setSupplierName(vcRelation.get(ConstantUtils.LEGALNAME));
        importRepRequest.setId(UUID.randomUUID().toString());
        return importRepRequest;
    }

    /**
     * validate Active and InactiveUser
     * @param importRepEmailIds
     * @return
     */
    private Map<String, List<String>> validateActiveAndInactiveUser(Set<String> importRepEmailIds) {
        List<String> activeUserIds = userVMRepository.getActiveUsers(importRepEmailIds);
        List<String> inactiveUserIds = userVMRepository.getInactiveUsers(importRepEmailIds);
        importRepEmailIds.removeAll(activeUserIds);
        importRepEmailIds.removeAll(inactiveUserIds);
        List<DeleteUserAudit> deleteUserAuditList = deleteUserAuditRepository.findDeleteUserAudit(importRepEmailIds,
                DateUtils.getMinusDates(ConstantUtils.DAYS));
        List<String> deletedUserAuditUserIds = new ArrayList<>();
        deleteUserAuditList.forEach(deleteUserAudit -> deletedUserAuditUserIds.add(deleteUserAudit.getUserId()));
        importRepEmailIds.removeAll(deletedUserAuditUserIds);
        Map<String, List<String>> userIdsMap = new HashMap<>();
        userIdsMap.put(ACTIVE_USER_IDS, activeUserIds);
        userIdsMap.put(INACTIVE_USER_IDS, inactiveUserIds);
        userIdsMap.put(DELETED_USER_AUDIT_USER_IDS, deletedUserAuditUserIds);
        userIdsMap.put(NEW_USER_IDS, new ArrayList<>(importRepEmailIds));
        return userIdsMap;
    }

    /**
     * populate Import RepDetails
     * @param row
     * @return ImportRepDetailsDTO
     */
    private ImportRepDetailsDTO populateImportRepDetails(Row row) {
        DataFormatter formatter = new DataFormatter();
        ImportRepDetailsDTO importRepDetailsDTO = null;
        if (BooleanUtils.isFalse(isRowEmpty(row))) {
            importRepsBusinessValidator.validateUserId(formatter.formatCellValue(row.getCell(4)));
            importRepDetailsDTO = new ImportRepDetailsDTO();
            importRepDetailsDTO.setFirstName(formatter.formatCellValue(row.getCell(0)).trim());
            importRepDetailsDTO.setMiddleInitial(formatter.formatCellValue(row.getCell(1)).trim());
            importRepDetailsDTO.setLastName(formatter.formatCellValue(row.getCell(2)).trim());
            importRepDetailsDTO.setTitle(formatter.formatCellValue(row.getCell(3)));
            importRepDetailsDTO.setEmail(formatter.formatCellValue(row.getCell(4)).trim().toLowerCase(Locale.getDefault()));
            importRepDetailsDTO.setWorkPhone(regulateDataField(formatter.formatCellValue(row.getCell(5)).trim()));
            importRepDetailsDTO.setDob(regulateDOB(formatter.formatCellValue(row.getCell(6))));
            importRepDetailsDTO.setSuffix(formatter.formatCellValue(row.getCell(7)));
            importRepDetailsDTO.setProfessionalDesignation(formatter.formatCellValue(row.getCell(8)));
            importRepDetailsDTO.setProfessionalLicense(regulateDataField(formatter.formatCellValue(row.getCell(9))));
            importRepDetailsDTO.setFax(regulateDataField(formatter.formatCellValue(row.getCell(10))));
            importRepDetailsDTO.setHomePhone(formatter.formatCellValue(row.getCell(11)));
            importRepDetailsDTO.setSalutation(formatter.formatCellValue(row.getCell(12)).toUpperCase(Locale.getDefault()));
            importRepDetailsDTO.setNickName(formatter.formatCellValue(row.getCell(13)));
            importRepDetailsDTO.setResidenceCounty(formatter.formatCellValue(row.getCell(14)));
            importRepDetailsDTO.setResidenceState(formatter.formatCellValue(row.getCell(15)));
            importRepDetailsDTO.setResidenceZip(regulateDataField(formatter.formatCellValue(row.getCell(16))));
            importRepDetailsDTO.setLocations(formatter.formatCellValue(row.getCell(17)));
            importRepDetailsDTO.setDepartments(formatter.formatCellValue(row.getCell(18)));
            importRepDetailsDTO.setBusinessAddressLine1(formatter.formatCellValue(row.getCell(19)));
            importRepDetailsDTO.setBusinessAddressLine2(formatter.formatCellValue(row.getCell(20)));
            importRepDetailsDTO.setBusinessAddressLine3(formatter.formatCellValue(row.getCell(21)));
            importRepDetailsDTO.setBusinessAddressCity(formatter.formatCellValue(row.getCell(22)));
            importRepDetailsDTO.setBusinessAddressState(formatter.formatCellValue(row.getCell(23)));
            importRepDetailsDTO.setBusinessAddressZip(regulateDataField(formatter.formatCellValue(row.getCell(24))));
            importRepDetailsDTO.setBusinessAddressCountry(formatter.formatCellValue(row.getCell(25)));
            importRepDetailsDTO.setOnSite(formatter.formatCellValue(row.getCell(26)));
            importRepDetailsDTO.setRepRiskQuestion1(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(27))));
            importRepDetailsDTO.setRepRiskQuestion2(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(28))));
            importRepDetailsDTO.setRepRiskQuestion3(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(29))));
            importRepDetailsDTO.setRepRiskQuestion4(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(30))));
            importRepDetailsDTO.setRepRiskQuestion5(Boolean.parseBoolean(formatter.formatCellValue(row.getCell(31))));
            importRepDetailsDTO.setRowNum(row.getRowNum());
            importRepDetailsDTO.setStatus(ConstantUtils.YET_TO_START);
        }
        return importRepDetailsDTO;
    }

    /**
     * regulate Data Field
     * @param dataField
     * @return
     */
    private String regulateDataField(String dataField) {
        if (StringUtils.isNotBlank(dataField) && dataField.indexOf(".0") > ConstantUtils.ZERO_INDEX) {
            return dataField.substring(0, dataField.length() - 2);
        }
        return dataField;
    }

    /**
     * regulate DOB
     * @param dob
     * @return
     */
    private Timestamp regulateDOB(String dob) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (StringUtils.isNotBlank(dob) && dob.indexOf('/') >= ConstantUtils.ONE_INDEX) {
            String[] dobArray = dob.trim().split("/");
            int month = Integer.parseInt(dobArray[ConstantUtils.ZERO_INDEX]);
            int day = Integer.parseInt(dobArray[ConstantUtils.ONE_INDEX]);
            if (month >= ConstantUtils.ONE_INDEX && month <= 12) {
                YearMonth yearMonthObject = YearMonth.of(year, month);
                int daysInMonth = yearMonthObject.lengthOfMonth();
                if (day <= daysInMonth) {
                    return getTimestamp(year - ConstantUtils.ONE_INDEX, month, day);
                }
            }
        }
        return null;
    }

    /**
     * get timestamp
     * @param year
     * @param month
     * @param day
     * @return Timestamp
     */
    private Timestamp getTimestamp(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, month - ConstantUtils.ONE_INDEX, day);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * get all import rep requests by idnOid
     */
    @LogExecutionTime
    @Override
    public Map<String, Object> getImportRepRequests(SearchRequest searchRequest, Pageable pageable) {

        LOGGER.info("ImportRepsServiceImpl getImportRepRequests idn oid {}", searchRequest.getOid());
        importRepsBusinessValidator.validateIdn(searchRequest.getOid());
        List<ImportRepRequestDTO> importRepRequestDTOs = importRepRequestRepository.findAllUploadHistory(searchRequest, pageable);
        if (CollectionUtils.isEmpty(importRepRequestDTOs)) {
            LOGGER.error(CustomMessageSource.getMessage(ConstantUtils.NO_RECORDS_FOUND), HttpStatus.OK);
            throw new BusinessException(CustomMessageSource.getMessage(ConstantUtils.NO_RECORDS_FOUND));
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ConstantUtils.IMPORT_REP_REQUEST, importRepRequestDTOs);
        map.put(ConstantUtils.TOTAL_NO_OF_RECORDS, importRepRequestRepository.findUploadImportRequestCount(searchRequest));
        return map;
    }

    /**
     * check row is empty or not
     * @param row
     * @return boolean isEmpty
     */
    private boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        if (Objects.nonNull(row)) {
            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }
    

    /**
     * Get import request user details by Import request Id
     */
    @Override
    public Map<String, Object> getImportRequestUserDetails(SearchRequest searchRequest, Pageable pageable) {

        LOGGER.info("ImportRepsServiceImpl getImportRequestUserDetails idn oid {} ,importRequestId {}", searchRequest.getOid(), searchRequest.getImportRequestId());
        importRepsBusinessValidator.validateIdn(searchRequest.getOid());

        List<RepDetailsDTO> repDetailsDTOs = importRepRequestRepository.findAllImportRequestUserDetails(searchRequest, pageable);
        if (CollectionUtils.isEmpty(repDetailsDTOs)) {
            LOGGER.error(CustomMessageSource.getMessage(ConstantUtils.NO_RECORDS_FOUND), HttpStatus.OK);
            throw new BusinessException(CustomMessageSource.getMessage(ConstantUtils.NO_RECORDS_FOUND));
        }
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put(ConstantUtils.IMPORT_REP_USER_DETAIL, repDetailsDTOs);
        userDetails.put(ConstantUtils.TOTAL_NO_OF_RECORDS, importRepRequestRepository.findImportRequestUserCount(searchRequest));


        return userDetails;
    }

}
