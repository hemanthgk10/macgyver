package io.macgyver.cli.command;

import java.io.IOException;

import io.macgyver.okrest.OkRestResponse;
import joptsimple.OptionParser;

public class AppDefinitionsCommand extends Command {

	@Override
	public String getCommandName() {
		return "app-definitions";
	}

	@Override
	public String getDescription() {
		return "view app definitions";
	}

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() throws IOException {
		
		OkRestResponse rr = getOkRestTarget().path("/api/cmdb/app-definitions").get().execute();
		
		System.out.println(rr.response().body().string());

	}

}
