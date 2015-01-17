package io.macgyver.core.incident;

import io.macgyver.core.MacGyverException;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class IncidentBuilder {

	ObjectNode props = new ObjectMapper().createObjectNode();

	public IncidentBuilder() {

	}

	public IncidentBuilder(Incident incident) {
		this(incident.getData());
	}
	public IncidentBuilder(ObjectNode props) {
		try {

			this.props = (ObjectNode) new ObjectMapper().readTree(props
					.toString());
			
		} catch (IOException e) {
			throw new MacGyverException(e);
		}

	}

	//public ObjectNode data() {
	//	return props;
	//}

	public ObjectNode data() {
		return props;
	}
	public Incident build() {
		try {
			
			ObjectNode n = (ObjectNode) new ObjectMapper().readTree(props
					.toString());
			if (!n.has("status")) {
				n.put("status","open");
			}
			
			return new IncidentImpl(n);
		} catch (IOException e) {
			throw new MacGyverException(e);
		}
	}

	protected IncidentBuilder property(String key, String val) {

		IncidentBuilder b = new IncidentBuilder(this.props);
		b.props.put(key, val);
		return b;
	}

	public IncidentBuilder incidentKey(String key) {
		return property("incidentKey", key);
	}

	public IncidentBuilder description(String description) {
		return property("description", description);
	}

}