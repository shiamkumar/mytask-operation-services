package com.ghx.api.operations.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import com.ghx.ngcommons.security.Exception.SecurityException;

/**
 * @author Rajasekar Jayakumar
 * 
 * Helps to convert {@link SecurityException} to customised Json response, via Zalando "problem-spring-web" library
 */
public class SecurityProblem extends AbstractThrowableProblem{
    private static final long serialVersionUID = 1L;

    public SecurityProblem(URI type, String errorMessage) {
        super(type, Status.UNAUTHORIZED.name(), Status.UNAUTHORIZED, null, null, null, getAlertParameters(errorMessage));
    }
    
    private static Map<String, Object> getAlertParameters(String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("detail", errorKey);
        return parameters;
    }
}
