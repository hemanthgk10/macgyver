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

public class DatabaseDefinitionController {

	Logger logger = LoggerFactory.getLogger(DatabaseDefinitionController.class);
	ObjectMapper mapepr = new ObjectMapper();

	@Inject
	NeoRxClient neo4j;

	@RequestMapping(value = "/api/cmdb/database-definitions", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> create(HttpServletRequest request) {

		ObjectNode response = mapepr.createObjectNode();

		ArrayNode arr = mapepr.createArrayNode();
		neo4j.execCypher("match (a:DatabaseDefinition) return a").forEach(it -> {
			arr.add(it);
		});
		response.set("results", arr);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/api/cmdb/database-definitions/{id}", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> create(HttpServletRequest request, @PathVariable("id") String jobId) {

		try {
			return ResponseEntity
					.ok(neo4j.execCypher("match (a:DatabaseDefinition {id:{id}}) return a", "id", jobId)
							.toBlocking().first());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).body(mapepr.createObjectNode().put("status", 404).put("message", String.format("job not found: %s",jobId)));
		}
	}

	@RequestMapping(value = "/cmdb/database-definitions", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request) {

	
		
		List<JsonNode> list = neo4j.execCypher("match (j:DatabaseDefinition) return j").toList().toBlocking().first();
		
		
		
		ModelAndView m = new ModelAndView("/cmdb/database-definitions").addObject("results",list);
		return m;
	}
	@RequestMapping(value = "/cmdb/database-definitions/{id}", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request, @PathVariable("id") String id) {

		logger.info("loading job def: {}",id);
		JsonNode service = neo4j.execCypher("match (j:DatabaseDefinition {id:{id}}) return j", "id", id)
		.toBlocking().first();
		
		
		ModelAndView m = new ModelAndView("/cmdb/database-definition").addObject("database",service);
		return m;
	}
}
