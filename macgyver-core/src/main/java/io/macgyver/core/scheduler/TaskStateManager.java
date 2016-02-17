package io.macgyver.core.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.macgyver.core.Kernel;
import io.macgyver.neorx.rest.NeoRxClient;

/**
 * TaskStateManager keeps track of the state of running tasks.  It is not involved in the coordination or scheduling of tasks.  Its
 * sole pupose is to keep track of state.
 * 
 * @author rschoening
 *
 */
public class TaskStateManager {

	public static enum TaskState {
		STARTED, COMPLETED, FAILED, CANCELLED;
	}

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	NeoRxClient neo4j;

	@Autowired
	Ignite ignite;

	Logger logger = LoggerFactory.getLogger(TaskStateManager.class);

	long purgeHorizon = 24;
	TimeUnit purgeHorizonUnit = TimeUnit.HOURS;

	public void recordTaskStart(String id) {

		String host = "localhost";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// stick with default
		}

		Instant now = Instant.now();

		String date = formatInstant(now);

		String cypher = "merge (t:TaskState {id:{taskId}}) set t.state={state},t.hostname={hostname},t.processUuid={processUuid}, t.startTs={ts}, t.startDate={date} return t";

		String processId = "unknown";
		try {
			processId = ignite.cluster().localNode().id().toString();
		} catch (RuntimeException e) {
			// stick with default
		}
		neo4j.execCypher(cypher, "taskId", id, "processUuid", processId, "hostname", host, "ts", now.toEpochMilli(),
				"date", date, "state", TaskState.STARTED.toString());

	}

	public String formatInstant(Instant instant) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneOffset.UTC).format(instant);
	}

	public void recordTaskStateProperties(String id, JsonNode props) {
		String cypher = "match (t:TaskState {id:{taskId}}) set t+={props} return t";
		Kernel.getApplicationContext().getBean(NeoRxClient.class).execCypher(cypher, "taskId",
				id, "props", props);
	}
	public void recordTaskEnd(String id, TaskState state) {

		Instant now = Instant.now();

		String date = formatInstant(now);

		String cypher = "match (t:TaskState {id:{taskId}}) set t.endTs={ts},t.endDate={date} ,t.state={state} return t";

		neo4j.execCypher(cypher, "taskId", id, "ts", now.toEpochMilli(), "date", date, "state", state.toString());

	}

	public void purge() {
		try {
			purge(purgeHorizon, purgeHorizonUnit);
		} catch (RuntimeException e) {
			logger.warn("problem purging", e);
		}

		try {
			newPurgeRunnable();
		} catch (RuntimeException e) {
			logger.warn("problem purging abandoned Task entries", e);
		}
	}

	public void purge(long units, TimeUnit unit) {
		long horizon = System.currentTimeMillis() - unit.toMillis(units);

		logger.info("purging Task entries before {}", new Date(horizon));
		String cypher = "match (t:TaskState) where t.startTs<{horizon} delete t";

		neo4j.execCypher(cypher, "horizon", horizon);
	}

	
	public void markTerminatedTasksAsCancelled() {
		
		List<String> idList = Lists.newArrayList();

		Instant now = Instant.now();

		ignite.cluster().nodes().forEach(it -> {
			idList.add(it.id().toString());
		});

		String cypher = "match (t:TaskState) where t.state='STARTED' and not has(t.endTs) and not t.processUuid in {list} set t.endTs={endTs}, t.endDate={endDate}, t.state={state} return t";
		neo4j.execCypher(cypher, "list", idList, "endTs", now.toEpochMilli(), "endDate", formatInstant(now), "state",
				TaskState.CANCELLED.toString());

	}

	public Runnable newMarkTerminatedRunnable() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					markTerminatedTasksAsCancelled();
				} catch (Exception e) {
					logger.warn("problem marking terminated tasks", e);
				}
			}
		};
		return r;
	}

	public Runnable newPurgeRunnable() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					purge();
				} catch (Exception e) {
					logger.warn("problem purging old tasks",e);
				}
			}
		};
		return r;
	}
}
