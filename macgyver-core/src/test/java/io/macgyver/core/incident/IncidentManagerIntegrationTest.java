package io.macgyver.core.incident;

import java.util.UUID;

import javax.inject.Inject;

import junit.extensions.TestDecorator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.google.common.base.Optional;

import io.macgyver.test.MacGyverIntegrationTest;

public class IncidentManagerIntegrationTest extends MacGyverIntegrationTest {

	@Inject
	IncidentManager incidentManager;

	@Test
	public void testIt() {
		Assertions.assertThat(incidentManager).isNotNull();

		Incident incident = new IncidentBuilder().incidentKey("test-incident-"+System.currentTimeMillis()).build();
		
		Incident i2 = incidentManager.createIncident(incident);
		
		Assertions.assertThat(i2).isNotNull();
		Assertions.assertThat(i2.getIncidentKey()).isEqualTo(incident.getIncidentKey());
		Assertions.assertThat(i2.isOpen()).isTrue();
		
		Incident acked = incidentManager.acknowledge(i2.getIncidentKey());
		
		Assertions.assertThat(acked.isOpen()).isFalse();
		Assertions.assertThat(acked.isAcknowledged()).isFalse();
		Assertions.assertThat(acked.isResolved()).isFalse();
/*
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().isPresent())
				.isTrue();

		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get()
						.getDescription()).isEqualTo(description);
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isOpen())
				.isTrue();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get()
						.isAcknowledged()).isFalse();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isResolved())
				.isFalse();
		
		
		// Now acknowledge it
		Assertions.assertThat(incidentManager.withIncidentKey(key).acknowledge().isAcknowledged()).isTrue();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isAcknowledged())
				.isTrue();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isOpen())
				.isFalse();
		
		// now create it again...should remain acknowledged
		incidentManager.withIncidentKey(key).create();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isAcknowledged())
				.isTrue();
		
		// now resolve it
		incidentManager.withIncidentKey(key).resolve();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isAcknowledged())
				.isFalse();
		Assertions.assertThat(
				incidentManager.withIncidentKey(key).get().get().isResolved())
				.isTrue();
		*/
	}
}
