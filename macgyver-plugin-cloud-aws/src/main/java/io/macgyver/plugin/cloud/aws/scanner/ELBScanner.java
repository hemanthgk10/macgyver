package io.macgyver.plugin.cloud.aws.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class ELBScanner extends AWSServiceScanner {
	ObjectMapper mapper = new ObjectMapper();

	public ELBScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.of(String.format("arn:aws:elb:%s:%s:elb/%s",n.path("aws_region").asText(), n.path("aws_account").asText(), n.path("aws_loadBalancerName").asText()));
	}

	@Override
	public void scan(Region region) {
		try { 
			AmazonElasticLoadBalancingClient client = new AmazonElasticLoadBalancingClient(getAWSServiceClient().getCredentialsProvider()).withRegion(region);
			DescribeLoadBalancersResult results = client.describeLoadBalancers();
			results.getLoadBalancerDescriptions().forEach(lb -> { 
				ObjectNode n = convertAwsObject(lb, region);
				
				String elbArn = n.path("aws_arn").asText();
				
				String cypher = "merge (x:AwsElb {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp()";
			
				NeoRxClient neoRx = getNeoRxClient();
				Preconditions.checkNotNull(neoRx);
			
				neoRx.execCypher(cypher, "aws_arn",elbArn, "props",n);
				
				mapElbRelationships(lb, elbArn, region.getName());
			});
		} catch (RuntimeException e) { 
			logger.warn("problem scanning ELBs", e);
		}
	}
	
	protected void addSecurityGroups(JsonNode securityGroups, String elbArn) { 		
		List<String> l = new ArrayList<>();
		for (JsonNode s : securityGroups) { 
			l.add(s.asText());
		}
		
		NeoRxClient neoRx = getNeoRxClient();
		Preconditions.checkNotNull(neoRx);
		
		String cypher = "match (x:AwsElb {aws_arn:{aws_arn}}) set x.aws_securityGroups={sg}";
		neoRx.execCypher(cypher, "aws_arn", elbArn, "sg",l);
	}
	
	protected void mapElbRelationships(LoadBalancerDescription lb, String elbArn, String region) { 
		JsonNode n = mapper.valueToTree(lb);
		JsonNode subnets = n.path("subnets");
		JsonNode instances = n.path("instances");
		JsonNode securityGroups = n.path("securityGroups");

		
		mapElbToSubnet(subnets, elbArn, region);
		mapElbToInstance(instances, elbArn, region);
		addSecurityGroups(securityGroups, elbArn);

	}
	
	protected void mapElbToSubnet(JsonNode subnets, String elbArn, String region) { 
		NeoRxClient neoRx = getNeoRxClient();
		Preconditions.checkNotNull(neoRx);
		
		for (JsonNode s : subnets) {
			String subnetName = s.asText();
			String subnetArn = String.format("arn:aws:subnet:%s:%s:subnet/%s", region, getAccountId(), subnetName);
			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsSubnet {aws_arn:{subnetArn}}) "
					+ "merge (x)-[:ROUTES_TO]->(y)";
			neoRx.execCypher(cypher, "elbArn",elbArn, "subnetArn",subnetArn);					
		}
	}
	
	protected void mapElbToInstance(JsonNode instances, String elbArn, String region) { 	
		NeoRxClient neoRx = getNeoRxClient();
		Preconditions.checkNotNull(neoRx);
		
		for (JsonNode i : instances) { 
			String instanceName = i.path("instanceId").asText();
			String instanceArn = String.format("arn:aws:ec2:%s:%s:instance/%s", region, getAccountId(), instanceName);
			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsEc2Instance {aws_arn:{instanceArn}}) "
					+ "merge (x)-[:DISTRIBUTES_TRAFFIC_TO]->(y)";
			neoRx.execCypher(cypher, "elbArn",elbArn, "instanceArn",instanceArn);
		}
	}
}
