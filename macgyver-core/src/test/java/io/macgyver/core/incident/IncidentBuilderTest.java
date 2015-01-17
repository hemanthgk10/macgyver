package io.macgyver.core.incident;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class IncidentBuilderTest {

	
	@Test
	public void testDefaultValues() {		
		Incident x = new IncidentBuilder().build();
		assertThat(x).isNotNull();
		assertThat(x.getDescription()).isNull();
		assertThat(x.getIncidentKey()).isNull();
		assertThat(x.isOpen()).isTrue();
	}
	
	@Test
	public void testValues() {		
		Incident x = new IncidentBuilder().incidentKey("testkey").description("My Description").build();
		assertThat(x).isNotNull();
		assertThat(x.getDescription()).isEqualTo("My Description");
		assertThat(x.getIncidentKey()).isEqualTo("testkey");
	}
	
}
