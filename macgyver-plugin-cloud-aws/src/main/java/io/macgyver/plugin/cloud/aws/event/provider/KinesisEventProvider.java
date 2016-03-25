/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.plugin.cloud.aws.event.provider;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.catalina.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsScope;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.provider.AbstractEventProvider;
import io.macgyver.core.event.provider.local.LocalEventProvider;
import io.macgyver.core.util.JsonNodes;
import rx.Subscriber;
import rx.functions.Action1;
import rx.observers.SafeSubscriber;

public class KinesisEventProvider extends AbstractEventProvider {

	Logger logger = LoggerFactory.getLogger(KinesisEventProvider.class);

	AmazonKinesisAsyncClient kinesisClient;
	String kinesisStreamName;
	static ObjectMapper mapper = new ObjectMapper();
	AWSCredentialsProvider credentialsProvider;

	Region region = null;
	String applicationName = "local";

	Worker worker;
	
	static ThreadGroup threadGroup = new ThreadGroup("kinesis-event-provider");
	public class MyRecordProcessor implements IRecordProcessor {

		@Override
		public void initialize(InitializationInput initializationInput) {

			logger.info("{}.initialize()", this);
			logger.info("init seq: {}", initializationInput.getExtendedSequenceNumber().getSequenceNumber());

		}

		@Override
		public void processRecords(ProcessRecordsInput processRecordsInput) {
			logger.info("processRecords()");
			List<Record> list = processRecordsInput.getRecords();
			if (list != null) {
				for (Record r : list) {
					JsonNode nn = null;
					try {
						nn = mapper.readTree(r.getData().array());

					} catch (IOException | RuntimeException e) {
						logger.error("problem reading event from kinesis stream ", e);
					}

					if (nn != null) {
						try {
							// very important to call dispatch() here and not publish()!!!
							// dispatch will send the received event to Rx observers, which is what we want.
							// Calling publish() would result in an infinite loop.
							
							getProxy().internalDispatch(DistributedEvent.fromJsonNode(nn));
						} catch (RuntimeException e) {
							logger.warn("problem dispatching event to Rx system", e);
						}
					}

				}
				try {
					processRecordsInput.getCheckpointer().checkpoint();
				} catch (InvalidStateException | ShutdownException e) {
					logger.warn("", e);
				}
			}

		}

		@Override
		public void shutdown(ShutdownInput shutdownInput) {
			System.out.println("shutdown....");

		}
	};

	public KinesisEventProvider(DistributedEventProviderProxy distributedEventProviderProxy) {
		super(distributedEventProviderProxy);
	}

	public KinesisEventProvider withKinesisClient(AmazonKinesisAsyncClient c) {
		kinesisClient = c;
		return this;
	}

	public KinesisEventProvider withCredentialsProvider(AWSCredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
		return this;
	}

	public KinesisEventProvider withRegion(Regions region) {
		return withRegion(Region.getRegion(region));
	}
	
	/**
	 * Set the Kinesis application name.
	 */
	public KinesisEventProvider withApplicationName(String appName) {
		this.applicationName = appName;
		return this;
	}
	/**
	 * Get the Kinesis application name.
	 * @return
	 */
	public String getApplicationName() {
		return applicationName;
	}
	public KinesisEventProvider withRegion(Region region) {
		this.region = region;
		return this;
	}
	public KinesisEventProvider withStreamName(String streamName) {
		this.kinesisStreamName = streamName;
		return this;
	}

	public PutRecordRequest convert(DistributedEvent e) {
		Preconditions.checkState(kinesisStreamName != null, "kinesis stream name not set");
		PutRecordRequest prr = new PutRecordRequest();
		prr.setPartitionKey("0");
		prr.setStreamName(kinesisStreamName);
		prr.setData(ByteBuffer.wrap(e.getJson().toString().getBytes()));
		return prr;
	}

	@Override
	public boolean publish(DistributedEvent event) {
		Preconditions.checkState(kinesisClient != null,
				"kinesis client must be set. This probably means that start() has not been called.");
		Future<PutRecordResult> f = kinesisClient.putRecordAsync(convert(event));
		logger.info("publishing to kinesis"+event.getJson());
		try {
			// Returning a future and calling get() doesn't make too much sense.
			// In the future, we may just let this go completely async.
			PutRecordResult prr = f.get();

			logger.debug("put to shardId:{} sequenceNumber: {}", prr.getShardId(), prr.getSequenceNumber());
			return true;
		} catch (ExecutionException | InterruptedException | RuntimeException e) {
			logger.warn("problem",e);
		}
		return true;
	}

	@Override
	public void stop() {
		kinesisClient.shutdown();

	}

	@Override
	public DistributedEvent fetchNextEvent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void doStart() {

		Preconditions.checkState(kinesisClient==null,"kinesis client already set");
		Preconditions.checkState(worker==null,"kinesis worker already set");
		kinesisClient = new AmazonKinesisAsyncClient(credentialsProvider);
		kinesisClient.setRegion(region);
		IRecordProcessorFactory factory = new IRecordProcessorFactory() {

			@Override
			public IRecordProcessor createProcessor() {
				return new MyRecordProcessor();
			}
		};



		logger.info("initializing kinesis client");
		
		KinesisClientLibConfiguration cfg = new KinesisClientLibConfiguration(applicationName, kinesisStreamName,
				credentialsProvider, generateWorkerId()).withRegionName(region.getName());

		logger.info(MoreObjects.toStringHelper(cfg).add("applicationName", cfg.getApplicationName()).add("streamName", cfg.getStreamName()).add("workerId", cfg.getWorkerIdentifier()).add("region", cfg.getRegionName()).toString());
		worker = new Worker.Builder().recordProcessorFactory(factory).config(cfg).build();

		Thread t = new Thread(threadGroup,worker);
		t.start();

	}

	
	private String generateWorkerId() {
		String workerId = UUID.randomUUID().toString();

		try {
			workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + workerId;
		} catch (Exception e) {
			workerId = "localhost:" + workerId;
		}
		return workerId;
	}
	
	@Override
	public void run() {
		// no need to drop into the default event loop...so this is a no-op
	}
}
