package io.macgyver.core.scheduler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

import io.macgyver.core.Kernel;
import io.macgyver.core.resource.Resource;

public class CrontabExpressionExtractor {

	private String profile = Kernel.getExecutionProfile().or("");

	private Logger logger = LoggerFactory.getLogger(CrontabExpressionExtractor.class);

	private class CrontabLineProcessor implements LineProcessor<List<ObjectNode>> {
		int i = 0;
		int last = -1;
		List<String> lines = new ArrayList<>();

		@Override
		public boolean processLine(String line) throws IOException {
			if (i++ > 50) {
				// only look through the first 50 lines
				return false;
			}
			if (line != null && line.contains(ScheduledTaskManager.SCHEDULE_TOKEN)) {
				// only parse consecutive schedule lines
				if (last < 0 || last == (i - 1)) {
					last = i;
				} else {
					return false;
				}
				lines.add(line.substring(line.indexOf(ScheduledTaskManager.SCHEDULE_TOKEN)
						+ ScheduledTaskManager.SCHEDULE_TOKEN.length()).trim());
			}
			return true;
		}

		@Override
		public List<ObjectNode> getResult() {
			ObjectMapper mapper = new ObjectMapper();
			List<ObjectNode> results = new ArrayList<>();
			lines.forEach(s -> {
				try {
					results.add((ObjectNode) mapper.readTree(s));
				} catch (IOException e) {
					logger.warn("problem parsing: {}", s);
				}
			});
			return results;
		}
	}

	public CrontabExpressionExtractor() {
	}

	public Optional<ObjectNode> extractCronExpression(Resource r) {
		try (StringReader sr = new StringReader(r.getContentAsString())) {
			List<ObjectNode> scheduleNodes = CharStreams.readLines(sr, new CrontabLineProcessor());
			for (ObjectNode n : scheduleNodes) {
				JsonNode envNode = n.path("env");
				if (envNode.isMissingNode() || profile.startsWith(envNode.asText())) {
					return Optional.of(n);
				}
			}
			return Optional.absent();
		} catch (IOException | RuntimeException e) {
			try {
				logger.warn("unable to extract cron expression: ", r.getContentAsString());
			} catch (Exception IGNORE) {
				logger.warn("unable to extract cron expression");
			}
		}
		return Optional.absent();
	}

	public CrontabExpressionExtractor withProfile(String profile) {
		this.profile = profile;
		return this;
	}

}