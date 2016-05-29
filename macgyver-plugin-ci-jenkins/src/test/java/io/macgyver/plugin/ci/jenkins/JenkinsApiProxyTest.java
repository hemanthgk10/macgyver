package io.macgyver.plugin.ci.jenkins;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import io.macgyver.plugin.ci.jenkins.JenkinsApiProxy.AuthorizationFunction;

public class JenkinsApiProxyTest {

	
	@Test
	public void testIt() {
		
		JenkinsApiProxy proxy = new JenkinsApiProxy() {

			@Override
			protected String lookupTargetUrlForService(String serviceName) {
				return "https://jenkins.example.com/foo";
			}
			
		};
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/jenkins/proxy/foo/bar");

		Assertions.assertThat(proxy.toProxyUrl("foo", request)).isEqualTo("https://jenkins.example.com/foo/bar");
		
		
	}
	
	
	@Test
	public void testAuthorization() {
		JenkinsApiProxy proxy = new JenkinsApiProxy();
		
		
		Assertions.assertThat(proxy.authorize(new MockHttpServletRequest())).isFalse();
		
		AuthorizationFunction f = new AuthorizationFunction() {
			
			@Override
			public Optional<Boolean> apply(HttpServletRequest t) {
				return Optional.empty();
			}
		};

		proxy.addAuthorizationFunction(f);
		
		Assertions.assertThat(proxy.authorize(new MockHttpServletRequest())).isFalse();
		
		 f = new AuthorizationFunction() {
				
				@Override
				public Optional<Boolean> apply(HttpServletRequest t) {
					return Optional.of(true);
				}
			};
			
		proxy.addAuthorizationFunction(f);
		
		Assertions.assertThat(proxy.authorize(new MockHttpServletRequest())).isTrue();
		
		f = new AuthorizationFunction() {
			
			@Override
			public Optional<Boolean> apply(HttpServletRequest t) {
				return Optional.of(false);
			}
		};
		proxy.addAuthorizationFunction(f);
		Assertions.assertThat(proxy.authorize(new MockHttpServletRequest())).isFalse();
		
	}
}
