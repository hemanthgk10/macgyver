package io.macgyver.cli.command;

import java.io.IOException;

import io.macgyver.cli.SubCommand;

public class FooBarSubCommand extends SubCommand {

	
	@Override
	public String getCommandName() {
		String name = super.getCommandName();
		if (name.endsWith("-sub")) {
			name = name.substring(0, name.length()-"-sub".length());
		}
		return name;
	}

	@Override
	public String getMetaCommandName() {
		return "foo";
	}

	@Override
	public void execute() throws IOException {
		System.out.println("EXECUTING "+this);
		
	}

}
