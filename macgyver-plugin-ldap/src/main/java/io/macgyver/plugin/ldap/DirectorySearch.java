package io.macgyver.plugin.ldap;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;

public interface DirectorySearch {

	JsonNode getObjectByDn(String dn);
	
	List<JsonNode> search(String filter);
	
	JsonNode getManager(String dn);
	
	List<JsonNode> getMemberOfGroups(String dn);
}
