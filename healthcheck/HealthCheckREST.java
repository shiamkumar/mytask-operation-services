package com.ghx.api.operations.healthcheck;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Endpoint For The Vision Services
 * 
 * The URL would be
 * <ul>
 * <li>AWS where the context root is removed - http://reference-api.ghx.com/ping
 * <li>Local - http://localhost:8080/ping
 * </ul>
 *
 */
@RestController
public class HealthCheckREST {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(String.format("I'm up!: %s", new Date()));
    }
}