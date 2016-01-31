package io.macgyver.plugin.cmdb;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.neorx.rest.NeoRxClient;

@Controller
@PreAuthorize("hasAnyRole('ROLE_MACGYVER_USER', 'ROLE_MACGYVER_ADMIN')")
public class AppDefinitionController {

	Logger logger = LoggerFactory.getLogger(AppDefinitionController.class);
	ObjectMapper mapepr = new ObjectMapper();

	@Inject
	NeoRxClient neo4j;

	@RequestMapping(value = "/api/cmdb/app-definitions", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> apiServices(HttpServletRequest request) {

		ObjectNode response = mapepr.createObjectNode();

		ArrayNode arr = mapepr.createArrayNode();
		neo4j.execCypher("match (a:AppDefinition) return a").forEach(it -> {
			arr.add(it);
		});
		response.set("results", arr);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/api/cmdb/app-definitions/{id}", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> apiSingleApp(HttpServletRequest request, @PathVariable("id") String id) {

		try {
			return ResponseEntity
					.ok(neo4j.execCypher("match (a:AppDefinition {appId:{appId}}) return a order by a.appId", "appId", id)
							.toBlocking().first());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).body(mapepr.createObjectNode().put("status", 404).put("message", String.format("not found: %s",id)));
		}
	}

	@RequestMapping(value = "/cmdb/app-definitions", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request) {

		
		List<JsonNode> results = neo4j.execCypher("match (a:AppDefinition) return a order by a.appId").toList().toBlocking().first();
		
		
		ModelAndView m = new ModelAndView("/cmdb/app-definitions").addObject("results",results);
		return m;
	}
	
	@RequestMapping(value = "/cmdb/app-definitions/{appId}", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request, @PathVariable("appId") String appId) {

		logger.info("loading app def: {}",appId);
		JsonNode service = neo4j.execCypher("match (a:AppDefinition {appId:{appId}}) return a", "appId", appId)
		.toBlocking().first();
		
		
		ModelAndView m = new ModelAndView("/cmdb/app-definition").addObject("app",service);
		return m;
	}
}
