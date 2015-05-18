package io.macgyver.plugin.ldap;

import java.util.List;

import org.springframework.ldap.core.LdapTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class ActiveDirectorySearch extends AbstractDirectorySearch {
	public ActiveDirectorySearch(LdapTemplate t) {
		super(t);
	}

	@Override
	public List<JsonNode> getMemberOfGroups(String dn) {
		return search("(member="+dn+")");
	}

}
