package com.ghx.api.operations.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ghx.ngcommons.security.model.CustomAuthenticationToken;
import com.ghx.ngcommons.security.model.Principal;

/**
 * @author Krishnan M
 *         Common Utils class contains user related details and authorities from Auth Token.
 * 
 */

@Component
public class SecurityUtils {

    /**
     * Get auth token
     * @return
     */
    private static Authentication getAuthenticationToken() {
        Authentication auth = (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth;
    }

    /**
     * get authorities
     * @return
     */
    public List<String> getAuthorities() {
        List<String> authorities = getAuthenticationToken().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return authorities;
    }

    /**
     * Get User id from Token
     * @return
     */
    public String getCurrentUser() {
        Authentication auth = getAuthenticationToken();
        return StringUtils.isEmpty(((Principal) auth.getPrincipal()).getEmail()) ? ((Principal) auth.getPrincipal()).getUsername()
                : ((Principal) auth.getPrincipal()).getEmail();
    }
}
