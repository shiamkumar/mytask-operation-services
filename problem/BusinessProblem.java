package com.ghx.api.operations.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * @author Rajasekar Jayakumar
 * 
 * Helps to convert BusinessException to customised Json response, via Zalando "problem-spring-web" library
 */
public class BusinessProblem extends AbstractThrowableProblem{
    private static final long serialVersionUID = 1L;

    /**
     * constructor for BusinessProblem
     * @param type
     * @param errorMessage
     * @param httpStatus
     */
    public BusinessProblem(URI type, String errorMessage, Status httpStatus) {
        super(type, Status.BAD_REQUEST.name(), Objects.nonNull(httpStatus) ? httpStatus : Status.BAD_REQUEST, null, null, null,
                getAlertParameters(errorMessage));
    }
    
    private static Map<String, Object> getAlertParameters(String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("detail", errorKey);
        return parameters;
    }
}
