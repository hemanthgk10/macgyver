package io.macgyver.core.event;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.macgyver.core.util.JsonNodes;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.selector.Selectors;

public class Slf4jEventWriter {

	Logger logger = LoggerFactory.getLogger(Slf4jEventWriter.class);
	@Autowired
	EventBus eventBus;

	@PostConstruct
	public void subscribe() {
		eventBus.on(Selectors.type(MacGyverMessage.class), (Event<MacGyverMessage> x) -> {
			if (logger.isDebugEnabled()) {
				logger.debug("logging event:\n {}",JsonNodes.pretty(x.getData().getEnvelope()));
			}
		});
	}
}
