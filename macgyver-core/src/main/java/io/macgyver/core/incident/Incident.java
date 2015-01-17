package io.macgyver.core.incident;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Incident {

	public String getIncidentKey();
	public String getDescription();
	
	public boolean isOpen();
	public boolean isResolved();
	public boolean isAcknowledged();
	
	public ObjectNode getData();
}
