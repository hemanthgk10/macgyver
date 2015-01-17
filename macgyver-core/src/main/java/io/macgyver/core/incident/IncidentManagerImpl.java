package io.macgyver.core.incident;

import io.macgyver.neorx.rest.NeoRxClient;

import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class IncidentManagerImpl extends IncidentManager {

	@Inject
	NeoRxClient neo4j;

	public IncidentBuilder withIncidentKey(String key) {

		IncidentBuilder b = new IncidentBuilder().incidentKey(key);

		return b;
	}

	/*
	 * protected Optional<Incident> doGet(IncidentBuilder b) { String cypher =
	 * "match (x:Incident {incidentKey: {incidentKey}}) return x";
	 * 
	 * JsonNode n = neo4j.execCypher(cypher,
	 * b.data()).toBlocking().firstOrDefault(null);
	 * 
	 * if (n==null) { return Optional.absent(); } Incident incident = new
	 * IncidentImpl(n); return Optional.fromNullable(incident);
	 * 
	 * }
	 * 
	 * protected Incident doResolve(IncidentBuilder b) {
	 * 
	 * String cypher =
	 * "match (x:Incident {incidentKey: {incidentKey}}) set x.status='resolved', x.resolvedTs=timestamp() return x"
	 * ;
	 * 
	 * JsonNode n = neo4j.execCypher(cypher, b.data()).toBlocking().first();
	 * 
	 * return new IncidentImpl(n);
	 * 
	 * }
	 * 
	 * protected Incident doAcknowledge(IncidentBuilder b) {
	 * 
	 * String cypher =
	 * "match (x:Incident {incidentKey: {incidentKey}}) set x.status='acknowledged', x.acknowledgdTs=timestamp() return x"
	 * ;
	 * 
	 * JsonNode n = neo4j.execCypher(cypher, b.data()).toBlocking().first();
	 * 
	 * return new IncidentImpl(n); }
	 * 
	 * protected Incident doCreate(IncidentBuilder b) {
	 * 
	 * String incidentKey = b.data().path("incidentKey").asText(); if
	 * (Strings.isNullOrEmpty(incidentKey)) { incidentKey =
	 * UUID.randomUUID().toString(); }
	 * 
	 * JsonNode n = neo4j.execCypher(
	 * "match (x:Incident) where x.incidentKey={incidentKey} return x"
	 * ,b.data()).toBlocking().firstOrDefault(null); if (n!=null) { IncidentImpl
	 * incident = new IncidentImpl(n); if (incident.isAcknowledged() ||
	 * incident.isOpen()) { return incident; } }
	 * 
	 * n = neo4j .execCypher(
	 * "merge (x:Incident {incidentKey: {incidentKey}}  ) return x",
	 * "incidentKey", incidentKey).toBlocking().first();
	 * 
	 * String cypher =
	 * "MATCH (n { incidentKey: {incidentKey} }) SET n = { props } RETURN n";
	 * 
	 * ObjectNode props = (ObjectNode) new ObjectMapper().createObjectNode()
	 * .setAll(b.data()); props.put("status", "open"); props.put("incidentKey",
	 * incidentKey); props.put("createTs", System.currentTimeMillis());
	 * ObjectNode args = new ObjectMapper().createObjectNode();
	 * args.put("incidentKey", incidentKey); args.set("props", props); n =
	 * neo4j.execCypher(cypher, args).toBlocking().first();
	 * 
	 * return new IncidentImpl(n); }
	 */

	public Incident acknowledge(String incidentKey) {
		String cypher = "match (x:Incident) where x.incidentKey={incidentKey} and (x.status='open' or x.status='acknowledged') set x.status='acknowledged' return x";
		JsonNode n = neo4j.execCypher(cypher, "incidentKey",incidentKey).toBlocking().firstOrDefault(null);
		
		if (n==null) {
			throw new IllegalStateException();
		}
		return new IncidentImpl(n);
	}
	
	
	@Override
	public Incident createIncident(Incident incident) {
		IncidentImpl impl = (IncidentImpl) incident;
		IncidentBuilder b = new IncidentBuilder(incident);
		String incidentKey = incident.getIncidentKey();
		if (Strings.isNullOrEmpty(incident.getIncidentKey())) {
			incidentKey = UUID.randomUUID().toString();
			b = b.incidentKey(incidentKey);
		}

		JsonNode n = neo4j
				.execCypher(
						"match (x:Incident) where x.incidentKey={incidentKey} return x",
						b.build().getData()).toBlocking().firstOrDefault(null);
		if (n != null) {
			impl = new IncidentImpl(n);
			if (impl.isAcknowledged() || impl.isOpen()) {
				return incident;
			}
		}

		n = neo4j
				.execCypher(
						"merge (x:Incident {incidentKey: {incidentKey}}  ) return x",
						"incidentKey", incidentKey).toBlocking().first();

		String cypher = "MATCH (n { incidentKey: {incidentKey} }) SET n = { props }, n.createTs=timestamp() RETURN n";

		ObjectNode props = (ObjectNode) new ObjectMapper().createObjectNode()
				.setAll(b.data());
		props.put("status", "open");
		props.put("incidentKey", incidentKey);
		ObjectNode args = new ObjectMapper().createObjectNode();
		args.put("incidentKey", incidentKey);
		args.set("props", props);
		n = neo4j.execCypher(cypher, args).toBlocking().first();

		return new IncidentImpl(n);

	}
}
