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
package io.macgyver.plugin.cloud.aws;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.lendingclub.mercator.aws.AWSScannerBuilder;
import org.lendingclub.mercator.aws.AllEntityScanner;
import org.lendingclub.mercator.core.Projector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.macgyver.core.Kernel;
import io.macgyver.core.MacGyverException;

public class AWSServiceClientImpl implements AWSServiceClient {

	Logger logger = LoggerFactory.getLogger(AWSServiceClientImpl.class);
	
	AWSCredentialsProvider credentialsProvider;

	String accountId;
	List<Regions> regionList = ImmutableList.of();
	public  AWSServiceClientImpl() {
		super();
	}
	
	public AWSServiceClientImpl(AWSCredentialsProvider p) {
		credentialsProvider = p;
	}
	public AWSServiceClientImpl(AWSCredentialsProvider p, String accountId) {
		credentialsProvider = p;
		this.accountId = accountId;
	}
	@Override
	public AWSCredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	@Override
	public AmazonS3Client newS3Client() {
		return createClient(AmazonS3Client.class);
	}

	@Override
	public AmazonEC2Client createEC2Client() {
		return createClient(AmazonEC2Client.class);
	}

	@Override
	public AmazonEC2Client createEC2Client(Region region) {
		AmazonEC2Client client = createEC2Client();
		assignRegion(client, region);
		return client;
	}

	@Override
	public AmazonEC2Client createEC2Client(Regions region) {
		return createEC2Client(Region.getRegion(region));
	}

	@Override
	public AmazonEC2Client createEC2Client(String name) {
		return createEC2Client(Regions.fromName(name));
	}

	void assignRegion(AmazonWebServiceClient c, String r) {
		assignRegion(c, Regions.fromName(r));
	}

	void assignRegion(AmazonWebServiceClient c, Regions r) {
		c.setRegion(Region.getRegion(r));
	}

	void assignRegion(AmazonWebServiceClient c, Region r) {
		c.setRegion(r);
	}

	@Override
	public <T extends AmazonWebServiceClient> T createClient(
			Class<? extends T> t) {

		return createClient(t,null);
	}

	
	@Override
	public <T extends AmazonWebServiceClient> T createClient(
			Class<? extends T> t, Region region) {
		try {

			Constructor<?> constructor = (Constructor<?>) t
					.getDeclaredConstructor(AWSCredentialsProvider.class);
			T client = t.cast(constructor.newInstance(getCredentialsProvider()));

			if (region != null) {
				assignRegion(client, region);
			}

			return client;
		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new MacGyverException("could not create AWS client: " + t, e);
		}
	}

	@Override
	public String getAccountId() {
		
		return accountId;
	}
	
	protected void setAccountId(String id) {
		this.accountId = id;
	}

	public AWSScannerBuilder createScannerBuilder() {
		// No need to set region or account.  Account will be determined at runtime.  Region will be selected by the caller.
		return Kernel.getApplicationContext().getBean(Projector.class).createBuilder(AWSScannerBuilder.class).withCredentials(getCredentialsProvider()).withFailOnError(false);
	}

	@Override
	public void scanRegion(String name) {
		scanRegion(Regions.fromName(name));	
	}

	@Override
	public void scanRegion(Regions region) {		
		createScannerBuilder().withRegion(region).build(AllEntityScanner.class).scan();
	}

	@Override
	public void scanRegion(Region region) {
		scanRegion(Regions.fromName(region.getName()));
	}

	public void scan() {
		for (Regions region: getConfiguredRegions()) {
			try {
				scanRegion(region);
			}
			catch (RuntimeException e) {
				logger.warn("problem scanning region: "+region,e);
			}
		}
	}
	
	/**
	 * This is public, but only on the implementation class.
	 * @param list
	 */
	
	public synchronized void setConfiguredRegions(List<Regions> list) {
		if (list==null) {
			list = Lists.newArrayList();
		}
		regionList = ImmutableList.copyOf(list);
	}
	
	@Override
	public synchronized List<Regions> getConfiguredRegions() {
		return regionList;
	}
	
	public String toString() {
		return MoreObjects.toStringHelper(this).add("account", getAccountId()).toString();
		
	}
	
}
