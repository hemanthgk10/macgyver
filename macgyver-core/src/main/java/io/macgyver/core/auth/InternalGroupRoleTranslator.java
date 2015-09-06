package io.macgyver.core.auth;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.neorx.rest.NeoRxFunctions;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * This class will map granted authorities from an upstream source (probably
 * LDAD/AD) and map them onto MacGyver-specific roles.
 * 
 * @author rschoening
 *
 */
public class InternalGroupRoleTranslator extends GrantedAuthoritiesTranslator {

	Logger log = LoggerFactory.getLogger(InternalGroupRoleTranslator.class);
	@Autowired
	NeoRxClient neo4j;

	@SuppressWarnings("unchecked")
	@Override
	protected void translate(Collection<? extends GrantedAuthority> source,
			Collection<? extends GrantedAuthority> target) {

		source.stream().forEach(sourceAuthority -> {
			
			mapSourceToTarget(sourceAuthority, target);
			((Collection<GrantedAuthority>) target).add(new SimpleGrantedAuthority("GROUP_"+normalizeUpperCase(sourceAuthority.getAuthority())));
		});

	}

	@SuppressWarnings("unchecked")
	protected void mapSourceToTarget(GrantedAuthority sourceAuthority,
			Collection<? extends GrantedAuthority> target) {

		String sourceName = sourceAuthority.getAuthority();

	
		neo4j.execCypher(
				"match (g:Group{name:{name}})-[:HAS_ROLE]-(r:Role) return distinct r.name as role_name",
				"name", sourceName).flatMap(NeoRxFunctions.jsonNodeToString())
				.forEach(roleName -> {
					
					log.info("found role_name: {}",roleName);
					GrantedAuthority sg = new SimpleGrantedAuthority(roleName);

					((Collection<GrantedAuthority>) target).add(sg);
				});
		
	}
	
	public static String normalizeUpperCase(String s) {
		return s.replaceAll("[^A-Za-z0-9]", "_").trim().toUpperCase();
	}
}
