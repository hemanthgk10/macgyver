package io.macgyver.cli.command;

import java.io.IOException;

import com.google.common.reflect.ClassPath;

import joptsimple.OptionParser;

public class HelpCommand extends Command {

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws IOException {
		ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClasses(getClass().getPackage().getName()).forEach(
				it -> {
					
				
						try {
						Command tc = (Command) (Class.forName(it.getName())).newInstance();
						System.out.println(tc.getCommandName()+" - "+tc.getDescription());
						
						}
						catch (ClassNotFoundException | IllegalAccessException | InstantiationException | RuntimeException e) {
							logger.debug("could not load: {}",it.getName());
					
						}
						
					
				});
		
	}

	public String getCommandName() {
		return "help";
	}
	public String getDescription() {
		return "summary of commands";
	}
}
