package io.macgyver.plugin.cloud.aws.kinesis;


import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;

public class Example {

	public static void main(String[] args) {

		
		AmazonKinesisClient c = new AmazonKinesisClient(
				new DefaultAWSCredentialsProviderChain());
		c.setRegion(Region.getRegion(Regions.US_WEST_2));

		KinesisProducerConfiguration config = new KinesisProducerConfiguration()
				.setRecordMaxBufferedTime(3000)
				.setMaxConnections(1)
				.setRequestTimeout(60000)
				.setCredentialsProvider(
						new DefaultAWSCredentialsProviderChain())
				.setRegion("us-west-2");

	

		
		Thread t = new Thread() {

			@Override
			public void run() {
				while (true == true) {
					System.out.println(c.listStreams().getStreamNames());
					
					PutRecordRequest pr = new PutRecordRequest();
					pr.setPartitionKey("1");
					pr.setStreamName("test-macgyver");
					pr.setData(ByteBuffer.wrap("{\"hello\":\"world\"}".getBytes()));
					c.putRecord(pr);
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
				}
			}

		};

		t.start();

	

	}
}
