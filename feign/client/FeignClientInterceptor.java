package com.ghx.api.operations.feign.client;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ghx.api.operations.util.ConstantUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        requestTemplate.header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        if (StringUtils.isNoneBlank(request.getHeader(ConstantUtils.SUB_IDENTIFIER))) {
            requestTemplate.header(ConstantUtils.SUB_IDENTIFIER, request.getHeader(ConstantUtils.SUB_IDENTIFIER));
        }
    }
}
