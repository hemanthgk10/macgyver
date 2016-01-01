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

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.base.Splitter;

import io.macgyver.core.service.ServiceRegistry;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class AWSScannerService implements Runnable {

	org.slf4j.Logger logger = LoggerFactory.getLogger(AWSScannerService.class);
	@Autowired
	ServiceRegistry registy;

	@Autowired
	NeoRxClient neo4j;

	ScheduledExecutorService executor;

	public AWSScannerService() {

		executor = Executors.newSingleThreadScheduledExecutor();

	}

	@PostConstruct
	public void startup() {
		executor.scheduleWithFixedDelay(this, 30, 120, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		registy.getServiceDefinitions().values().forEach(it -> {
			try {
				String type = Strings.nullToEmpty(it.getServiceType());
				if (type.toLowerCase().equals("aws")) {
					AWSServiceClient c = registy.get(it.getName());
					
					List<String> regionList = Splitter.on(",").omitEmptyStrings().trimResults()
							.splitToList(Strings.nullToEmpty(it.getProperty("regions")));
					scan(c,regionList.toArray(new String[0]));
				}
			} catch (Exception e) {
				logger.warn("", e);
			}
		});
	}
	
	
	public void scanAccount(String account, String ... regions) {
		AWSServiceClient c = registy.getServiceByProperty("aws","accountId", account);
		scan(c,regions);
	}
	
	public AWSServiceScanner createScanner(AWSServiceClient client, NeoRxClient neo4j) {
		return new DefaultAWSScannerGroup(client, neo4j);
	}
	public void scan(AWSServiceClient c, String...regions) {
		
		if (regions==null || regions.length==0) {
			createScanner(c, neo4j).scanAllRegions();
		} else {
			for (String regionName: regions) {
				try {
					createScanner(c,neo4j).scan(regionName);
				} catch (Exception e) {
					logger.warn("problem scanning region: " + regionName, e);
				}
			}
		}
	}

}
