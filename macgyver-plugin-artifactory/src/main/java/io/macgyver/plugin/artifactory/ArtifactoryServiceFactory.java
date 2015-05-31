package io.macgyver.plugin.artifactory;


import io.macgyver.core.service.ServiceDefinition;


public class ArtifactoryServiceFactory extends io.macgyver.core.service.ServiceFactory<ArtifactoryClient>{

	public ArtifactoryServiceFactory() {
		super("artifactory");

	}

	@Override
	protected ArtifactoryClient doCreateInstance(ServiceDefinition def) {
		
		String username = def.getProperties().getProperty("username", "");
		String url = def.getProperties().getProperty("url");
		String password = def.getProperties().getProperty("password","");
		ArtifactoryClientImpl c = new ArtifactoryClientImpl(url, username,password);
		
		return c;
	}
}
