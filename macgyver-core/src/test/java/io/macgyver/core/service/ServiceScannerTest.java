package io.macgyver.core.service;

import java.util.concurrent.atomic.AtomicInteger;

import io.macgyver.core.graph.NodeInfo;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.test.MacGyverIntegrationTest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rx.functions.Action1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

public class ServiceScannerTest extends MacGyverIntegrationTest {

	@Autowired
	NeoRxClient neorx;
	
	public static class TestScanner extends ServiceScanner<String> {

		public TestScanner(NeoRxClient neo4j, String service) {
			super(neo4j, service);
			
		}

		@Override
		public void scan() {
			
			
		}
		
	}
	
	
	@Test
	public void testIt() {
		
		
		

		TestScanner ts = new TestScanner(neorx, "dummy");
		
		Action1<NodeInfo> action = new Action1<NodeInfo>() {

			@Override
			public void call(NodeInfo t1) {
				//System.out.println(t1.getNeoRxClient());
				
			}};
		ts.addDecorationAction(action);
		
		ObjectNode n = new ObjectMapper().createObjectNode().put("foo", "bar");
		
		ts.decorate(0,n);
		

		
	}
}
