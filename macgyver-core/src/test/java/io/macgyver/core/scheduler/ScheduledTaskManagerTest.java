package io.macgyver.core.scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.resource.Resource;
import io.macgyver.core.resource.StringResource;

public class ScheduledTaskManagerTest {

	ObjectMapper mapper = new ObjectMapper();
	
	ScheduledTaskManager stm = new ScheduledTaskManager();
	
	@Test
	public void testDefault() {
			
		Assertions.assertThat(stm.isSchedulerEnabled()).isTrue();
		
	}
	
	@Test 
	public void testLocalDefault() {
		ObjectNode n = mapper.createObjectNode();		
		Assertions.assertThat(stm.isEnabled(mapper.createObjectNode())).isTrue();
	}

	@Test 
	public void testEnabled() {
		ObjectNode n = mapper.createObjectNode();
		Assertions.assertThat(stm.isEnabled(mapper.createObjectNode().put("enabled", "true"))).isTrue();
		Assertions.assertThat(stm.isEnabled(mapper.createObjectNode().put("enabled", "xx"))).isTrue();
		
		Assertions.assertThat(stm.isEnabled(mapper.createObjectNode().put("enabled", "false"))).isFalse();
	}

	@Test 
	public void testManualScheduledScript() {
		
		
		Assertions.assertThat(stm.isScriptManuallyScheduled(null)).isFalse();
		Assertions.assertThat(stm.isScriptManuallyScheduled(mapper.createObjectNode())).isFalse();
		Assertions.assertThat(stm.isScriptManuallyScheduled(mapper.createObjectNode().put("scheduledBy", "manual"))).isTrue();
	}
		
		

	
	@Test
	public void testExtractCron() {
		Assertions.assertThat(stm.extractCronExpression(new StringResource("")).isPresent()).isFalse();
		Assertions.assertThat(stm.extractCronExpression(new StringResource("#@Schedule{}")).isPresent()).isTrue();
		Assertions.assertThat(stm.extractCronExpression(new StringResource("// #@Schedule{}")).isPresent()).isTrue();
		Assertions.assertThat(stm.extractCronExpression(new StringResource("// #@Schedule{\"foo\":\"bar\"}")).get().path("foo").asText()).isEqualTo("bar");
				
		Assertions.assertThat(stm.extractCronExpression(new StringResource("// #@Schedule{\"invalidJson:\"foo\"}")).isPresent()).isFalse();
		
	}
		
	

}
