package io.macgyver.plugin.cmdb;

import io.macgyver.core.web.UIContext;
import io.macgyver.core.web.UIContextDecorator;

public class CmdbMenuDecorator implements UIContextDecorator {

	public CmdbMenuDecorator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void call(UIContext ctx) {
		ctx.getOrCreateMenuItem("inventory").label("Inventory").style("fa fa-list");
		ctx.getOrCreateMenuItem("inventory","app-instances").label("App Instances").url("/plugin/cmdb/app-instances");
	}

}
