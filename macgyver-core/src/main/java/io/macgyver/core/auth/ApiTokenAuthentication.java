package io.macgyver.core.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class ApiTokenAuthentication extends AbstractAuthenticationToken {

	String token;
	public ApiTokenAuthentication(String token) {
		super(null);
		setAuthenticated(false);
		this.token = token;
		
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

}
