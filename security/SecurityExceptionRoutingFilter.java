package com.ghx.api.operations.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.ghx.api.operations.exception.CustomExceptionHandler;
import com.ghx.ngcommons.security.Exception.SecurityException;

/**
 * @author Rajasekar Jayakumar
 * 
 * Catches and routes {@link SecurityException} to {@link HandlerExceptionResolver}, which delegates
 * to {@link CustomExceptionHandler}
 */
@Component
public class SecurityExceptionRoutingFilter extends GenericFilterBean {

    @Autowired
    private HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (SecurityException e) {
            resolver.resolveException(httpRequest, httpResponse, null, e);
        }
    }

}
