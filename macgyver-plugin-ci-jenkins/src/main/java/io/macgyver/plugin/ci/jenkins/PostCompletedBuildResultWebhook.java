package io.macgyver.plugin.ci.jenkins;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProviderProxy;

/**
 * This is a webhook for the Post Completed Build Results plugin.
 * 
 * https://wiki.jenkins-ci.org/display/JENKINS/Post+Completed+Build+Result+Plugin
 * 
 * @author rschoening
 *
 */
@Controller
@RequestMapping("/api/plugin/ci/jenkins")
public class PostCompletedBuildResultWebhook {

	@Autowired
	DistributedEventProviderProxy devent;
	
	Logger logger = LoggerFactory.getLogger(PostCompletedBuildResultWebhook.class);
	ObjectMapper mapper = new ObjectMapper();
	
	public PostCompletedBuildResultWebhook() {
	
	}

	
	
	@RequestMapping(value="/post-completed-build-result",method={
			RequestMethod.POST},produces="application/json")
	@PreAuthorize("permitAll")
	public  ResponseEntity<JsonNode> postBuildCompleted(HttpServletRequest request) {
		
		String url = request.getParameter("url");
		logger.info("url: "+url);
		
		ObjectNode payload = mapper.createObjectNode().put("url", url);
		DistributedEvent evt = DistributedEvent.create().topic("ci.jenkins.post-build-completed").payload(payload);
		devent.publish(evt);
		// we can call back to <url>/api/json to get actual information about the build
		return ResponseEntity.ok(mapper.createObjectNode().put("status", "ok"));
	}
}
