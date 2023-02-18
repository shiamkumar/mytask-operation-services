package com.ghx.api.operations.util;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.SupplierDTO;
import com.ghx.api.operations.dto.SupplierDetailsDTO;
import com.ghx.api.operations.enums.TierChangeRequestStatus;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.TierChangeRequest;
import com.ghx.api.operations.repository.VendorDetailRepositoryCustom;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * The Util class for Tier change Request
 * @author Krishnan M
 * @since 18/08/2021
 */
@Component
public class TierChangeRequestUtil {

    /** GHX logger */
    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TierChangeRequestUtil.class);

    /** Vendor Detail Repository*/
    @Autowired
    private transient VendorDetailRepositoryCustom vendorDetailRepositoryCustom;

    /** Security Utils */
    @Autowired
    private SecurityUtils securityUtils;
    /**
     * populate tier change request
     * @param tierChangeRequestDTO
     */
    public void populateTierChangeRequest(TierChangeRequest tierChangeRequest,SupplierDetailsDTO supplierDetailsDTO) {
        tierChangeRequest.setId(UUID.randomUUID().toString());
        tierChangeRequest.setStatus(TierChangeRequestStatus.PENDING);
        tierChangeRequest.setSupplier(
                new SupplierDTO(tierChangeRequest.getSupplier().getOid(), supplierDetailsDTO.getFein(), supplierDetailsDTO.getSupplierName()));
        tierChangeRequest.setCurrentTierCode(supplierDetailsDTO.getTierCode());
        tierChangeRequest.setCreatedOn(DateUtils.today());
        tierChangeRequest.setCreatedBy(securityUtils.getCurrentUser());
    }

    /**
     * 
     * @param supplierOid
     * @return
     */
    public SupplierDetailsDTO validateSupplierDetails(String supplierOid) {
        SupplierDetailsDTO supplierDetailsDTO = vendorDetailRepositoryCustom.getVendorDetailsByOid(supplierOid);
        if (Objects.isNull(supplierDetailsDTO)) {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.VENDOR_DETAIL_NOT_FOUND));
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.VENDOR_DETAIL_NOT_FOUND));
        }
        return supplierDetailsDTO;
    }

}
