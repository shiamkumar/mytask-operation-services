package com.ghx.api.operations.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This class DocumentServiceClient used to connect Document APIs
 * @author Kanakaraj
 *
 */
@FeignClient(name = "documentApi", url = "${credentialingApi.baseUrl}")
public interface DocumentServiceClient {
    /**
     * 
     * @param id
     * @param type
     */
    @DeleteMapping("/rrpdef/{id}/remove")
    void removeRrpDef(@PathVariable String id, @RequestParam String type);
}
