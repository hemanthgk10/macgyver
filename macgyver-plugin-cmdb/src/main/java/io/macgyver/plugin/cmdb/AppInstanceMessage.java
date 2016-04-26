package io.macgyver.plugin.cmdb;

import io.macgyver.core.reactor.MacGyverMessage;

public class AppInstanceMessage extends MacGyverMessage {

	public static class AppInstanceDiscoveryMessage extends AppInstanceMessage {

	}

	public static class AppInstanceStartMessage extends AppInstanceMessage {

	}
	
	public static class AppInstanceRevisionUpdateMessage extends AppInstanceMessage {

	}
	public static class AppInstanceVersionUpdateMessage extends AppInstanceMessage {

	}
}
