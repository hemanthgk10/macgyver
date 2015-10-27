package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class ELBScanner extends AWSServiceScanner {

	public ELBScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.of(String.format("arn:aws:elb:%s:%s:elb/%s",n.path("aws_region").asText(), n.path("aws_account").asText(), n.path("aws_loadBalancerName").asText()));
	}

	@Override
	public void scan(Region region) {
		AmazonElasticLoadBalancingClient client = new AmazonElasticLoadBalancingClient(getAWSServiceClient().getCredentialsProvider()).withRegion(region);
		DescribeLoadBalancersResult results = client.describeLoadBalancers();
		results.getLoadBalancerDescriptions().forEach(lb -> { 
			ObjectNode n = convertAwsObject(lb, region);
			
			String cypher = "merge (x:AwsElb {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp()";
		
			NeoRxClient neoRx = getNeoRxClient();
			Preconditions.checkNotNull(neoRx);
			
			neoRx.execCypher(cypher, "aws_arn",n.path("aws_arn").asText(), "props",n);
			
			mapElbRelationships(lb);
		});
	}
	
	protected void mapElbRelationships(LoadBalancerDescription lb) { 
		/**
		 * 
		 * use that to map elb to subnets and ec2Instances
		 * 
{LoadBalancerName: buscal-poc,DNSName: internal-buscal-poc-840894535.us-west-2.elb.amazonaws.com,CanonicalHostedZoneNameID: Z33MTJ483KN6FU,ListenerDescriptions: [{Listener: {Protocol: HTTP,LoadBalancerPort: 8080,InstanceProtocol: HTTP,InstancePort: 8080,},PolicyNames: []}],Policies: {AppCookieStickinessPolicies: [],LBCookieStickinessPolicies: [],OtherPolicies: []},BackendServerDescriptions: [],AvailabilityZones: [us-west-2a, us-west-2b],Subnets: [subnet-71b4fa14, subnet-729ef205],VPCId: vpc-ac7d05c9,Instances: [{InstanceId: i-ac532d6a}, {InstanceId: i-ef502e29}],HealthCheck: {Target: HTTP:8080/health-check,Interval: 30,Timeout: 5,UnhealthyThreshold: 2,HealthyThreshold: 2},SourceSecurityGroup: {OwnerAlias: 00000000000,GroupName: prod-app-poc},SecurityGroups: [sg-f753de93],CreatedTime: Wed Sep 16 12:25:47 PDT 2015,Scheme: internal}
		 */
	}

}
