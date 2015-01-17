package io.macgyver.core.incident;

import io.macgyver.core.MacGyverException;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;


public abstract class IncidentManager {


	public abstract Incident acknowledge(String id);
	public abstract Incident createIncident(Incident incident);
	

}
