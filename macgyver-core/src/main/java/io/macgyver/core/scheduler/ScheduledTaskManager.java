package io.macgyver.core.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;

public class ScheduledTaskManager {


	Logger logger = LoggerFactory.getLogger(ScheduledTaskManager.class);
	
	@Autowired
	NeoRxClient neo4j;
	
	boolean schedulerEnabled=true;
	
	public void scheduleInline(String id, String cron, String script) {
		scheduleInline(id,cron,script,"groovy");
	}
	public void scheduleInline(String id, String cron, String script, String language) {
		String cypher = "merge (t:ScheduledTask {id:{id}}) set t.scheduledBy='manual', t.cron={cron}, t.inlineScript={script}, t.enabled=true, t.inlineScriptLanguage={language} return t";
		neo4j.execCypher(cypher, "id",id,"cron",cron, "script", script, "language", language);
	}
	
	public void updateSchedule(String id, String cron) {
		String cypher = "match (t:ScheduledTask {id:{id}}) set t.cron={cron}";
		neo4j.execCypher(cypher, "id",id,"cron",cron);
	}
	
    public void disable(String id) {
        enable(id,false);
    }
    public void enable(String id) {
        enable(id,true);
    }
	public void enable(String id, boolean b) {
		String cypher = "match (t:ScheduledTask {id:{id}}) set t.enabled={enabled}";
		neo4j.execCypher(cypher, "id",id,"enabled",b);
	}
	
	public boolean isEnabled(JsonNode config) {
		
		return schedulerEnabled && config.path("enabled").asBoolean(true);
		
	}
	
	public void setSchedulerEnabled(boolean b) {
		logger.info("setting global scheduler status: {}",b);
		this.schedulerEnabled = b;
	}
	public boolean isSchedulerEnabled() {
		return this.schedulerEnabled;
	}

}
