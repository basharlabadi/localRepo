package com.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;



public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		AuthToken authToken = (AuthToken) authentication;

        String key = authToken.getKey();
        String credentials = authToken.getCredentials();
        if (!credentials.equals(key + "123")) 
        {
            throw new BadCredentialsException("Enter a username and password");
        }
        return getAuthenticatedUser(key, credentials);
	}

	private Authentication getAuthenticatedUser(String key, String credentials) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (!key.equals("admin")) {
        	authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
        	authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        	authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return new AuthToken(key, credentials, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AuthToken.class.equals(authentication);
	}

}
