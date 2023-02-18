package com.ghx.api.operations.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.ghx.api.operations.dto.RoleDTO;
import com.ghx.api.operations.dto.UserSecurityDTO;
import com.ghx.api.operations.service.UserSecurityService;
import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.common.log.GHXLogger;
import com.ghx.common.log.GHXLoggerFactory;
import com.ghx.ngcommons.security.TokenEnhancer;
import com.ghx.ngcommons.security.model.CustomAuthenticationToken;
import com.ghx.ngcommons.security.model.Principal;

/**
 * @author Loganathan.M
 * Class is use to enhance token from security api based on application roles.
 *
 */
@Component
public class TokenEnhancerImpl implements TokenEnhancer {

    private static final GHXLogger LOGGER = GHXLoggerFactory.getLogger(TokenEnhancerImpl.class);

    @Value("${security.token.filter.role-prefix:ROLE_}")
    public Optional<String> rolePrefix;

    @Autowired
    private UserSecurityService userSecurityService;

    @Override
    public Optional<CustomAuthenticationToken> enhanceToken(CustomAuthenticationToken authToken, ServletRequest request, ServletResponse response) {
        Principal principal = (Principal) authToken.getPrincipal();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String subIdentifier = httpRequest.getHeader(ConstantUtils.SUB_IDENTIFIER);
        UserSecurityDTO userSecurityDTO = null;
        if (StringUtils.isNotEmpty(principal.getEmail())) {
            userSecurityDTO = userSecurityService.getUserByUserIdAndStatus(principal.getEmail(), ConstantUtils.USER_STATUS_ACTIVE);
        } else {
            userSecurityDTO = userSecurityService.getUserByUserIdAndStatus(principal.getUsername(), ConstantUtils.USER_STATUS_ACTIVE);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (Objects.nonNull(userSecurityDTO)) {
            userSecurityDTO.getRoles().forEach((roleDTO) -> {
                LOGGER.info("role type--->" + roleDTO.getType());
                setGrantedAuthorities(roleDTO, authorities);
            });
        } else if (ConstantUtils.EXTERNAL_USER.equalsIgnoreCase(subIdentifier)) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setRoleName(ConstantUtils.ROLE_EXTERNAL_CLIENT);
            setGrantedAuthorities(roleDTO, authorities);
        } else if (ConstantUtils.VM_SYSTEM_USER.equalsIgnoreCase(principal.getUsername())) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setRoleName(ConstantUtils.ROLE_SYSTEM_USER);
            setGrantedAuthorities(roleDTO, authorities);
        }
        CustomAuthenticationToken customToken = new CustomAuthenticationToken(authorities, principal);
        return Optional.of(customToken);
    }

    private void setGrantedAuthorities(RoleDTO roleDTO, List<GrantedAuthority> authorities) {
        // added the condition to get roleName
		String roleName = StringUtils.equalsAnyIgnoreCase(roleDTO.getRoleName(), ConstantUtils.ROLE_EXTERNAL_CLIENT,
				ConstantUtils.ROLE_SYSTEM_USER) ? ConstantUtils.ROLE_PREFIX + roleDTO.getRoleName()
						: rolePrefix.orElse("") + roleDTO.getType();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleName);
        authorities.add(grantedAuthority);
    }
}
