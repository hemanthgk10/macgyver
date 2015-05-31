package io.macgyver.plugin.artifactory;

import java.io.IOException;

import io.macgyver.okrest.OkRestTarget;

public class PropertySearchBuilder extends AbstractSearchBuilder<PropertySearchBuilder> {


	public PropertySearchBuilder(OkRestTarget target) {
		super(target,"/api/search/prop");
	}
	
	public PropertySearchBuilder property(String key, String val) {
		target = target.queryParameter(key, val);
		return this;
	}

	@Override
	public void formatRequest() throws IOException {
		
		super.formatRequest();
	}


}
