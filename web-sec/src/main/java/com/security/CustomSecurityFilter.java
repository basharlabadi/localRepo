package com.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;


/**
 * User: Joseph Vartuli
 * Date: 25/08/12
 *
 * This is the class you should use to extract and manipulate the header data
 * to pass to the authentication manager to authenticate.
 *
 */
public class CustomSecurityFilter extends GenericFilterBean {
    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public CustomSecurityFilter(AuthenticationManager authenticationManager) {
        this(authenticationManager, new BasicAuthenticationEntryPoint());
    }

    public CustomSecurityFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //Pull out the Authorization header
        String authorization = request.getHeader("x-tcss-auth");
        
        //If the Authorization header is null, let the ExceptionTranslationFilter provided by
        //the <http> namespace kick of the BasicAuthenticationEntryPoint to provide the username and password dialog box
        if (authorization == null) {
            chain.doFilter(request, response);
            return;
        }

        String[] credentials = decodeHeader(authorization);
        assert credentials.length == 2;
        Authentication authentication = new AuthToken(credentials[0], credentials[1]);

        try {
            //Request the authentication manager to authenticate the token
            Authentication successfulAuthentication = authenticationManager.authenticate(authentication);
            //Pass the successful token to the SecurityHolder where it can be retrieved by this thread at any stage.
            SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
            //Continue with the Filters
            chain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            //If it fails clear this threads context and kick off the authentication entry point process.
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, authenticationException);
        }
    }

    public String[] decodeHeader(String authorization) {
		String credentials = new String(authorization);
		return credentials.split(":");
    }

}
