package io.macgyver.plugin.ci.jenkins;

import io.macgyver.core.reactor.MacGyverMessage;

public class JenkinsNotificationMessage extends MacGyverMessage {

	public static class ProjectNotificationMessage extends JenkinsNotificationMessage {

	}
	

	public static class BuildNotificationMessage extends JenkinsNotificationMessage {

	}

	public static class QueueNotificationMessage extends JenkinsNotificationMessage {

	}
	
}
