package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public  abstract class AWSServiceScanner {

	AWSServiceClient client;
	Logger logger = LoggerFactory.getLogger(AWSServiceScanner.class);
	static ObjectMapper mapper = new ObjectMapper();
	NeoRxClient neo4j;

	
	public AWSServiceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		Preconditions.checkNotNull(client);
		Preconditions.checkNotNull(neo4j);
	
		this.client = client;
		this.neo4j = neo4j;
		
	}
	
	public AWSServiceClient getAWSServiceClient() {
		return client;
	}
	
	public NeoRxClient getNeoRxClient() {
		return neo4j;
	}

	public abstract Optional<String> computeArn(JsonNode n);
	
	
	public void scanAllRegions() {
		for (Regions r: Regions.values()) {
			try {
				scan(Region.getRegion(r));
			}
			catch (RuntimeException e) {
				logger.warn("regions",e);
			}
		}
	}
	public abstract void scan(Region region);
	
	public void scan(String region) {
		
		scan(Region.getRegion(Regions.fromName(region)));
	}
	
	protected ObjectNode flatten(ObjectNode n) {
		ObjectNode r = mapper.createObjectNode();

		n.fields().forEachRemaining(it -> {

			if (!it.getValue().isContainerNode()) {
				r.set("aws_"+it.getKey(), it.getValue());
			}

		});

		n.path("tags").iterator().forEachRemaining(it -> {
			String tagKey = "aws_tag_"+it.path("key").asText();
			r.put(tagKey, it.path("value").asText());
		});
		
		Optional<String> arn = computeArn(r);
		if (arn.isPresent()) {
			r.put("aws_arn", arn.get());
		}
	
		return r;
	}
	public String getAccountId() {
		return client.getAccountId();
	}
	
	public ObjectNode convertAwsObject(Object x, Region region) {
		ObjectNode n = mapper.valueToTree(x);
		n.put("region", region.getName());
		n.put("account", getAccountId());
		n = flatten(n);
		return n;
	}
}
