package com.ghx.api.operations.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.ghx.api.operations.util.ConstantUtils;
import com.ghx.ngcommons.security.filter.TokenAuthenticationFilter;

/**
 * 
 * @author Loganathan.M
 *
 */

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	TokenAuthenticationFilter tokenAuthenticationFilter;

	@Autowired
	SecurityExceptionRoutingFilter exceptionHandlerFilter;

	@Autowired
	private SecurityProblemSupport problemSupport;
	/**
	 * security configuration method
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.requestMatchers()
            .antMatchers("/**/pricing/**", "/**/tickets/**", "/**/lookups/**", "/**/suppliers/**","/**/users/**","/**/idns/**/importreps","/**/migration/request/**","/**/audittrail/**","/**/documents/**").and().exceptionHandling().authenticationEntryPoint(problemSupport)
                                .accessDeniedHandler(problemSupport).and().csrf().disable().sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                                .addFilterBefore(tokenAuthenticationFilter, SecurityContextHolderAwareRequestFilter.class)
                                .addFilterBefore(exceptionHandlerFilter, TokenAuthenticationFilter.class).authorizeRequests()
                                .antMatchers("/**/lookups").hasAnyRole(ConstantUtils.ROLE_EXTERNAL_CLIENT, ConstantUtils.ROLE_VMRM, ConstantUtils.ROLE_VREP)
                                .antMatchers("/**/pricing/configurations/**").hasAnyRole(ConstantUtils.ROLE_VMRM, ConstantUtils.ROLE_VREP, ConstantUtils.ROLE_SYSTEM_USER)
                                .antMatchers("/**/pricing/prepaidcontracts/**").hasAnyRole(ConstantUtils.ROLE_VMRM,ConstantUtils.ROLE_SYSTEM_USER)
                                .antMatchers("/**/suppliers/mergerequest/**").hasAnyRole(ConstantUtils.ROLE_VMRM)
                                .antMatchers("/**/suppliers/migration/statistics/**").hasAnyRole(ConstantUtils.ROLE_VMRM,ConstantUtils.ROLE_SYSTEM_USER)
                                .antMatchers(HttpMethod.POST, "/**/suppliers/tierchangerequest/**").hasAnyRole(ConstantUtils.ROLE_VREP)
                                .antMatchers("/**/suppliers/tierchangerequest/**").hasAnyRole(ConstantUtils.ROLE_VMRM, ConstantUtils.ROLE_VREP, ConstantUtils.ROLE_SYSTEM_USER)
                                .antMatchers("/**/users/deleterequest").hasAnyRole(ConstantUtils.ROLE_VMRM)
                                .antMatchers("/**/users/**").hasAnyRole(ConstantUtils.ROLE_VMRM, ConstantUtils.ROLE_VREP)
                                .antMatchers("/**/idns/**/importreps").hasAnyRole(ConstantUtils.ROLE_VMRM, ConstantUtils.ROLE_VREP)
                                .antMatchers("/**/tickets/**").hasAnyRole(ConstantUtils.ROLE_EXTERNAL_CLIENT,ConstantUtils.ROLE_VMRM,ConstantUtils.ROLE_VREP,ConstantUtils.ROLE_SYSTEM_USER)
                                .antMatchers("/**/migration/request/**").hasAnyRole(ConstantUtils.ROLE_VMRM)
                                .antMatchers("/**/audittrail/**").hasAnyRole(ConstantUtils.ROLE_VMRM)
                                .antMatchers("/**/documents/actionrequest/**").hasAnyRole(ConstantUtils.ROLE_VMRM)
                                .and().authorizeRequests().anyRequest()
                                .authenticated();
	}

	@Bean
	protected FilterRegistrationBean<TokenAuthenticationFilter> tokenAuthFilterRegistration() {
		FilterRegistrationBean<TokenAuthenticationFilter> frb = new FilterRegistrationBean<>();
		frb.setFilter(tokenAuthenticationFilter);
		frb.setEnabled(false);
		return frb;
	}

	@Bean
	protected FilterRegistrationBean<SecurityExceptionRoutingFilter> exceptionHandlerFilterRegistration() {
		FilterRegistrationBean<SecurityExceptionRoutingFilter> frb = new FilterRegistrationBean<>();
		frb.setFilter(exceptionHandlerFilter);
		frb.setEnabled(false);
		return frb;
	}
}
