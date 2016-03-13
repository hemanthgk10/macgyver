package io.macgyver.core.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.neorx.rest.NeoRxClient;

@Controller
public class TaskController {

	
	@Autowired
	NeoRxClient neo4j;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@PreAuthorize("hasAnyRole('ROLE_MACGYVER_USER', 'ROLE_MACGYVER_ADMIN')")
	@RequestMapping("/api/core/tasks/active")
	@ResponseBody
	public JsonNode apiActiveTasks() {
	
		ObjectNode result = mapper.createObjectNode();
		ArrayNode arr = mapper.createArrayNode();
		neo4j.execCypher("match (t:TaskState) where t.state='STARTED' return t order by t.startTs").forEach(it-> {
			arr.add(it);
		});
		result.set("results", arr);
		
		return result;
	}
	

}
