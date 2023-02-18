package com.ghx.api.operations.util;

import static com.ghx.api.operations.util.ConstantUtils.CREATED;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.MergeSupplierRequestDTO;
import com.ghx.api.operations.dto.SupplierDTO;
import com.ghx.api.operations.dto.ValidateRequestDTO;
import com.ghx.api.operations.exception.BusinessException;
import com.ghx.api.operations.feign.client.ProfileServiceClient;
import com.ghx.api.operations.messagesource.CustomMessageSource;
import com.ghx.api.operations.model.MergeDetails;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * 
 * The Util class for Merge Supplier Request
 * @author Sundari V
 * @since 03/11/2021
 */
@Component
public class MergeSupplierRequestUtil {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(MergeSupplierRequestUtil.class);

    @Autowired
    private transient ProfileServiceClient profileServiceClient;

    /**
     * populate merge request
     * @param mergeSupplierRequestDTO
     */
    @SuppressWarnings("unchecked")
    public void populateMergeRequest(MergeSupplierRequestDTO mergeSupplierRequestDTO) {
        Map<String, Object> compareVendorDetails = profileServiceClient.compareVendors(populateRequestBody(mergeSupplierRequestDTO));
        if (BooleanUtils.isFalse(StringUtils.equalsAny(MapUtils.getString(compareVendorDetails, "responseCode"), "00", "01"))) {
            LOGGER.error(CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUEST_CANNOT_CREATE));
            throw new BusinessException(CustomMessageSource.getMessage(ErrorConstants.MERGE_REQUEST_CANNOT_CREATE));
        }
        Map<String, Object> compareDetails = MapUtils.getMap(compareVendorDetails, "compareDetails");
        mergeSupplierRequestDTO.setId(UUID.randomUUID().toString());
        mergeSupplierRequestDTO.setStatus(CREATED);
        SupplierDTO deleteSupplier = new SupplierDTO(mergeSupplierRequestDTO.getDeleteSupplierOid(),
                MapUtils.getString(compareDetails, "sourceVendorFein"), MapUtils.getString(compareDetails, "sourceVendorLegalName"));
        SupplierDTO retainSupplier = new SupplierDTO(mergeSupplierRequestDTO.getRetainSupplierOid(),
                MapUtils.getString(compareDetails, "targetVendorFein"), MapUtils.getString(compareDetails, "targetVendorLegalName"));
        mergeSupplierRequestDTO.setDeleteSupplier(deleteSupplier);
        mergeSupplierRequestDTO.setRetainSupplier(retainSupplier);
        mergeSupplierRequestDTO.setMergeDetails(populateMergeDetails(compareDetails));
    }
    
	private ValidateRequestDTO populateRequestBody(MergeSupplierRequestDTO mergeSupplierRequestDTO) {
		ValidateRequestDTO validateRequestDTO = new ValidateRequestDTO();
		validateRequestDTO.setSourceVendorOid(mergeSupplierRequestDTO.getDeleteSupplierOid());
		validateRequestDTO.setDestinationVendorOid(mergeSupplierRequestDTO.getRetainSupplierOid());
		return validateRequestDTO;
	}

    private MergeDetails populateMergeDetails(Map<String, Object> compareDetails) {
        MergeDetails mergeDetails = new MergeDetails();
        mergeDetails.setCommonIdnCount(MapUtils.getIntValue(compareDetails, "commonIDNCount"));

        mergeDetails.setDeleteSupplierIdnCount(MapUtils.getIntValue(compareDetails, "sourceVendorIDNCount"));
        mergeDetails.setDeleteSupplierUserCount(MapUtils.getIntValue(compareDetails, "sourceVendorUserCount"));
        mergeDetails.setDeleteSupplierTierId(MapUtils.getString(compareDetails, "sourceVendorTierId"));
        mergeDetails.setDeleteSupplierTierName(MapUtils.getString(compareDetails, "sourceVendorTierName"));
        mergeDetails.setDeleteSupplierTierType(MapUtils.getString(compareDetails, "sourceVendorTierType"));

        mergeDetails.setRetainSupplierIdnCount(MapUtils.getIntValue(compareDetails, "targetVendorIDNCount"));
        mergeDetails.setRetainSupplierUserCount(MapUtils.getIntValue(compareDetails, "targetVendorUserCount"));
        mergeDetails.setRetainSupplierTierId(MapUtils.getString(compareDetails, "targetVendorTierId"));
        mergeDetails.setRetainSupplierTierName(MapUtils.getString(compareDetails, "targetVendorTierName"));
        mergeDetails.setRetainSupplierTierType(MapUtils.getString(compareDetails, "targetVendorTierType"));
        return mergeDetails;
    }
}
