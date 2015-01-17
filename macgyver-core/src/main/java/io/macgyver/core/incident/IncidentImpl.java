package io.macgyver.core.incident;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;

public class IncidentImpl implements Incident {

	ObjectNode n;

	protected IncidentImpl(JsonNode n) {
		this((ObjectNode)n);
	}
	protected IncidentImpl(ObjectNode n) {
		this.n = n;
	}

	
	public ObjectNode getData() {
		return n;
	}
	
	@Override
	public String getIncidentKey() {
		return n.path("incidentKey").asText(null);
	}

	@Override
	public String getDescription() {
		return n.path("description").asText(null);
	}

	public String toString()  {
		return MoreObjects.toStringHelper(this).add("incidentKey", getIncidentKey()).add("description", getDescription()).toString();
	}

	@Override
	public boolean isOpen() {
		return n.path("status").asText("xxx").equalsIgnoreCase("open");
	}

	@Override
	public boolean isResolved() {
		return n.path("status").asText("xxx").equalsIgnoreCase("resolved");
	}

	@Override
	public boolean isAcknowledged() {
		return n.path("status").asText("xxx").equalsIgnoreCase("acknowledged");
	}
}
