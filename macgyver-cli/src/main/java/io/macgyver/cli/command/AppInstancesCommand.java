package io.macgyver.cli.command;

import java.io.IOException;

import io.macgyver.okrest.OkRestResponse;
import joptsimple.OptionParser;

public class AppInstancesCommand extends Command {

	@Override
	public String getCommandName() {
		return "app-instances";
	}

	@Override
	public String getDescription() {
		return "view app instances";
	}

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() throws IOException {
		
		OkRestResponse rr = getOkRestTarget().path("/api/cmdb/app-instances").get().execute();
		
		System.out.println(rr.response().body().string());

	}

}
