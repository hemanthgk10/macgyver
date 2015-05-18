package io.macgyver.plugin.ldap;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractLdapTest {

	static EmbeddedLDAPServer ldapServer;

	@BeforeClass
	public static void setupServer() throws Exception {
		ldapServer = new EmbeddedLDAPServer();
		ldapServer.setLdifResources("classpath:test-server.ldif");
		ldapServer.start();
	}

	@AfterClass
	public static void shutdownServer() {
		ldapServer.stop();
	}
}
