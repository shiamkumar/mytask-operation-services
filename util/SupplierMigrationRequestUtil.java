package com.ghx.api.operations.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.audit.ChangeAudit;
import com.ghx.api.operations.dto.PricingMigrationDTO;
import com.ghx.api.operations.dto.PricingTierConfigDto;
import com.ghx.api.operations.dto.SupplierResponseDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.AfterMigrationRequest;
import com.ghx.api.operations.model.BeforeMigrationRequest;
import com.ghx.api.operations.model.PricingMigrationRequest;
import com.ghx.api.operations.model.RequestAudit;
import com.ghx.api.operations.repository.PricingConfigRepository;
import com.ghx.api.operations.repository.PricingMigrationRequestRepository;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Ajith
 *
 */
@Component
public class SupplierMigrationRequestUtil {

    /** PricingBusinessValidator Instance */
    @Autowired
    private transient PricingConfigRepository pricingConfigRepository;

    /** MapperFacade Instance */
    @Autowired
    private transient MapperFacade mapper;

    /** The ChangeAudit */
    @Autowired
    private transient ChangeAudit changeAudit;

    /** PricingMigrationRequestRepository Instance */
    @Autowired
    private transient PricingMigrationRequestRepository pricingMigrationRequestRepository;

    /** VendorDetail Repo Instance */
    @Autowired
    private transient VendorDetailRepositoryCustom vendorDetailRepositoryCustom;


    /** users import limit */
    @Value("${suppliers.migration.limit}")
    public int suppliersMigrationLimit;

    /** grp consideration days */
    @Value("${grp.consideration.days}")
    public int grpConsiderationDays;

    /** payment Profile Grace Date */
    @Value("${supplier.migration.paymentProfile.graceDate}")
    public String paymentProfileGraceDate;

    /**
     * 
     * @param pricingMigrationRequestDTO
     */
    public void validateParams(PricingMigrationDTO pricingMigrationRequestDTO) {
        if (StringUtils.isBlank(pricingMigrationRequestDTO.getMigrationFileKey())) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.COMMON_EMPTY, "Migration File Key"));
        }
    }

    /**
     * validate WorkSheet
     * @param worksheet
     */
    public void validateWorkSheet(Sheet worksheet) {
        if (worksheet.getLastRowNum() > suppliersMigrationLimit) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.SUPPLIER_MIGRATION_LIMIT_EXCEED, suppliersMigrationLimit));
        }
    }

    /**
     * 
     * @param feinSet
     */
    public void validateEmptyFein(Set<String> feinSet) {
        if (CollectionUtils.isEmpty(feinSet)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.NO_RECORDS_FOUND_FILE));
        }
    }


    /**
     * @param feinsMap
     * @param migrationFileKey
     * @param errorSuppliersMap
     * @param lookup
     * @param fein
     * @param vendorInfo
     * @param existingMigrationRequest
     */
    public void populateSaveMigrationRequest(Map<String, SupplierResponseDTO> feinsMap, String migrationFileKey,
            Map<String, SupplierResponseDTO> errorSuppliersMap, String fein, List<Map<String, Object>> vendorInfo,
            PricingMigrationRequest existingMigrationRequest) {
        if (Objects.nonNull(existingMigrationRequest)) {
            RequestAudit requestAudit = new RequestAudit();
            if (!existingMigrationRequest.getStatus().equalsIgnoreCase(ConstantUtils.COMPLETED)) {
                populateExistingRequestAudit(migrationFileKey, existingMigrationRequest, requestAudit);
                if (Objects.isNull(errorSuppliersMap.get(fein))) {
                    populateUserAndIdnCount(migrationFileKey, vendorInfo, existingMigrationRequest);
                } else {
                    existingMigrationRequest.setStatus(StringUtils.capitalize(ConstantUtils.FAILED));
                    existingMigrationRequest.setNotes(errorSuppliersMap.get(fein).getStatus());
                    existingMigrationRequest.setFein(fein);
                    existingMigrationRequest.setMigrationFileKey(migrationFileKey);
                }
            } else {
                populateCompleteStatusAudit(feinsMap, migrationFileKey, fein, existingMigrationRequest, requestAudit);
            }
            pricingMigrationRequestRepository.save(existingMigrationRequest);
        } else {
            PricingMigrationRequest pricingMigrationRequest = new PricingMigrationRequest();
            if (Objects.nonNull(errorSuppliersMap.get(fein))) {
                pricingMigrationRequest.setLegalName(errorSuppliersMap.get(fein).getSupplierName());
                pricingMigrationRequest.setStatus(StringUtils.capitalize(ConstantUtils.FAILED));
                pricingMigrationRequest.setNotes(errorSuppliersMap.get(fein).getStatus());
                pricingMigrationRequest.setMigrationFileKey(migrationFileKey);
                pricingMigrationRequest.setFein(fein);
            } else {
                populateUserAndIdnCount(migrationFileKey, vendorInfo, pricingMigrationRequest);
            }
            pricingMigrationRequestRepository.save(pricingMigrationRequest);
        }
    }

    /**
     * @param feinsMap
     * @param migrationFileKey
     * @param fein
     * @param existingMigrationRequest
     * @param requestAudit
     */
    private void populateCompleteStatusAudit(Map<String, SupplierResponseDTO> feinsMap, String migrationFileKey, String fein,
            PricingMigrationRequest existingMigrationRequest, RequestAudit requestAudit) {
        SupplierResponseDTO supplierDTO = feinsMap.get(fein);
        requestAudit.setCreatedBy(changeAudit.getCurrentAuditor().orElse(null));
        requestAudit.setCreatedOn(new Date());
        requestAudit.setUpdatedOn(requestAudit.getCreatedOn());
        requestAudit.setUpdatedBy(requestAudit.getCreatedBy());
        requestAudit.setMigrationFileKey(migrationFileKey);
        requestAudit.setStatus(StringUtils.capitalize(ConstantUtils.FAILED));
        requestAudit.setNotes(supplierDTO.getStatus());
        if (CollectionUtils.isNotEmpty(existingMigrationRequest.getRequestAudit())) {
            existingMigrationRequest.getRequestAudit().add(requestAudit);
        } else {
            List<RequestAudit> requestAuditList = new ArrayList<>();
            requestAuditList.add(requestAudit);
            existingMigrationRequest.setRequestAudit(requestAuditList);
        }
    }

    /**
     * @param migrationFileKey
     * @param lookup
     * @param vendorInfo
     * @param existingMigrationRequest
     */
    private void populateUserAndIdnCount(String migrationFileKey, List<Map<String, Object>> vendorInfo,
            PricingMigrationRequest existingMigrationRequest) {
        populatePricingMigrationRequest(vendorInfo.get(0),migrationFileKey, existingMigrationRequest);
    }

    /**
     * @param migrationFileKey
     * @param existingMigrationRequest
     * @param requestAudit
     */
    private void populateExistingRequestAudit(String migrationFileKey, PricingMigrationRequest existingMigrationRequest, RequestAudit requestAudit) {
        requestAudit.setCreatedBy(existingMigrationRequest.getCreatedBy());
        requestAudit.setCreatedOn(existingMigrationRequest.getCreatedOn());
        requestAudit.setUpdatedBy(existingMigrationRequest.getUpdatedBy());
        requestAudit.setUpdatedOn(existingMigrationRequest.getUpdatedOn());
        requestAudit.setMigrationFileKey(existingMigrationRequest.getMigrationFileKey());
        requestAudit.setStatus(existingMigrationRequest.getStatus());
        requestAudit.setNotes(existingMigrationRequest.getNotes());
        if (CollectionUtils.isNotEmpty(existingMigrationRequest.getRequestAudit())) {
            existingMigrationRequest.getRequestAudit().add(requestAudit);
        } else {
            List<RequestAudit> requestAuditList = new ArrayList<>();
            requestAuditList.add(requestAudit);
            existingMigrationRequest.setRequestAudit(requestAuditList);
        }
        existingMigrationRequest.setCreatedBy(changeAudit.getCurrentAuditor().orElse(null));
        existingMigrationRequest.setCreatedOn(new Date());
        existingMigrationRequest.setUpdatedBy(existingMigrationRequest.getUpdatedBy());
        existingMigrationRequest.setUpdatedOn(existingMigrationRequest.getUpdatedOn());
        existingMigrationRequest.setStatus(ConstantUtils.PENDING);
        existingMigrationRequest.setMigrationFileKey(migrationFileKey);
    }

    /**
     * 
     * @param vendorInfo
     * @param vcsCountMap
     * @param usersCountMap
     * @param migrationFileKey
     * @param lookup
     * @return
     */
    private PricingMigrationRequest populatePricingMigrationRequest(Map<String, Object> vendorInfo, String migrationFileKey,
            PricingMigrationRequest pricingMigrationRequest) {
        pricingMigrationRequest.setFein((String) vendorInfo.get(ConstantUtils.FEIN));
        pricingMigrationRequest.setOid((String) vendorInfo.get(ConstantUtils.VENDOR_OID));
        pricingMigrationRequest.setLegalName((String) vendorInfo.get(ConstantUtils.LEGALNAME));
        pricingMigrationRequest.setStatus(ConstantUtils.PENDING);
        pricingMigrationRequest.setMigrationFileKey(migrationFileKey);
        populateCurrentPlan(pricingMigrationRequest, vendorInfo);
        BeforeMigrationRequest beforeMigrationRequest = new BeforeMigrationRequest();
        AfterMigrationRequest afterMigrationRequest = new AfterMigrationRequest();
        populateBeforeAfterMigration(vendorInfo, pricingMigrationRequest, beforeMigrationRequest, afterMigrationRequest);
        pricingMigrationRequest.setBeforeMigrationRequest(beforeMigrationRequest);
        pricingMigrationRequest.setAfterMigrationRequest(afterMigrationRequest);
        int accountableVcs = pricingMigrationRequest.getBeforeMigrationRequest().getActiveVcs()
                + pricingMigrationRequest.getBeforeMigrationRequest().getRfpmtVcs();
        populatePricingPlan(pricingMigrationRequest, accountableVcs);
        return pricingMigrationRequest;
    }

    /**
     * 
     * @param pricingMigrationRequest
     * @param accountableVcs
     * @param lookup
     */
    private void populatePricingPlan(PricingMigrationRequest pricingMigrationRequest, int accountableVcs) {
        if (StringUtils.equalsAnyIgnoreCase(pricingMigrationRequest.getCurrentPlan(), ConstantUtils.REGULAR_GRP, ConstantUtils.GRP_OAS_PLAN)) {
            List<PricingTierConfigDto> pricingConfig = getPricingTier(ConstantUtils.PREPAID);
            Map<Integer, PricingTierConfigDto> idnCountsByType = new LinkedHashMap<>();
            pricingConfig.forEach(tierConfig -> idnCountsByType.put(tierConfig.getTierSeq(), tierConfig));
            int maxTierSeq = Collections.max(idnCountsByType.keySet());
            pricingMigrationRequest.setPricingPlan(StringUtils.join("Prepaid - ", idnCountsByType.get(maxTierSeq).getTierName()));
            pricingMigrationRequest.setPricingPlanCode(idnCountsByType.get(maxTierSeq).getTierCode());
        } else {
            List<PricingTierConfigDto> pricingConfig = getPricingTier(ConstantUtils.CREDIT_CARD);
            Map<Integer, PricingTierConfigDto> idnCountsByType = new LinkedHashMap<>();
            pricingConfig.forEach(tierConfig -> idnCountsByType.put(tierConfig.getTierSeq(), tierConfig));
            List<Integer> tierSeq = new ArrayList<>();
            idnCountsByType.keySet().forEach(tierIdnCount -> {
                if (CollectionUtils.isEmpty(tierSeq) && accountableVcs <= idnCountsByType.get(tierIdnCount).getAllowedMaxIdn()) {
                    tierSeq.add(idnCountsByType.get(tierIdnCount).getTierSeq());
                }
            });
            if (CollectionUtils.isEmpty(tierSeq)) {
                int maxTierSeq = Collections.max(idnCountsByType.keySet());
                pricingMigrationRequest.setPricingPlan(StringUtils.join("Credit Card - ", idnCountsByType.get(maxTierSeq).getTierName()));
                pricingMigrationRequest.setPricingPlanCode(idnCountsByType.get(maxTierSeq).getTierCode());
            } else {
                pricingMigrationRequest.setPricingPlan("Credit card - " + idnCountsByType.get(tierSeq.get(0)).getTierName());
                pricingMigrationRequest.setPricingPlanCode(idnCountsByType.get(tierSeq.get(0)).getTierCode());
            }
        }

    }

    /**
     * This method will return the list of Pricing tier based on the TierType,
     * throw Business Exception
     * 
     * @param pricingTierConfig
     * @return
     */
    public List<PricingTierConfigDto> getPricingTier(String tierType) {
        Pageable pageable = PageRequest.of(ConstantUtils.PAGE_START_INDEX, ConstantUtils.PAGE_END_INDEX,
                Sort.by(ConstantUtils.TIER_SEQ).ascending().and(Sort.by(ConstantUtils.EFFECTIVE_FROM).ascending()));
        return pricingConfigRepository.findByTierType(tierType, DateUtils.getISODate(new Date(), ConstantUtils.UTC), pageable).stream()
                .map(plan -> mapper.map(plan, PricingTierConfigDto.class)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 
     * @param pricingMigrationRequest
     * @param vendorInfo
     */
    private void populateCurrentPlan(PricingMigrationRequest pricingMigrationRequest, Map<String, Object> vendorInfo) {
        if (Objects.isNull(vendorInfo.get(ConstantUtils.GLOBAL_PROFILE_OID))
                || BooleanUtils.isTrue((Boolean) vendorInfo.get(ConstantUtils.DELETED.toLowerCase(Locale.getDefault())))
                || (Objects.nonNull(vendorInfo.get(ConstantUtils.GLOBAL_PROFILE_OID))
                        && Objects.nonNull(vendorInfo.get(ConstantUtils.EXPIRATION_DATE))
                        && (!org.apache.commons.lang.time.DateUtils.isSameDay((Timestamp) vendorInfo.get(ConstantUtils.EXPIRATION_DATE), new Date())
                                && ((Timestamp) vendorInfo.get(ConstantUtils.EXPIRATION_DATE)).before(new Date())))) {
            pricingMigrationRequest.setCurrentPlan(ConstantUtils.RETAIL_PLAN);
        } else if (Objects.nonNull(vendorInfo.get(ConstantUtils.GRP_PLAN))
                && StringUtils.equalsIgnoreCase(ConstantUtils.OPEN_ACCESS_PLAN, (String) vendorInfo.get(ConstantUtils.GRP_PLAN))) {
                    pricingMigrationRequest.setCurrentPlan(ConstantUtils.GRP_OAS_PLAN);
                } else {
                    pricingMigrationRequest.setCurrentPlan(ConstantUtils.REGULAR_GRP);
                }
    }

    /**
     * 
     * @param fein
     * @param beforeMigrationRequest
     * @param afterMigrationRequest
     * @param vcsCountMap
     * @param usersCountMap
     */
    private void populateBeforeAfterMigration(Map<String, Object> vendorInfo, PricingMigrationRequest pricingMigrationRequest,
            BeforeMigrationRequest beforeMigrationRequest, AfterMigrationRequest afterMigrationRequest) {
        if (Objects.nonNull(MapUtils.getInteger(vendorInfo, ConstantUtils.ACTIVE_USERS))) {
            beforeMigrationRequest.setActiveUsers(MapUtils.getInteger(vendorInfo, ConstantUtils.ACTIVE_USERS));
        }
        if (Objects.nonNull(MapUtils.getInteger(vendorInfo, ConstantUtils.INACTIVE_USERS))) {
            beforeMigrationRequest.setInactiveUsers(MapUtils.getInteger(vendorInfo, ConstantUtils.INACTIVE_USERS));
        }
        if (Objects.nonNull(MapUtils.getInteger(vendorInfo, ConstantUtils.ACTIVE_VCS))) {
            beforeMigrationRequest.setActiveVcs(MapUtils.getInteger(vendorInfo, ConstantUtils.ACTIVE_VCS));
        }
        int inactiveVcs = vendorDetailRepositoryCustom.getInactiveVcCount(pricingMigrationRequest.getFein());
        beforeMigrationRequest.setInactiveVcs(inactiveVcs);
        int rfpmtCount;
        if (pricingMigrationRequest.getCurrentPlan().equalsIgnoreCase(ConstantUtils.RETAIL_PLAN)) {
            if (StringUtils.isNotBlank((String) vendorInfo.get(ConstantUtils.GLOBAL_PROFILE_OID))
                    && Objects.nonNull(vendorInfo.get(ConstantUtils.EXPIRATION_DATE)) && ((Timestamp) vendorInfo.get(ConstantUtils.EXPIRATION_DATE))
                            .after(org.apache.commons.lang.time.DateUtils.addDays(new Date(), -grpConsiderationDays))) {
                rfpmtCount = vendorDetailRepositoryCustom.getAllRfpmtCount(pricingMigrationRequest.getFein());
            } else {
                rfpmtCount = vendorDetailRepositoryCustom.getRfpmtCount(pricingMigrationRequest.getFein(),
                        DateUtils.convertStringToTimeStamp(paymentProfileGraceDate));
            }
        } else {
            rfpmtCount = vendorDetailRepositoryCustom.getAllRfpmtCount(pricingMigrationRequest.getFein());
        }
        beforeMigrationRequest.setRfpmtVcs(rfpmtCount);
        afterMigrationRequest.setActiveUsers(beforeMigrationRequest.getActiveUsers());
        afterMigrationRequest.setInactiveUsers(beforeMigrationRequest.getInactiveUsers());
        afterMigrationRequest.setActiveVcs(beforeMigrationRequest.getActiveVcs() + beforeMigrationRequest.getRfpmtVcs());
        afterMigrationRequest.setInactiveVcs(beforeMigrationRequest.getInactiveVcs());
    }

    /**
     * validate Pending Migration Request
     */
    public void validatePendingMigrationRequest() {
        List<PricingMigrationRequest> pricingMigrationList = pricingMigrationRequestRepository
                .findByStatus(Arrays.asList(ConstantUtils.PENDING, ConstantUtils.MIGRATION_REQUEST_IN_PROGRESS_CONSTANT));
        if (CollectionUtils.isNotEmpty(pricingMigrationList)) {
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.ANOTHER_MIGRATION_REQUEST_IN_PROGRESS));
        }
    }

    /**
     * 
     * @param fein
     * @param migrationFileKey
     * @param notes
     */
    public void populateExistingGrpOrPrepaidInfo(String fein, String migrationFileKey) {
        PricingMigrationRequest migrationRequest = pricingMigrationRequestRepository.findByFein(fein);
        RequestAudit requestAudit = new RequestAudit();
        if (Objects.isNull(migrationRequest)) {
            migrationRequest = new PricingMigrationRequest();
            migrationRequest.setStatus(ConstantUtils.PENDING);
            migrationRequest.setFein(fein);
            migrationRequest.setMigrationFileKey(migrationFileKey);
            migrationRequest.setCurrentPlan(ConstantUtils.REGULAR_GRP);
            migrationRequest.setBeforeMigrationRequest(new BeforeMigrationRequest());
            migrationRequest.setAfterMigrationRequest(new AfterMigrationRequest());
            populatePricingPlan(migrationRequest, 0);
        } else {
            if (!migrationRequest.getStatus().equalsIgnoreCase(ConstantUtils.COMPLETED)) {
                populateExistingRequestAudit(migrationFileKey, migrationRequest, requestAudit);
            } else {
                requestAudit.setCreatedBy(changeAudit.getCurrentAuditor().orElse(null));
                requestAudit.setCreatedOn(new Date());
                requestAudit.setUpdatedOn(requestAudit.getCreatedOn());
                requestAudit.setUpdatedBy(requestAudit.getCreatedBy());
                requestAudit.setMigrationFileKey(migrationFileKey);
                requestAudit.setStatus(StringUtils.capitalize(ConstantUtils.FAILED));
                if (CollectionUtils.isNotEmpty(migrationRequest.getRequestAudit())) {
                    migrationRequest.getRequestAudit().add(requestAudit);
                } else {
                    List<RequestAudit> requestAuditList = new ArrayList<>();
                    requestAuditList.add(requestAudit);
                    migrationRequest.setRequestAudit(requestAuditList);
                }
            }
        }
        pricingMigrationRequestRepository.save(migrationRequest);
    }


}
