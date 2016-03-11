package io.macgyver.plugin.cmdb;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.macgyver.core.resource.Resource;
import io.macgyver.core.resource.ResourceMatcher;
import io.macgyver.core.resource.ResourceProvider;
import io.macgyver.core.service.ServiceRegistry;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.git.GitRepository;
import io.macgyver.plugin.git.GitRepositoryServiceFactory;
import io.macgyver.plugin.git.GitResourceProvider;
import rx.Observable;
import rx.functions.Func1;

public abstract class AbstractCatalogLoader {

	List<ResourceProvider> providers = Lists.newCopyOnWriteArrayList();

	@Autowired
	NeoRxClient neo4j;

	@Autowired
	ServiceRegistry serviceRegistry;

	static Logger logger = LoggerFactory.getLogger(AbstractCatalogLoader.class);

	public void addResourceProvider(ResourceProvider p) {
		providers.add(p);
	}

	public void clearResourceProviders() {
		providers.clear();
	}

	public static class ProviderMapper implements Func1<ResourceProvider, Observable<Resource>> {

		@Override
		public Observable<Resource> call(ResourceProvider t) {
			try {
				t.refresh();
				return t.allResources();
			} catch (IOException e) {
				logger.warn("", e);
			}
			return Observable.empty();
		}

	}

	public static class RegexResourceFilter implements Func1<Resource, Boolean> {

		Pattern matchingPattern;

		public RegexResourceFilter(Pattern p) {
			matchingPattern = p;
		}

		public Boolean call(Resource r) {
			try {
				Matcher m = matchingPattern.matcher(r.getPath());
				if (m.matches()) {
					return true;
				}
				return false;
			} catch (IOException | RuntimeException e) {
				logger.warn("problem filtering", e);
			}
			return false;
		}

	}

	public void discoverResourceProviders() {
		
		if (serviceRegistry!=null && serviceRegistry.getServiceDefinitions().containsKey("serviceCatalogGitRepo")) {
			GitRepository repo = serviceRegistry.get("serviceCatalogGitRepo");
			GitResourceProvider p = new GitResourceProvider(repo);
			addResourceProvider(p);
		} else {
			logger.warn("service 'serviceCatalogGitRepo' not found");
		}

	}
	public abstract void importAll();
	
	
	public final void recordParseError(String name, Resource resource, Throwable error) {
		try {
			doRecordParseError(name, resource, error);
		}
		catch (RuntimeException e) {
			logger.warn("problem recording parse error",e);
		}
	}
	public abstract void doRecordParseError(String name, Resource resource, Throwable e);
	
}
