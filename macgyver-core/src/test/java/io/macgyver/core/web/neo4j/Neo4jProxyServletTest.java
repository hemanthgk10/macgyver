package io.macgyver.core.web.neo4j;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class Neo4jProxyServletTest {

	Neo4jProxyServlet proxy = new Neo4jProxyServlet();


	@Test
	public void testCheckReadOnly() {
		Assertions.assertThat(proxy.isCypherReadOnly(" crEAte x")).isFalse();
		Assertions.assertThat(proxy.isCypherReadOnly("CREATE foo")).isFalse();
		Assertions.assertThat(proxy.isCypherReadOnly("blah CREATE blah")).isFalse();

		Assertions.assertThat(proxy.isCypherReadOnly("match (m) set m.name='foo' return m")).isFalse();
		Assertions.assertThat(proxy.isCypherReadOnly(" \rcrEAte\n   x")).isFalse();
		Assertions.assertThat(proxy.isCypherReadOnly(" createx x")).isTrue();
		Assertions.assertThat(proxy.isCypherReadOnly(" is_create ")).isTrue();
		Assertions.assertThat(proxy.isCypherReadOnly(" create_date ")).isTrue();
	}
}
