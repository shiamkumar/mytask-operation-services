package com.ghx.api.operations.exception;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import com.ghx.api.operations.problem.BusinessProblem;
import com.ghx.api.operations.problem.ExceptionProblem;
import com.ghx.api.operations.problem.ResourceNotFoundProblem;
import com.ghx.api.operations.problem.SecurityProblem;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;

/**
 * @author Rajasekar Jayakumar
 * 
 *         Handles exception across all @RequestMapping methods through @ExceptionHandler
 *         ControllerAdvice is used to declare and share annotations like @ExceptionHandler across controllers
 * 
 */
@ControllerAdvice
@ResponseBody
public class CustomExceptionHandler implements ProblemHandling, SecurityAdviceTrait {
	
	private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(CustomExceptionHandler.class);
	
	private static final String MESSAGE = "message";

    /**
     * handle buisness exception
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Problem> handleBusinessException(BusinessException ex, NativeWebRequest request) {
        BusinessProblem businessProblem = new BusinessProblem(
                URI.create(request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString()), ex.getMessage(), null);
        LOGGER.error(MESSAGE, ex);
        return create(businessProblem, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleSystemException(Exception ex, NativeWebRequest request) {
        ExceptionProblem exceptionProblem = new ExceptionProblem(
                URI.create(request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString()), ex.getMessage());
        LOGGER.error(MESSAGE, ex);
        return create(exceptionProblem, request);
    }

    @ExceptionHandler(com.ghx.ngcommons.security.Exception.SecurityException.class)
    public ResponseEntity<Problem> handleSecurityException(com.ghx.ngcommons.security.Exception.SecurityException ex, NativeWebRequest request) {
        SecurityProblem securityProblem = new SecurityProblem(
                URI.create(request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString()), ex.getMessage());
        LOGGER.error(MESSAGE, ex);
        return create(securityProblem, request);
    }
    
    /**
     * handle system exception
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Problem> handleSystemException(BusinessException ex, NativeWebRequest request) {
        BusinessProblem businessProblem = new BusinessProblem(
                URI.create(request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString()), ex.getMessage(),
                Status.INTERNAL_SERVER_ERROR);
        LOGGER.error(MESSAGE, ex);
        return create(businessProblem, request);
    }
    
    /**
     * handler resource not found exception
     * @param ex
     * @param request
     * @return
     */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Problem> handleResourceNotFoundException(ResourceNotFoundException ex, NativeWebRequest request) {
		ResourceNotFoundProblem businessProblem = new ResourceNotFoundProblem(
				URI.create(request.getNativeRequest(HttpServletRequest.class).getRequestURL().toString()),
				ex.getMessage(), Status.NOT_FOUND);
		LOGGER.error(MESSAGE, ex);
		return create(businessProblem, request);
	}
    
}
