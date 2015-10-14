package io.macgyver.plugin.cmdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;

@Controller
@PreAuthorize("hasAnyRole('ROLE_MACGYVER_USER','ROLE_MACGYVER_ADMIN')")
@RequestMapping("/plugin/cmdb")
public class AppInstanceController {

	@Autowired
	NeoRxClient neo4j;
	
	@RequestMapping("/app-instances")
	@ResponseBody
	public ModelAndView lteHome() {

		List<JsonNode> results = neo4j.execCypherAsList("match (m:AppInstance) return m order by m.environment, m.host ");
		

		
		return new ModelAndView("/plugin/cmdb/app-instances","results",results);

	}

}