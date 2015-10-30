package io.macgyver.plugin.cloud.aws.scanner;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class AWSScannerGroup extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(AWSScannerGroup.class);
	
	public List<AWSServiceScanner> scannerList = new CopyOnWriteArrayList<>();

	public AWSScannerGroup(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);



	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {
		scannerList.forEach(it -> {
			logger.info("{} scanning region {}",it,region);
			try {
				it.scan(region);
			}
			catch (RuntimeException e) {
				logger.error("problem scanning region "+region,e);
			}
		});
		
	}

}
