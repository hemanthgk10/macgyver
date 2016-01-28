package io.macgyver.core.auth;

import java.security.SecureRandom;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.io.BaseEncoding;

public class ApiTokenAuthenticationFilterTest {


	@Test
	public void testIt() {
		MockHttpServletRequest request  = new MockHttpServletRequest();
		ApiToken apiToken = ApiToken.createRandom();
		
		request.addHeader("Authorization", "Token "+apiToken.getArmoredString());
		
		ApiTokenAuthenticationFilter filter = new ApiTokenAuthenticationFilter();
		Assertions.assertThat(filter.extractToken(request).get().getCredentials()).isEqualTo(apiToken.getArmoredString());
		Assertions.assertThat(filter.extractToken(request).get().isAuthenticated()).isFalse();
		
	}
	
	
	@Test
	public void testBasicAuth() {
		MockHttpServletRequest request  = new MockHttpServletRequest();
		request.addHeader("Authorization", "Basic foobar");
		
		ApiTokenAuthenticationFilter filter = new ApiTokenAuthenticationFilter();
		Assertions.assertThat(filter.extractToken(request).isPresent()).isFalse();
		
	}
	
	@Test
	public void testAbsentHeader() {
		MockHttpServletRequest request  = new MockHttpServletRequest();
		
		ApiTokenAuthenticationFilter filter = new ApiTokenAuthenticationFilter();
		Assertions.assertThat(filter.extractToken(request).isPresent()).isFalse();
	
		
	}
}
