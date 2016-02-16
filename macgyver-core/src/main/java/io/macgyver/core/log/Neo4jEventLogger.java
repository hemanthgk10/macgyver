package io.macgyver.core.log;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.macgyver.core.event.DistributedEventSystem;
import io.macgyver.neorx.rest.NeoRxClient;

public class Neo4jEventLogger extends EventLogger {

	public static final String EVENT_LOG_LABEL="EventLog";
	
	@Autowired
	NeoRxClient neo4j;
	

	@Override
	protected void doLogEvent(Event event) {
		
		Preconditions.checkNotNull(neo4j,"neo4j must be set");
	
		String labelClause="";
		if (!Strings.isNullOrEmpty(event.label)) {
			checkLabel(event.label);
			labelClause=":"+event.label;
		}
		ObjectNode props = (ObjectNode) event.data;
		
		applyTimestamp(event.instant,props);
		
		String cypher = "create (x:EventLog" + labelClause  +") set x={props}";
		
		neo4j.execCypher(cypher, "props",props);
		
		
	}
	
	
	protected void applyTimestamp(Instant instant, ObjectNode n) {
		if (instant==null) {
		 instant = Instant.now();
		}
		
		
		n.put("eventTs", instant.toEpochMilli());
		
		n.put("eventDate", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
		        .withZone(ZoneOffset.UTC)
		        .format(instant));
	}
	
	

	protected void checkLabel(String label) {
		
		Preconditions.checkArgument(!Strings.isNullOrEmpty(label),"label cannot be empty");
		Preconditions.checkArgument(Character.isUpperCase(label.charAt(0)),"first character of label must be upper case");
		char [] c = label.toCharArray();
		for (int i=0; i<c.length; i ++) {
			Preconditions.checkArgument(Character.isLetterOrDigit(c[i]), "label must be alpha-numeric");
		}
	}







}
