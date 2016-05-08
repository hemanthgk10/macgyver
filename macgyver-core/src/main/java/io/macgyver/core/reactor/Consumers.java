package io.macgyver.core.reactor;

import org.slf4j.LoggerFactory;

import reactor.fn.Consumer;

public class Consumers {

	
	public static <T extends Consumer> T exceptionSafeConsumer(T consumer) {
		
		Consumer x = new Consumer() {

			@Override
			public void accept(Object t) {
				try {
					consumer.accept(t);
				}
				catch (Exception e) {
					LoggerFactory.getLogger(consumer.getClass()).warn("uncaught exception",e);
				}
				
			}
		};
		return (T) x;
	}
}
