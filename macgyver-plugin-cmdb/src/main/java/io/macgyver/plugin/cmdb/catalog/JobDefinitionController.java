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
package io.macgyver.plugin.cmdb.catalog;

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

public class JobDefinitionController {

	Logger logger = LoggerFactory.getLogger(JobDefinitionController.class);
	ObjectMapper mapepr = new ObjectMapper();

	@Inject
	NeoRxClient neo4j;

	@RequestMapping(value = "/api/cmdb/job-definitions", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> create(HttpServletRequest request) {

		ObjectNode response = mapepr.createObjectNode();

		ArrayNode arr = mapepr.createArrayNode();
		neo4j.execCypher("match (a:JobDefinition) return a").forEach(it -> {
			arr.add(it);
		});
		response.set("results", arr);
		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/api/cmdb/job-definitions/{id}", method = { RequestMethod.GET })
	public ResponseEntity<JsonNode> create(HttpServletRequest request, @PathVariable("id") String jobId) {

		try {
			return ResponseEntity
					.ok(neo4j.execCypher("match (a:JobDefinition {id:{id}}) return a", "id", jobId)
							.toBlocking().first());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).body(mapepr.createObjectNode().put("status", 404).put("message", String.format("job not found: %s",jobId)));
		}
	}

	@RequestMapping(value = "/cmdb/job-definitions", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request) {

	
		
		List<JsonNode> list = neo4j.execCypher("match (j:JobDefinition) return j").toList().toBlocking().first();
		
		
		
		ModelAndView m = new ModelAndView("/cmdb/job-definitions").addObject("results",list);
		return m;
	}
	@RequestMapping(value = "/cmdb/job-definitions/{id}", method = { RequestMethod.GET })
	public ModelAndView apps(HttpServletRequest request, @PathVariable("id") String id) {

		logger.info("loading job def: {}",id);
		JsonNode service = neo4j.execCypher("match (j:JobDefinition {id:{id}}) return j", "id", id)
		.toBlocking().first();
		
		
		ModelAndView m = new ModelAndView("/cmdb/job-definition").addObject("job",service);
		return m;
	}
}
