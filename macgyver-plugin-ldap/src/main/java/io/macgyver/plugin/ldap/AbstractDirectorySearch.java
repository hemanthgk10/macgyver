package io.macgyver.plugin.ldap;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public abstract class AbstractDirectorySearch implements DirectorySearch {

	final LdapTemplate template;
	final ObjectMapper MAPPER = new ObjectMapper();
	String base = "";

	JsonAttributesMapper attributesMapper = new JsonAttributesMapper();

	public AbstractDirectorySearch(LdapTemplate t) {
		this.template = t;
	}

	@Override
	public JsonNode getObjectByDn(String dn) {

		try {
			Object object = template.lookup(dn);

			DirContextAdapter a = (DirContextAdapter) object;

			return (JsonNode) attributesMapper.mapFromAttributes(a
					.getAttributes());
		} catch (NamingException e) {
			throw LdapUtils.convertLdapException(e);
		}

	}

	@Override
	public List<JsonNode> search(String filter) {
		return template.search(base, filter, SearchControls.SUBTREE_SCOPE,
				attributesMapper);

	}

	

	@Override
	public JsonNode getManager(String dn) {
		JsonNode n = getObjectByDn(dn);
		
		String manager = n.path("manager").asText();
		if (Strings.isNullOrEmpty(manager)) {
			throw new NameNotFoundException("manager not found");
		}
		JsonNode mn = getObjectByDn(manager);
		return mn;
	}


	


}
