/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.core.auth;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

import com.lambdaworks.crypto.SCrypt;
import com.lambdaworks.crypto.SCryptUtil;

import io.macgyver.neorx.rest.NeoRxClient;
import joptsimple.internal.Strings;
import rx.exceptions.OnErrorNotImplementedException;

public class ApiTokenAuthenticationProvider implements AuthenticationProvider {

	@Inject
	NeoRxClient neo4j;

	Logger logger = LoggerFactory.getLogger(ApiTokenAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {

		try {
			return validateToken(Objects.toString(auth.getCredentials(), null)).orElse(null);
		
		}
		catch (RuntimeException e) {
			throw new AuthenticationCredentialsNotFoundException("could not validate token",e);
		}

	}

	@Override
	public boolean supports(Class<?> auth) {
		return ApiTokenAuthentication.class.isAssignableFrom(auth);
	}

	Optional<Authentication> validateToken(String token) {
		if (Strings.isNullOrEmpty(token)) {
			return Optional.empty();
		}
		AtomicReference<Authentication> auth = new AtomicReference<>(null);
		
		try {
			ApiToken apiToken = ApiToken.parse(token);
		
			
			
			String cypher = "match (a:ApiToken {accessKey:{accessKey}}) return a";

		
		
			neo4j.execCypher(cypher, "accessKey", apiToken.getAccessKey()).first().forEach(it -> {
			
				if (SCryptUtil.check(apiToken.getSecretKey(),it.path("secretKeyHash").asText())) {
					
				
				
					
				String username = it.path("username").asText();
				
					List<GrantedAuthority> x = Lists.newArrayList();
					it.path("roles").elements().forEachRemaining(r->{
						SimpleGrantedAuthority sga = new SimpleGrantedAuthority(r.asText());
						x.add(sga);
					});
					
					UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(
							username,null,x);
					auth.set(authenticatedUser);
				}
				else {
					logger.info("token could not be authenticated");
				}

			});
		} catch (NoSuchElementException|OnErrorNotImplementedException e) {
			logger.info("invalid token",e);
		} 
		return Optional.ofNullable(auth.get());
	}

	
	
	protected String createToken() {
		
		return ApiToken.createRandom().getArmoredString();
		
	}
}
