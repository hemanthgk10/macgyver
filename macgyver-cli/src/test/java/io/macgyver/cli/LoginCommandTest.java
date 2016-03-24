package io.macgyver.cli;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.cli.command.LoginCommand;

public class LoginCommandTest {

	
	@Test
	public void testIt() {
		
		LoginCommand c = new LoginCommand();
		c.init(new String [] {"--user","foo"});		
		Assertions.assertThat(c.getUsername().get()).isEqualTo("foo");
		
		
		c = new LoginCommand();
		c.init(new String [] {});		
		
		

	}
}
