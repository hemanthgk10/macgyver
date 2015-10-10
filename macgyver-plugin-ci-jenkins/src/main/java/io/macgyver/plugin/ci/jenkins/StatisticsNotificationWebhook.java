package io.macgyver.plugin.ci.jenkins;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Webhook for the Jenkins Statistics Notification Plugin
 * 
 * https://wiki.jenkins-ci.org/display/JENKINS/Statistics+Notification+Plugin
 * 
 * @author rschoening
 *
 */
@Controller
@RequestMapping("/api/plugin/ci/jenkins/statistics-notification")
public class StatisticsNotificationWebhook {

	Logger logger = LoggerFactory.getLogger(StatisticsNotificationWebhook.class);
	ObjectMapper mapper = new ObjectMapper();

	public StatisticsNotificationWebhook() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = "/builds", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = "application/json")
	@PreAuthorize("permitAll")
	public ResponseEntity<JsonNode> builds(@RequestBody JsonNode payload, HttpServletRequest request)
			throws IOException {

		logger.info("received builds webhook: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
		return ResponseEntity.ok(mapper.createObjectNode().put("status", "ok"));
	}

	@RequestMapping(value = "/queues", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = "application/json")
	@PreAuthorize("permitAll")
	public ResponseEntity<JsonNode> statisticsNotificationQueues(@RequestBody String payload,
			HttpServletRequest request) throws IOException {

		logger.info("received queues webhook: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
		return ResponseEntity.ok(mapper.createObjectNode().put("status", "ok"));
	}

	@RequestMapping(value = "/projects", method = { RequestMethod.POST,
			RequestMethod.PUT }, produces = "application/json")
	@PreAuthorize("permitAll")
	public ResponseEntity<JsonNode> statisticsNotificationProjects(@RequestBody String payload,
			HttpServletRequest request) throws IOException {

		logger.info("received projects webhook: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
		return ResponseEntity.ok(mapper.createObjectNode().put("status", "ok"));
	}

}
