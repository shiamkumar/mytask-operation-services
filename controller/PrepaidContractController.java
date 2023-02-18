package com.ghx.api.operations.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghx.api.operations.dto.PrepaidContractDTO;
import com.ghx.api.operations.dto.PrepaidContractSupplierDTO;
import com.ghx.api.operations.dto.SearchRequest;
import com.ghx.api.operations.service.PrepaidContractService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * The Class PrepaidContractController.
 */
@RestController
@Validated
@RequestMapping("/v1/pricing/prepaidcontracts")
public class PrepaidContractController {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(PrepaidContractController.class);

    @Autowired
    private transient PrepaidContractService prepaidContractService;

    @Autowired
    private transient ResourceLoader resourceLoader;

    /**
     * This method will fetch all Prepaid Contract details available with prepaid
     * plan.
     *
     * @param fein
     *            the fein
     * @param supplierName
     *            the supplier name
     * @param contractStatus
     *            the contract status
     * @param pricingTierCode
     *            the pricing tier code
     * @param startFromDt
     *            the start from dt
     * @param startToDt
     *            the start to dt
     * @param endFromDt
     *            the end from dt
     * @param endToDt
     *            the end to dt
     * @param updatedBy
     *            the updated by
     * @param pageable
     *            the pageable
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> fetchPrepaidGridContracts(@RequestParam(required = false) String fein,
            @RequestParam(required = false) String supplierName, @RequestParam(required = false) String contractStatus,
            @RequestParam(required = false) String pricingTierCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate startFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate startToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate endFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate endToDate,
            @RequestParam(required = false) String updatedBy, Pageable pageable) {
        LOGGER.info("PrepaidContractController fetchPrepaidGridContracts fein {} start {}", fein , System.currentTimeMillis());
        PrepaidContractDTO prepaidDetails = new PrepaidContractDTO(fein, supplierName, contractStatus, startFromDate, startToDate, endFromDate, endToDate,
                updatedBy, pricingTierCode);
        return new ResponseEntity<>(prepaidContractService.fetchPrepaidGridContracts(prepaidDetails, pageable,ConstantUtils.GET), HttpStatus.OK);
    }

    /**
     * This method will Export all Prepaid Contract details available with prepaid
     * plan.
     *
     * @param exportType
     *            the export type
     * @param response
     *            the response
     * @param fein
     *            the fein
     * @param supplierName
     *            the supplier name
     * @param contractStatus
     *            the contract status
     * @param pricingTierName
     *            the pricing tier code
     * @param startFromDt
     *            the start from dt
     * @param startToDt
     *            the start to dt
     * @param endFromDt
     *            the end from dt
     * @param endToDt
     *            the end to dt
     * @param updatedBy
     *            the updated by
     * @param pageable
     *            the pageable
     * @throws IOException
     */
    @GetMapping(path = "/export/{exportType}")
    public <T> void exportPrepaidContracts(@PathVariable String exportType, HttpServletResponse response, @RequestParam(required = false) String fein,
            @RequestParam(required = false) String supplierName, @RequestParam(required = false) String contractStatus,
            @RequestParam(required = false) String pricingTierCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate startFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate startToDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate endFromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = ConstantUtils.MM_DD_YYYY) LocalDate endToDate,
            @RequestParam(required = false) String updatedBy, Pageable pageable) throws IOException {
        LOGGER.info("PrepaidContractController exportPrepaidContracts fein {} start {}", fein , System.currentTimeMillis());

        PrepaidContractDTO prepaidDetails = new PrepaidContractDTO(fein, supplierName, contractStatus, startFromDate, startToDate, endFromDate,
                endToDate, updatedBy, pricingTierCode);
        prepaidContractService.exportPrepaidContracts(prepaidDetails, this.resourceLoader, ConstantUtils.FILENAME, pageable, exportType, response);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public ResponseEntity<PrepaidContractDTO> getById(@PathVariable String id) {
        LOGGER.info("PrepaidContractController exportGridContracts oid {} start {}", id , System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.getById(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PrepaidContractDTO> save(@RequestBody PrepaidContractDTO prepaidContractDTO) {
        LOGGER.info("PrepaidContractController save fein {} start {}", prepaidContractDTO.getFein(), System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.save(prepaidContractDTO), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/{id}")
    public ResponseEntity<PrepaidContractDTO> update(@PathVariable String id, @RequestBody PrepaidContractDTO prepaidContractDTO) {
        LOGGER.info("PrepaidContractController update oid {} start {}", id , System.currentTimeMillis());
        prepaidContractDTO.setOid(id);
        return new ResponseEntity<>(prepaidContractService.update(id, prepaidContractDTO), HttpStatus.OK);
    }

    /**
     * API to delete contract and mark users unpaid
     * @param id
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public ResponseEntity<PrepaidContractDTO> delete(@PathVariable String id) {
        LOGGER.info("PrepaidContractController delete oid {} start {}", id, System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.delete(id), HttpStatus.OK);
    }

    /**
     * search vendor by fein
     * @param fein
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/vendor/{fein}")
    public ResponseEntity<PrepaidContractDTO> searchVendorByFein(@PathVariable String fein) {
        LOGGER.info("PrepaidContractController searchVendorByFein fein {} start {}", fein , System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.getByFein(fein), HttpStatus.OK);
    }

    /**
     * Get Prepaid Contract Audits By Contract Id
     * @param id
     * @param pageable
     * @return
     */
    @GetMapping(path = "/{id}/audit")
    public ResponseEntity<Page<PrepaidContractDTO>> getAuditsByPrepaidContractId(@PathVariable String id, Pageable pageable) {
        LOGGER.info("PrepaidContractController getAuditsByPrepaidContractId id:{} ", id);
        return new ResponseEntity<>(prepaidContractService.getAuditsByPrepaidContractId(id, pageable), HttpStatus.OK);
    }
    
    /**
     * get suppliers
     * @param fein
     * @param legalName
     * @param pageable
     * @return
     */
    @GetMapping(path = "/suppliers")
    public ResponseEntity<Map<String, Object>> getVendors(@RequestParam(required = false) String fein,
            @RequestParam(required = false) String legalName, Pageable pageable) {
        SearchRequest searchRequest = SearchRequest.builder().pageable(pageable).fein(fein).legalName(legalName).build();
        return new ResponseEntity<>(prepaidContractService.searchSuppliers(searchRequest), HttpStatus.OK);

    }
    /**
     * API to fetch all suppliers associated with a prepaid contract
     * @param id
     * @param pageable
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/suppliers")
    public ResponseEntity<Map<String, Object>> getSupplierDetails(@PathVariable String id, Pageable pageable) {
        LOGGER.info("PrepaidContractController getSupplierDetails id {} start {}", id , System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.getSupplierDetails(id, pageable), HttpStatus.OK);
    }
    
    /**
     * API to mark users unpaid for expired prepaid contract
     * @param id(prepaidContractOid)
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.PATCH, path = "/{id}/expirecontract")
    public ResponseEntity<PrepaidContractDTO> updateExpiredContract(@PathVariable String id) {
        LOGGER.info("PrepaidContractController updateExpiredContract id {} start {}", id, System.currentTimeMillis());
        return new ResponseEntity<>(prepaidContractService.updateExpiredContract(id), HttpStatus.OK);
    }

    /**
     * 
     * @param id
     * @param suppliers
     * @return
     */
    @PostMapping(path= "/{id}/suppliers")
    public ResponseEntity<PrepaidContractDTO> addSuppliersInContract(@PathVariable String id, @RequestBody List<PrepaidContractSupplierDTO> suppliers) {
        return new ResponseEntity<PrepaidContractDTO>(prepaidContractService.addPrepaidContractSuppliers(suppliers, id), HttpStatus.OK);
    }

    /**
     * 
     * @param id
     * @param suppliers
     * @return
     */
    @DeleteMapping(path= "/{id}/suppliers")
    public ResponseEntity<PrepaidContractDTO> deleteSuppliersFromContract(@PathVariable String id, @RequestBody List<PrepaidContractSupplierDTO> suppliers) {
        return new ResponseEntity<PrepaidContractDTO>(prepaidContractService.deletePrepaidContractSuppliers(suppliers, id), HttpStatus.OK);
    }
}
