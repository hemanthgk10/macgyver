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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.lambdaworks.crypto.SCrypt;
import com.lambdaworks.crypto.SCryptUtil;

import io.macgyver.core.util.JsonNodes;
import io.macgyver.neorx.rest.NeoRxClient;

@Controller
@PreAuthorize("hasAnyRole('ROLE_MACGYVER_USER', 'ROLE_MACGYVER_ADMIN')")
@RequestMapping("/api/core/token")
public class ApiTokenController {

	@Inject
	NeoRxClient neo4j;
	
	@Inject
	ApiTokenAuthenticationProvider provider;
	

	ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/create", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String create(HttpServletRequest request) { 
		
		String plainToken = provider.createToken();
		
		ApiToken apiToken = ApiToken.parse(plainToken);
		
		
		String hashedSecretKey = SCryptUtil.scrypt(apiToken.getSecretKey(), 4096,8,1);
		
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		
		String expiresInString = request.getParameter("expires_in");
		
		long expiration = LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
		if (!Strings.isNullOrEmpty(expiresInString)) {
			long expiresIn = Long.parseLong(expiresInString);
			expiration = LocalDateTime.now().plusSeconds(expiresIn).atZone(ZoneId.of("UTC")).toInstant().getEpochSecond();
		}
		
		
		ArrayNode roles = mapper.createArrayNode();
		if (authorities!=null) {
			authorities.forEach(it -> {
				roles.add(it.getAuthority());
			});
		}
		ObjectNode attrs = mapper.createObjectNode();
		attrs.set("roles", roles);
		String cypher = "merge (a:ApiToken {accessKey:{accessKey}})  set a.secretKeyHash={secretKeyHash}, a.createTs=timestamp(), a.username={username},a.expirationTs={expiration}, a+={attrs}  return a";
		JsonNode n = neo4j.execCypher(cypher, "accessKey",apiToken.getAccessKey(),"username",username,"attrs",attrs,"expiration",expiration,"secretKeyHash",hashedSecretKey).toBlocking().first();
		
		ObjectNode on = (ObjectNode) n;
		on.put("token", plainToken);
		on.remove("secretKeyHash");
		on.remove("accessKey");
		return JsonNodes.pretty(n);
	}
	
	@RequestMapping(value = "/validate", method = {RequestMethod.GET})
	@ResponseBody
	public String validate(HttpServletRequest request) { 
		return SecurityContextHolder.getContext().getAuthentication().toString();
	}
	
	@RequestMapping(value = "/delete", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonNode delete(HttpServletRequest request) { 
		
		String token = request.getParameter("token");
		String cypher = "match (a:ApiToken {accessKey:{accessKey}}) delete a";
		
		// there is a deficiency here where we really only want to be able to delete our own token.  Fix this later.
		neo4j.execCypher(cypher, "accessKey", ApiToken.parse(token).getAccessKey());
		
		ObjectNode n = mapper.createObjectNode().put("status", "deleted");
		
		return n;
	
	}
}
