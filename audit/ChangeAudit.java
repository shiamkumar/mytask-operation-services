package com.ghx.api.operations.audit;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ghx.ngcommons.security.model.CustomAuthenticationToken;
import com.ghx.ngcommons.security.model.Principal;


/**
 * A component that is aware of current use details, to populate audit informations like,
 * createdBy, createdDate, lastModifiedBy, lastModifiedDate
 *
 */
@Component
public class ChangeAudit implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Principal principal = (Principal) ((CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return Optional.of(StringUtils.isEmpty(principal.getEmail()) ? principal.getUsername() : principal.getEmail());
    }

}
