package io.macgyver.plugin.etcd;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import io.macgyver.core.service.ServiceDefinition;
import mousio.etcd4j.EtcdClient;

public class EtcdClientServiceFactory extends
		io.macgyver.core.service.ServiceFactory<EtcdClient> {

	public EtcdClientServiceFactory() {
		super("etcd");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object doCreateInstance(ServiceDefinition def) {

		Properties p = def.getProperties();

		String uriList = p.getProperty("uri", "http://127.0.0.1:4001");

		List<String> x = Splitter.on(Pattern.compile("[;,\\s]"))
				.omitEmptyStrings().splitToList(uriList);

		logger.info("etcd uri list: {}",x);
		List<URI> tmp = Lists.newArrayList();
		for (String uri : x) {
			try {

				URI u = URI.create(uri);
				tmp.add(u);
			} catch (Exception e) {
				logger.warn("problem parsing uri", e);
			}
		}

		EtcdClient c = new EtcdClient(tmp.toArray(new URI[0]));
		return c;
	}

}
