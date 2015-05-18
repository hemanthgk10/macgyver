package io.macgyver.plugin.ldap;

import io.macgyver.plugin.ldap.JsonAttributesMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.directory.server.core.partition.Partition;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.server.ApacheDSContainer;

import com.fasterxml.jackson.databind.JsonNode;

public class EmbeddedLDAPServer {

	ApacheDSContainer server;
	int port;
	LdapContextSource contextSource;
	String base = "dc=macgyver,dc=io";
	String ldif = "classpath:test-server.ldif";
	
	public EmbeddedLDAPServer() {
		port = findAvailablePort();
	}

	public void setLdifResources(String s) {
		this.ldif = s;
	}
	private int findAvailablePort() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
			return serverSocket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void start() throws Exception {
		server = new ApacheDSContainer(base,
				ldif);

		server.setPort(port);

		server.afterPropertiesSet();
	}

	public void stop() {
		for (Partition p : server.getService().getPartitions()) {
			try {
				server.getService().removePartition(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		server.stop();
	}

	public synchronized LdapContextSource getLdapContextSource() {
		if (contextSource!=null) {
			return contextSource;
		}
		try {
			LdapContextSource lcs = new LdapContextSource();
			lcs.setUrl("ldap://localhost:" + getPort());
			lcs.setBase(base);
			lcs.afterPropertiesSet();
			contextSource = lcs;
			return contextSource;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public LdapTemplate getLdapTemplate() {
		return new LdapTemplate(getLdapContextSource());
	}

	public static void main(String[] args) throws Exception {

		EmbeddedLDAPServer s = new EmbeddedLDAPServer();
		s.setLdifResources("classpath:test-server.ldif");
		s.start();

		
		LdapTemplate lt = s.getLdapTemplate();

		List<JsonNode> x = lt.search("", "(objectClass=person)", SearchControls.SUBTREE_SCOPE,
				new JsonAttributesMapper());
	
		for (JsonNode n: x) {
			System.out.println(n);
		}

		s.stop();

	}
}