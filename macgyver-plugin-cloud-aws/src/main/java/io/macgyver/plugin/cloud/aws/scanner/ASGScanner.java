package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class ASGScanner extends AWSServiceScanner {
	NeoRxClient neoRx = new NeoRxClient();
	ObjectMapper mapper = new ObjectMapper();

	public ASGScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.of(n.path("aws_autoScalingGroupARN").asText());
	}

	@Override
	public void scan(Region region) {
		try { 
			AmazonAutoScalingClient client = new AmazonAutoScalingClient(getAWSServiceClient().getCredentialsProvider()).withRegion(region); 
			DescribeAutoScalingGroupsResult results = client.describeAutoScalingGroups();
			results.getAutoScalingGroups().forEach(asg -> {
				ObjectNode n = convertAwsObject(asg, region);
				String asgArn = n.path("aws_arn").asText();
				
				String cypher = "merge (x:AwsAsg {aws_arn:{aws_arn}}) set x+={props}, x.updateTs=timestamp()";
				
				Preconditions.checkNotNull(neoRx);
				neoRx.execCypher(cypher, "aws_arn",asgArn, "props",n);

				mapAsgRelationships(asg, asgArn, region.getName());				
				
			});
		} catch (RuntimeException e) { 
			logger.warn("problem scanning auto-scaling groups",e);
		}
	}

	protected void mapAsgRelationships(AutoScalingGroup asg, String asgArn, String region) { 
		JsonNode n = mapper.valueToTree(asg);
 
		String subnets = n.path("vpczoneIdentifier").asText().trim();
		String launchConfig = n.path("launchConfigurationName").asText().trim();
		JsonNode instances = n.path("instances");
		JsonNode elbs = n.path("loadBalancerNames");
		
		mapAsgToSubnet(subnets, asgArn, region);
		mapAsgToLaunchConfig(launchConfig, asgArn, region);
		mapAsgToInstance(instances, asgArn, region);
		mapAsgToElb(elbs, asgArn, region);
		
	}
	
	protected void mapAsgToLaunchConfig(String launchConfig, String asgArn, String region) { 
		String cypher = "match (x:AwsAsg {aws_arn:{asgArn}}), (y:AwsLaunchConfig {aws_launchConfigurationName:{lcn}, aws_region:{region}}) "
				+ "merge (y)-[r:TEMPLATE_FOR]->(x) set r.updateTs=timestamp()";
		neoRx.execCypher(cypher, "asgArn",asgArn, "lcn",launchConfig, "region",region);
	}
	
	protected void mapAsgToSubnet(String subnets, String asgArn, String region) {
		String[] arr = subnets.split(",");
		for (String s : arr) { 
			String subnetArn = String.format("arn:aws:ec2:%s:%s:subnet/%s",region, getAccountId() ,s.trim());

			String cypher = "match (x:AwsAsg {aws_arn:{asgArn}}), (y:AwsSubnet {aws_arn:{subnetArn}}) "
					+ "merge (x)-[r:LAUNCHES_INSTANCES_IN]->(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "asgArn",asgArn, "subnetArn",subnetArn);
		}
	}
	
	protected void mapAsgToInstance(JsonNode instances, String asgArn, String region) {
		for (JsonNode i : instances) { 
			String instanceId = i.path("instanceId").asText();
			String instanceArn = String.format("arn:aws:ec2:%s:%s:instance/%s", region, getAccountId(), instanceId);

			String cypher = "match (x:AwsEc2Instance {aws_arn:{instanceArn}}), (y:AwsAsg {aws_arn:{asgArn}}) "
					+ "merge (x)-[r:ATTACHED_TO]->(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "instanceArn",instanceArn, "asgArn",asgArn);
		}
	}

	protected void mapAsgToElb(JsonNode elbs, String asgArn, String region) {
		for (JsonNode e : elbs) { 
			String elbName = e.asText();
			String elbArn = String.format("arn:aws:elasticloadbalancing:%s:%s:loadbalancer/%s", region, getAccountId(), elbName);

			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsAsg {aws_arn:{asgArn}}) "
					+ "merge (x)-[r:ATTACHED_TO]-(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "elbArn",elbArn, "asgArn",asgArn);
		}
	}
		
	
	
}
