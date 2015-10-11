package io.macgyver.core.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodesTest {

	@Test
	public void testNonArrayNode() {
		
		ObjectNode n = JsonNodes.mapper.createObjectNode();
		
		n.put("a", 1);
		n.put("b", 2);
		Assertions.assertThat(JsonNodes.arrayToList(n)).isEmpty();
	}
	
	@Test
	public void testArrayNode() {
		
		ArrayNode n = JsonNodes.mapper.createArrayNode();
		
		n.add("a");
		n.add("b");
		
		Assertions.assertThat(JsonNodes.arrayToList(n)).hasSize(2);
	}
}
