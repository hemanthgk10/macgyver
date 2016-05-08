package io.macgyver.core.metrics;

import java.util.concurrent.ThreadPoolExecutor;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

public class MetricsUtil {

	
	
	public static void monitorExecutor(MetricRegistry metricRegistry, ThreadPoolExecutor executor, String name) {
		metricRegistry.register(MetricRegistry.name(name, "taskCount"),new Gauge<Long>() {

			@Override
			public Long getValue() {
				return executor.getTaskCount();
			}
		});
		
		metricRegistry.register(MetricRegistry.name(name, "largestPoolSize"),new Gauge<Integer>() {

			@Override
			public Integer getValue() {
				return executor.getLargestPoolSize();
			}
		});
		metricRegistry.register(MetricRegistry.name(name, "poolSize"),new Gauge<Integer>() {

			@Override
			public Integer getValue() {
				return executor.getPoolSize();
			}
		});
		metricRegistry.register(MetricRegistry.name(name, "completedTaskCount"),new Gauge<Long>() {

			@Override
			public Long getValue() {
				return executor.getCompletedTaskCount();
			}
		});
	}
}
