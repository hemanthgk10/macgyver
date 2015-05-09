package io.macgyver.plugin.elb.a10;

import com.google.common.base.Optional;

public interface A10HAClient extends A10Client {

	public A10Client getActiveClient();
	public A10Client getStandbyClient();
	public void resetClientHAStatus();
	
}
