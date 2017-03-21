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
package io.macgyver.plugin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.macgyver.plugin.cloud.aws.AWSServiceFactory;
import io.macgyver.plugin.cloud.aws.event.SNSMacGyverEventWriter;

@Configuration
public class AWSConfig {

	@Bean
	public AWSServiceFactory macAWSServiceClientFactory() {
		
		return new  AWSServiceFactory();
		
	}
	

	
	@Bean
	public SNSMacGyverEventWriter macSNSEventPublisher() {
		return new SNSMacGyverEventWriter();
	}
}
