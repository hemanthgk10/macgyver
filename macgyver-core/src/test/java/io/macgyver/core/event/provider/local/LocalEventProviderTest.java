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
package io.macgyver.core.event.provider.local;

import org.junit.After;
import org.junit.Test;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.provider.AbstractEventProvider;

public class LocalEventProviderTest {

	
	DistributedEventProviderProxy provider;
	LocalEventProvider localProvider;
	@org.junit.Before
	public void startProvider() {
		provider = new DistributedEventProviderProxy();
		
		localProvider = new LocalEventProvider(provider);

		localProvider.start();
	}
	
	@After
	public void stopProvider() {
		localProvider.stop();
	}
	@Test
	public void testIt() {
		
		
		provider.getObservableDistributedEvent().subscribe(it -> System.out.println(it));

		DistributedEvent evt = new DistributedEvent().topic("test");
		provider.publish(evt);
		
		
		
		
	}

}
