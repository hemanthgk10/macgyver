package io.macgyver.plugin.cloud.aws.ec2;

import java.io.IOException;

import io.macgyver.core.test.StandaloneServiceBuilder;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceFactory;
import io.macgyver.test.MacGyverIntegrationTest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

public class EC2InstanceScannerTest extends MacGyverIntegrationTest {


	
	@Test
	public void testFlattenInstance() throws IOException {
		String json = "{\n" + 
				"  \"instanceId\" : \"i-abcde1234\",\n" + 
				"  \"imageId\" : \"ami-abcd5544\",\n" + 
				"  \"state\" : {\n" + 
				"    \"code\" : 16,\n" + 
				"    \"name\" : \"running\"\n" + 
				"  },\n" + 
				"  \"privateDnsName\" : \"ip-10-101-1-101.us-west-1.compute.internal\",\n" + 
				"  \"publicDnsName\" : \"\",\n" + 
				"  \"stateTransitionReason\" : \"\",\n" + 
				"  \"keyName\" : \"mykey\",\n" + 
				"  \"amiLaunchIndex\" : 0,\n" + 
				"  \"productCodes\" : [ ],\n" + 
				"  \"instanceType\" : \"c3.large\",\n" + 
				"  \"launchTime\" : 1444437427000,\n" + 
				"  \"placement\" : {\n" + 
				"    \"availabilityZone\" : \"us-west-1b\",\n" + 
				"    \"groupName\" : \"\",\n" + 
				"    \"tenancy\" : \"default\"\n" + 
				"  },\n" + 
				"  \"kernelId\" : null,\n" + 
				"  \"ramdiskId\" : null,\n" + 
				"  \"platform\" : \"windows\",\n" + 
				"  \"monitoring\" : {\n" + 
				"    \"state\" : \"disabled\"\n" + 
				"  },\n" + 
				"  \"subnetId\" : \"subnet-12345678\",\n" + 
				"  \"vpcId\" : \"vpc-12345678\",\n" + 
				"  \"privateIpAddress\" : \"10.101.1.101\",\n" + 
				"  \"publicIpAddress\" : null,\n" + 
				"  \"stateReason\" : null,\n" + 
				"  \"architecture\" : \"x86_64\",\n" + 
				"  \"rootDeviceType\" : \"ebs\",\n" + 
				"  \"rootDeviceName\" : \"/dev/sda1\",\n" + 
				"  \"blockDeviceMappings\" : [ {\n" + 
				"    \"deviceName\" : \"/dev/sda1\",\n" + 
				"    \"ebs\" : {\n" + 
				"      \"volumeId\" : \"vol-12345678\",\n" + 
				"      \"status\" : \"attached\",\n" + 
				"      \"attachTime\" : 1432937430000,\n" + 
				"      \"deleteOnTermination\" : true\n" + 
				"    }\n" + 
				"  } ],\n" + 
				"  \"virtualizationType\" : \"hvm\",\n" + 
				"  \"instanceLifecycle\" : null,\n" + 
				"  \"spotInstanceRequestId\" : null,\n" + 
				"  \"clientToken\" : \"abcdef1234568\",\n" + 
				"  \"tags\" : [ {\n" + 
				"    \"key\" : \"Name\",\n" + 
				"    \"value\" : \"mynode\"\n" + 
				"  } ],\n" + 
				"  \"securityGroups\" : [ {\n" + 
				"    \"groupName\" : \"mygroup\",\n" + 
				"    \"groupId\" : \"sg-abcd1234\"\n" + 
				"  } ],\n" + 
				"  \"sourceDestCheck\" : true,\n" + 
				"  \"hypervisor\" : \"xen\",\n" + 
				"  \"networkInterfaces\" : [ {\n" + 
				"    \"networkInterfaceId\" : \"eni-44444d1d\",\n" + 
				"    \"subnetId\" : \"subnet-61444444\",\n" + 
				"    \"vpcId\" : \"vpc-60e00000\",\n" + 
				"    \"description\" : \"Primary network interface\",\n" + 
				"    \"ownerId\" : \"758521605024\",\n" + 
				"    \"status\" : \"in-use\",\n" + 
				"    \"macAddress\" : \"06:22:22:28:ec:95\",\n" + 
				"    \"privateIpAddress\" : \"10.101.1.101\",\n" + 
				"    \"privateDnsName\" : \"ip-10-101-1-101.us-west-1.compute.internal\",\n" + 
				"    \"sourceDestCheck\" : true,\n" + 
				"    \"groups\" : [ {\n" + 
				"      \"groupName\" : \"QA_Group\",\n" + 
				"      \"groupId\" : \"sg-26100000\"\n" + 
				"    } ],\n" + 
				"    \"attachment\" : {\n" + 
				"      \"attachmentId\" : \"eni-attach-8ef00000\",\n" + 
				"      \"deviceIndex\" : 0,\n" + 
				"      \"status\" : \"attached\",\n" + 
				"      \"attachTime\" : 1432937427000,\n" + 
				"      \"deleteOnTermination\" : true\n" + 
				"    },\n" + 
				"    \"association\" : null,\n" + 
				"    \"privateIpAddresses\" : [ {\n" + 
				"      \"privateIpAddress\" : \"10.101.1.101\",\n" + 
				"      \"privateDnsName\" : \"ip-10-101-1-101.us-west-1.compute.internal\",\n" + 
				"      \"primary\" : true,\n" + 
				"      \"association\" : null\n" + 
				"    } ]\n" + 
				"  } ],\n" + 
				"  \"iamInstanceProfile\" : null,\n" + 
				"  \"ebsOptimized\" : false,\n" + 
				"  \"sriovNetSupport\" : null\n" + 
				"}";
		
		EC2InstanceScanner scanner = new EC2InstanceScanner(null, null);
		
		JsonNode n = scanner.m.readTree(json);
		
		JsonNode out = scanner.flattenInstance(n);
		
		logger.info("source: {}",scanner.m.writerWithDefaultPrettyPrinter().writeValueAsString( n));
			
		Assertions.assertThat(out.get("ec2_instanceId").asText()).isEqualTo("i-abcde1234");
		Assertions.assertThat(out.get("ec2_privateIpAddress").asText()).isEqualTo("10.101.1.101");
		Assertions.assertThat(out.get("ec2_stateReason").isNull()).isTrue();
	}
}
