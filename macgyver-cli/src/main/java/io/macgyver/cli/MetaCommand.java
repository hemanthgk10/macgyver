package io.macgyver.cli;

import java.io.IOException;

public abstract class MetaCommand extends Command {

	@Override
	public void execute() throws IOException {

		getCLI().getCommander().getParameters().forEach(it -> {
			System.out.println("ARGS: {}"+ it);
		});
	}

}
