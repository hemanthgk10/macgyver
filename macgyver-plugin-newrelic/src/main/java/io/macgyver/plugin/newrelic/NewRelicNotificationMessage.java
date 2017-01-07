package io.macgyver.plugin.newrelic;

import io.macgyver.core.event.MacGyverMessage;

public class NewRelicNotificationMessage extends MacGyverMessage {
	
	public static class DeploymentNotificationMessage extends NewRelicNotificationMessage {

	}
	
	public static class IncidentNotificationMessage extends NewRelicNotificationMessage {

	}

}
