/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.plugin.cloud.aws.scanner;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClientImpl;

public class DefaultAWSScannerGroup extends AWSScannerGroup {

	public DefaultAWSScannerGroup(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	
		
		// will create a better way to compose this later
		//ORDER MATTERS
		
		addScanner(new AccountScanner());
	
		addScanner(new RegionScanner());
		addScanner(new AvailabilityZoneScanner());
		addScanner(new SubnetScanner());
		addScanner(new VPCScanner());
		addScanner(new SecurityGroupScanner());
		addScanner(new AMIScanner());
		addScanner(new EC2InstanceScanner());
		addScanner(new ELBScanner());
		addScanner(new LaunchConfigScanner());
		addScanner(new ASGScanner());
		addScanner(new RDSInstanceScanner());
		
		
	}

	public static  void main(String [] args) throws Exception {
		
		AWSServiceClientImpl c = new AWSServiceClientImpl(new DefaultAWSCredentialsProviderChain(),"000000000000");
	
		NeoRxClient neo4j = new NeoRxClient();
		
		AWSScannerGroup g = new DefaultAWSScannerGroup(c, neo4j);
		
		g.scan(Region.getRegion(Regions.US_WEST_2));
	}

}
