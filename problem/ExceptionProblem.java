package com.ghx.api.operations.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * @author Rajasekar Jayakumar
 * 
 *         Helps to convert {@link Exception} to customised Json response, via Zalando "problem-spring-web" library
 */
public class ExceptionProblem extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public ExceptionProblem(URI type, String errorMessage) {
        super(type, Status.INTERNAL_SERVER_ERROR.name(), Status.INTERNAL_SERVER_ERROR, null, null, null, getAlertParameters(errorMessage));
    }

    private static Map<String, Object> getAlertParameters(String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("detail", errorKey);
        return parameters;
    }
}
