package io.macgyver.core.event;


public class LogMessage extends MacGyverMessage {
	String label = null;
	boolean sent = false;
	EventLogger eventLogger;
	
	public LogMessage(EventLogger eventLogger) {
		super();
		withEventType(LogMessage.class.getName());
		this.eventLogger = eventLogger;
	}
	public LogMessage withLabel(String label) {
		this.label = label;
		return this;
	}
	public String getLabel() {
		return label;
	}

	public LogMessage withMessage(String msg) {
		return (LogMessage) withAttribute("message", msg);
	}

	public void log() {
		eventLogger.logEvent(this);
	}
	
}