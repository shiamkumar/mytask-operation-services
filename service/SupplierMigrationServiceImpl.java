package com.ghx.api.operations.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import org.springframework.stereotype.Service;

import com.ghx.api.operations.dto.BadgeCountInfo;
import com.ghx.api.operations.dto.BlobDTO;
import com.ghx.api.operations.dto.MigrationStatisticsDTO;
import com.ghx.api.operations.dto.PricingMigrationDTO;
import com.ghx.api.operations.dto.PricingMigrationRequestDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.dto.SupplierCountInfo;
import com.ghx.api.operations.dto.SupplierStatisticsDTO;
import com.ghx.api.operations.dto.UserCountInfo;
import com.ghx.api.operations.dto.SupplierResponseDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.PricingMigrationRequest;
import com.ghx.api.operations.model.SupplierStatistics;
import com.ghx.api.operations.repository.DocumentRepositoryCustom;
import com.ghx.api.operations.repository.PricingMigrationRequestRepository;
import com.ghx.api.operations.repository.PricingMigrationRequestRepositoryCustom;
import com.ghx.api.operations.repository.SupplierStatisticsRepository;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.api.operations.util.ErrorConstants;
import com.ghx.api.operations.util.SupplierMigrationRequestUtil;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

import ma.glasnost.orika.MapperFacade;

/**
 * SupplierMigrationServiceImpl class holds the migration request, statistics implementation
 * @author Ajith
 *
 */
@Service
public class SupplierMigrationServiceImpl implements SupplierMigrationService {

    /** GHX logger */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(SupplierMigrationServiceImpl.class);

    /** PricingMigrationRequestRepositoryCustom instance */
    @Autowired
    private transient PricingMigrationRequestRepositoryCustom pricingMigrationRequestRepositoryCustom;

    /** The DocumentRepositoryCustom */
    @Autowired
    private transient DocumentRepositoryCustom documentRepositoryCustom;

    /** The SupplierMigrationRequestUtil */
    @Autowired
    private transient SupplierMigrationRequestUtil supplierMigrationRequestUtil;

    /** VendorDetail Repo Instance */
    @Autowired
    private transient VendorDetailRepositoryCustom vendorDetailRepositoryCustom;

    /** PricingMigrationRequestRepository Instance */
    @Autowired
    private transient PricingMigrationRequestRepository pricingMigrationRequestRepository;
    
    /** The SupplierStatistics Repository instance */
    @Autowired
    private transient SupplierStatisticsRepository supplierStatisticsRepository;

    /** MapperFacade Instance */
    @Autowired
    private transient MapperFacade mapper;


    /**
     * getMigrationRequests implementation
     * 
     */
    @Override
    public Map<String, Object> getMigrationRequests(SearchRequest searchRequest, Pageable pageable) {
        return enhancePricingMigrationList(pricingMigrationRequestRepositoryCustom.getMigrationRequests(searchRequest, pageable));
    }

    /**
     * 
     * @param pricingMigrationRequest
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> enhancePricingMigrationList(Map<String, Object> pricingMigrationRequest) {
        List<PricingMigrationRequestDTO> pricingMigrationList = (List<PricingMigrationRequestDTO>) pricingMigrationRequest
                .get(ConstantUtils.MIGRATION_REQUESTS);
        if (CollectionUtils.isNotEmpty(pricingMigrationList)) {
            List<String> completedFeins = new ArrayList<>();
            pricingMigrationList.stream()
                    .filter(pricingMigration -> StringUtils.equalsIgnoreCase(ConstantUtils.COMPLETED, pricingMigration.getStatus()))
                    .forEach(pricingMigration -> {
                        completedFeins.add(pricingMigration.getFein());
                    });
            if (CollectionUtils.isNotEmpty(completedFeins)) {
                List<Map<String, Object>> usersList = vendorDetailRepositoryCustom.fetchUsersCount(completedFeins);
                Map<String, Object> feinsMap = new HashMap<>();
                usersList.forEach(users -> feinsMap.put((String) users.get(ConstantUtils.FEIN), users));
                populatePricingMigrationList(pricingMigrationList, feinsMap);
            }
        }
        return pricingMigrationRequest;
    }

    /**
     * @param pricingMigrationList
     * @param feinsMap
     */
    @SuppressWarnings("unchecked")
    private void populatePricingMigrationList(List<PricingMigrationRequestDTO> pricingMigrationList, Map<String, Object> feinsMap) {
        pricingMigrationList.forEach(pricingMigration -> {
            if (ConstantUtils.COMPLETED.equalsIgnoreCase(pricingMigration.getStatus())) {
                if (feinsMap.containsKey(pricingMigration.getFein())) {
                    Map<String, Object> userCount = (Map<String, Object>) feinsMap.get(pricingMigration.getFein());
                    pricingMigration.setPaidUserCount(MapUtils.getInteger(userCount, ConstantUtils.PAID_USER));
                    pricingMigration.setUnpaidUserCount(MapUtils.getInteger(userCount, ConstantUtils.UNPAID_USER));
                    pricingMigration.getAfterMigrationRequest()
                            .setActiveUsers(pricingMigration.getPaidUserCount() + pricingMigration.getUnpaidUserCount());
                } else {
                    pricingMigration.getAfterMigrationRequest().setActiveUsers(0);
                }
            }
        });
    }

    /**
     * saveMigrationRequest implementation
     */
    @Override
    public Map<String, Object> saveMigrationRequest(PricingMigrationDTO pricingMigrationRequestDTO) {
        LOGGER.info("saveMigrationRequest Starts - {}", System.currentTimeMillis());
        supplierMigrationRequestUtil.validatePendingMigrationRequest();
        Map<String, Object> migrationRequest = new HashMap<>();
        supplierMigrationRequestUtil.validateParams(pricingMigrationRequestDTO);
        /** fetching document content with mongo key from migration file directory */
        BlobDTO blobDTO = documentRepositoryCustom.getBlob(pricingMigrationRequestDTO.getMigrationFileKey(), ConstantUtils.MIGRATION_REQUEST_FILES);
        InputStream inputStream = new ByteArrayInputStream(blobDTO.getData());
        Sheet worksheet = prepareWorkSheet(blobDTO.getMimeType(), inputStream);
        supplierMigrationRequestUtil.validateWorkSheet(worksheet);
        Map<String, SupplierResponseDTO> feinsMap = new HashMap<>();
        List<SupplierResponseDTO> supplierResponseDTOList = new ArrayList<>();
        List<SupplierResponseDTO> emptyFeinSuppliers = populateFeinsMap(worksheet, feinsMap, supplierResponseDTOList);
        supplierMigrationRequestUtil.validateEmptyFein(feinsMap.keySet());
        Map<String, SupplierResponseDTO> grpSuppliers = new HashMap<>();
        Map<String, SupplierResponseDTO> errorSuppliersMap = validateFein(feinsMap, grpSuppliers);
        feinsMap.putAll(grpSuppliers);
        Collection<SupplierResponseDTO> errorSuppliers = errorSuppliersMap.values();
        errorSuppliers = errorSuppliers.stream().filter(supplier -> !StringUtils.equalsAnyIgnoreCase(supplier.getStatus(),
                ConstantUtils.PREPAID_CONTRACT_EXIST, ConstantUtils.GRP_FEIN_EXIST)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(errorSuppliers) || BooleanUtils.isTrue(pricingMigrationRequestDTO.isSubmitOnError())) {
            saveMigrationRequest(feinsMap, pricingMigrationRequestDTO.getMigrationFileKey(), errorSuppliersMap);
        } else {
            emptyFeinSuppliers.addAll(errorSuppliers);
            migrationRequest.put(ConstantUtils.ERROR_SUPPLIERS, emptyFeinSuppliers);
            migrationRequest.put(ConstantUtils.TOTAL_NO_OF_RECORDS, emptyFeinSuppliers.size());
        }
        return migrationRequest;
    }

    /**
     * save Migration Request
     * @param feinsMap
     * @param migrationFileKey
     * @param errorSuppliersMap
     */
    private void saveMigrationRequest(Map<String, SupplierResponseDTO> feinsMap, String migrationFileKey,
            Map<String, SupplierResponseDTO> errorSuppliersMap) {
        feinsMap.keySet().forEach(fein -> {
            try {
                List<Map<String, Object>> vendorInfo = new ArrayList<>();
                /**
                 * 1. check if the supplier present in error suppliers(already migrated ,
                 *      not active , not present in our system , merge supplier exist)
                 * 1.1. if yes ,skip the vendor info part
                 * 1.2 if no , fetch the vendor info and populate
                 * 
                 */
                if (!errorSuppliersMap.keySet().contains(fein)) {
                    // fetch vendor, grp , count info only if vendor have one act VC with active relationship with user
                    vendorInfo = vendorDetailRepositoryCustom.fetchVendorInfo(fein);
                    if (CollectionUtils.isEmpty(vendorInfo)) {
                        // fetch vendor info only if vendor does not have active VC or active relationship with user
                        Map<String, Object> vendorDetails = vendorDetailRepositoryCustom.getVendorDetailsByFein(fein);
                        if (MapUtils.isEmpty(vendorDetails)) {
                            // populate grp info if vendor does not exist in system but exist in grp contract
                            supplierMigrationRequestUtil.populateExistingGrpOrPrepaidInfo(fein, migrationFileKey);
                            return;
                        }
                        vendorInfo = populateVendorInfo(fein, vendorDetails);
                    }
                }
                PricingMigrationRequest existingMigrationRequest = pricingMigrationRequestRepository.findByFein(fein);
                supplierMigrationRequestUtil.populateSaveMigrationRequest(feinsMap, migrationFileKey, errorSuppliersMap, fein, vendorInfo,
                        existingMigrationRequest);
            } catch (Exception e) {
                LOGGER.error("Exception occured while creating request for fein - {}  ", fein, e);
            }
        });

    }

    /**
     * 
     * @param fein
     * @return
     */
    private List<Map<String, Object>> populateVendorInfo(String fein, Map<String, Object> vendorDetails) {
        List<Map<String, Object>> vendorInfoList = new ArrayList<>();
        Map<String, Object> vendorInfo = new HashMap<>();
        vendorInfo.put(ConstantUtils.LEGALNAME, vendorDetails.get(ConstantUtils.LEGALNAME));
        vendorInfo.put(ConstantUtils.FEIN, fein);
        vendorInfo.put(ConstantUtils.VENDOR_OID, vendorDetails.get(ConstantUtils.VENDOR_OID));
        if (Objects.nonNull(vendorDetails.get(ConstantUtils.DELETED))) {
            vendorInfo.put(ConstantUtils.DELETED, vendorDetails.get(ConstantUtils.DELETED));
        }
        if (Objects.nonNull(vendorDetails.get(ConstantUtils.EXPIRATION_DATE))) {
            vendorInfo.put(ConstantUtils.EXPIRATION_DATE, vendorDetails.get(ConstantUtils.EXPIRATION_DATE));
        }
        if (Objects.nonNull(vendorDetails.get(ConstantUtils.GRP_PLAN))) {
            vendorInfo.put(ConstantUtils.GRP_PLAN, vendorDetails.get(ConstantUtils.GRP_PLAN));
        }
        if (Objects.nonNull(vendorDetails.get(ConstantUtils.GLOBAL_PROFILE_OID))) {
            vendorInfo.put(ConstantUtils.GLOBAL_PROFILE_OID, vendorDetails.get(ConstantUtils.GLOBAL_PROFILE_OID));
        }
        vendorInfoList.add(vendorInfo);
        return vendorInfoList;
    }

    /**
     * 
     * @param worksheet
     * @param feinsMap
     * @param supplierResponseDTOList
     * @return
     */
    private List<SupplierResponseDTO> populateFeinsMap(Sheet worksheet, Map<String, SupplierResponseDTO> feinsMap,
            List<SupplierResponseDTO> supplierResponseDTOList) {
        List<SupplierResponseDTO> emptyFeinSuppliers = new ArrayList<>();
        if (Objects.nonNull(worksheet)) {
            worksheet.forEach(row -> {
                if (row.getRowNum() == ConstantUtils.ZERO_INDEX) {
                    return;
                }
                SupplierResponseDTO supplierResponseDTO = populateSupplierDetails(row);
                if (Objects.nonNull(supplierResponseDTO)) {
                    if (StringUtils.isBlank(supplierResponseDTO.getFein())) {
                        supplierResponseDTO.setStatus(ConstantUtils.SUPPLIER_FEIN_MISSING);
                        emptyFeinSuppliers.add(supplierResponseDTO);
                        return;
                    }
                    supplierResponseDTOList.add(supplierResponseDTO);
                    feinsMap.put(supplierResponseDTO.getFein(), supplierResponseDTO);
                }
            });
        }
        return emptyFeinSuppliers;
    }

    /**
     * 
     * @param row
     * @return
     */
    private SupplierResponseDTO populateSupplierDetails(Row row) {
        DataFormatter formatter = new DataFormatter();
        SupplierResponseDTO supplierResponseDTO = null;
        if (BooleanUtils.isFalse(isRowEmpty(row))) {
            supplierResponseDTO = new SupplierResponseDTO();
            supplierResponseDTO.setFein(formatter.formatCellValue(row.getCell(0)).trim());
            supplierResponseDTO.setSupplierName(formatter.formatCellValue(row.getCell(1)).trim());
        }
        return supplierResponseDTO;
    }

    /**
     * 
     * @param feinsMap
     * @param grpSuppliers
     * @return
     */
    private Map<String, SupplierResponseDTO> validateFein(Map<String, SupplierResponseDTO> feinsMap, Map<String, SupplierResponseDTO> grpSuppliers) {
        Map<String, SupplierResponseDTO> errorSuppliersMap = new HashMap<>();
        feinsMap.keySet().forEach(fein -> validateErrorFein(feinsMap, errorSuppliersMap, fein, grpSuppliers));
        return errorSuppliersMap;

    }

    /**
     * @param feinsMap
     * @param errorSuppliersMap
     * @param fein
     * @param grpSuppliers
     */
    private void validateErrorFein(Map<String, SupplierResponseDTO> feinsMap, Map<String, SupplierResponseDTO> errorSuppliersMap, String fein,
            Map<String, SupplierResponseDTO> grpSuppliers) {
        Map<String, Object> vendorDetails = vendorDetailRepositoryCustom.getVendorDetailsByFein(fein);
        if (MapUtils.isEmpty(vendorDetails)) {
            List<String> associatedFeins = vendorDetailRepositoryCustom.fetchAssociatedGrpFeins(fein);
            if (CollectionUtils.isNotEmpty(associatedFeins)) {
                associatedFeins.forEach(associatedFein -> {
                    SupplierResponseDTO multiFeinSupplierResponseDTO = new SupplierResponseDTO();
                    multiFeinSupplierResponseDTO.setFein(associatedFein);
                    grpSuppliers.put(associatedFein, multiFeinSupplierResponseDTO);
                });
                return;
            }
        }
        if (MapUtils.isEmpty(vendorDetails)) {
            populateStatus(feinsMap, errorSuppliersMap, fein, ConstantUtils.SUPPLIER_DOES_NOT_EXIST);
            return;
        }
        List<String> associatedFeins = vendorDetailRepositoryCustom.fetchAssociatedGrpFeins(fein);
        associatedFeins.remove(fein);
        if (CollectionUtils.isNotEmpty(associatedFeins)) {
            associatedFeins.forEach(associatedFein -> {
                SupplierResponseDTO multiFeinSupplierResponseDTO = new SupplierResponseDTO();
                multiFeinSupplierResponseDTO.setFein(associatedFein);
                grpSuppliers.put(associatedFein, multiFeinSupplierResponseDTO);
            });
            return;
        }
        if (!StringUtils.equalsIgnoreCase(ConstantUtils.USER_STATUS_ACTIVE, (String) vendorDetails.get(ConstantUtils.STATUS))) {
            populateStatus(feinsMap, errorSuppliersMap, fein, ConstantUtils.SUPPLIER_DOES_NOT_EXIST);
            return;
        }
        if (BooleanUtils.isTrue((boolean) vendorDetails.get(ConstantUtils.ENABLE_PROGRAM_CHANGE))) {
            populateStatus(feinsMap, errorSuppliersMap, fein, ConstantUtils.SUPPLIER_ALREADY_MIGRATED);
            return;
        }
        int mergeSupplierCount = vendorDetailRepositoryCustom.fetchMergeSupplierRequest(fein);
        if (mergeSupplierCount > 0) {
            populateStatus(feinsMap, errorSuppliersMap, fein, ConstantUtils.MERGE_SUPPLIER_EXIST);
        }
    }

    /**
     * @param feinsMap
     * @param errorSuppliersMap
     * @param fein
     */
    private void populateStatus(Map<String, SupplierResponseDTO> feinsMap, Map<String, SupplierResponseDTO> errorSuppliersMap, String fein,
            String status) {
        SupplierResponseDTO supplierResponseDTO = feinsMap.get(fein);
        supplierResponseDTO.setStatus(status);
        errorSuppliersMap.put(fein, supplierResponseDTO);
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
        Sheet worksheet;
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
     * Fetch supplier migration statistics
     * paid / unpaid user count for migrated, vc model, new pricing suppliers
     * badge printed users count for migrated suppliers
     * count of suppliers in vc model, new pricing, migrated
     */
    @Override
    public SupplierStatisticsDTO getSupplierMigrationStatistics() {
        LOGGER.info("getSupplierStatistics Starts - {}", System.currentTimeMillis());
        List<SupplierStatistics> statistics = supplierStatisticsRepository.findAll();
        return CollectionUtils.isEmpty(statistics) ? populateSupplierStatistics() : mapper.map(statistics.get(0), SupplierStatisticsDTO.class);
    }


    /**
     * populate supplier statistics details
     */
    private SupplierStatisticsDTO populateSupplierStatistics() {
        MigrationStatisticsDTO statistics = new MigrationStatisticsDTO();
        statistics.setBadge(new BadgeCountInfo());
        statistics.setSuppliers(new SupplierCountInfo());
        statistics.setUsers(new UserCountInfo());

        SupplierStatisticsDTO supplierStatistics = new SupplierStatisticsDTO();
        supplierStatistics.setCurrentStatistics(statistics);
        supplierStatistics.setPreviousStatistics(statistics);
        return supplierStatistics;
    }

}
