package io.macgyver.plugin.cloud.aws.scanner;

import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.macgyver.neorx.rest.MockNeoRxClient;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

/**
 * Some common test scaffolding to make it easy to unit test scanners.
 * @author rschoening
 *
 */
public abstract class AbstractAwsScannerUnitTest {

	ObjectMapper mapper = new ObjectMapper();
	
	NeoRxClient neo4j = new MockNeoRxClient();

	
	public String getAccountId() {
		return "123456123456";
	}

	AWSServiceClient newMockServiceClient() {
		
		AWSServiceClient c = Mockito.mock(AWSServiceClient.class);
		Mockito.when(c.getAccountId()).thenReturn(getAccountId());
		
		return c;
	}

}
