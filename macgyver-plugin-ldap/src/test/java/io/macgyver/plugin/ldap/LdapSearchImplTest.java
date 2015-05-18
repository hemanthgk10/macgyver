package io.macgyver.plugin.ldap;

import io.macgyver.plugin.ldap.JsonAttributesMapper;

import java.io.IOException;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LdapSearchImplTest extends AbstractLdapTest {

	@Test
	public void testIt() throws IOException {
		LdapTemplate lt = ldapServer.getLdapTemplate();

	/*	List<JsonNode> x = lt.search("", "(objectClass=*)",
				SearchControls.SUBTREE_SCOPE, new JsonAttributesMapper());

		System.out.println(x.size());
		*/
		ActiveDirectorySearch s = new ActiveDirectorySearch(lt);


		for (JsonNode n : s.search("(objectClass=groupOfNames)")) {

			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(n));
		
		}

	}
}
