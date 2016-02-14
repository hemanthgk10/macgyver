package io.macgyver.plugin.cloud.aws.event.provider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.util.BackoffThrottle;
import io.macgyver.core.util.JsonNodes;

public class SQSDistributedEventBridge {


	Logger logger = LoggerFactory.getLogger(SQSDistributedEventBridge.class);
	
	ObjectMapper mapper = new ObjectMapper();
	AmazonSQSClient sqsClient;
	String sqsQueueUrl = null;
	
	DistributedEventProvider target;

	BackoffThrottle throttle = new BackoffThrottle();
	static ThreadGroup threadGroup = new ThreadGroup("sqs-bridge");
	
	protected SQSDistributedEventBridge() {
		
	}
	public void forward(Message m) {
	
		convert(m).ifPresent(it -> {
			target.publish(it);
		});
	
		
	}
	
	public Optional<DistributedEvent> convert(Message m) {
		
	
		try {
			JsonNode n = mapper.readTree(m.getBody());
			
			if (n.path("Type").asText().equals("Notification") && n.has("Message")) {
				// unwrap SNS-originated messages
				n = mapper.readTree(n.path("Message").asText());
				
				
			}
			return Optional.of(DistributedEvent.create().payload(n));	
		}
		catch (IOException e) {
			logger.debug("could not process message...dropping: "+m,e);
		}
		
		return Optional.empty();
	}
	
	public void forward(ReceiveMessageResult sqs) {
		
		sqs.getMessages().forEach(m -> {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("processing SQS Message: {}",m);
				}
				forward(m);
				
			}
			catch (RuntimeException e) {
				logger.warn("problem forwarding ",e);
			}
			finally {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("deleting sqs message id="+m.getMessageId()+" receiptHandle="+m.getReceiptHandle());
					}
					sqsClient.deleteMessage(sqsQueueUrl, m.getReceiptHandle());
				}
				catch (Exception e) {
					logger.error("could not delete message id="+m.getMessageId()+" receiptHandle="+m.getReceiptHandle());
				}
			}
		});
		
		
	}
	
	public void processOnce() {
		logger.debug("processOnce()");
		ReceiveMessageRequest rmr = new ReceiveMessageRequest(sqsQueueUrl);
		rmr.setWaitTimeSeconds(10);
		forward(sqsClient.receiveMessage(rmr));
	}
	
	public void processEventLoop() {
		while (true==true) {
			try {
				processOnce();
				throttle.markSuccess();
			}
			catch (Exception e) {
				logger.warn("failed to process sqs",e);
				throttle.markFailureAndSleep();
				logger.info("continuing");
			}
		}
	}
	
	public static SQSDistributedEventBridge to(DistributedEventProvider target) {
		SQSDistributedEventBridge bridge = new SQSDistributedEventBridge();
		bridge.target = target;
		return bridge;
	}
	
	public SQSDistributedEventBridge from(AmazonSQSClient client, String queue) {
		this.sqsClient = client;
		this.sqsQueueUrl = queue;
		return this;
	}
	
	public void start() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				processEventLoop();				
			}
			
		};
		
		Thread t = new Thread(threadGroup,r);
		t.setDaemon(true);
		logger.info("starting SQS bridge thread {}",t);
		t.start();
	}
	
	

}
