package io.macgyver.core.web.mvc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

import io.macgyver.core.Kernel;
import io.macgyver.core.MacGyverException;
import io.macgyver.core.auth.AuthUtil;
import io.macgyver.core.auth.MacGyverRole;
import io.macgyver.core.cluster.ClusterManager;
import io.macgyver.core.crypto.Crypto;
import io.macgyver.core.crypto.KeyStoreManager;
import io.macgyver.core.resource.Resource;
import io.macgyver.core.resource.ResourceProvider;
import io.macgyver.core.resource.provider.filesystem.FileSystemResourceProvider;
import io.macgyver.core.scheduler.DirectScriptExecutor;
import io.macgyver.core.scheduler.IgniteSchedulerService;
import io.macgyver.core.script.ExtensionResourceProvider;
import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;
import io.macgyver.core.service.ServiceRegistry;
import io.macgyver.core.web.UIContext;
import io.macgyver.core.web.UIContextDecorator;

@Component("macAdminController")
@Controller
@RequestMapping("/core/admin")
@PreAuthorize("hasAnyRole('ROLE_MACGYVER_ADMIN')")
public class AdminController implements UIContextDecorator {

	@Autowired
	Crypto crypto;

	@Autowired
	ServiceRegistry serviceRegistry;

	Logger logger = LoggerFactory.getLogger(AdminController.class);
	@Autowired
	ApplicationContext applicationContext;

	ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("/spring-beans")
	@ResponseBody
	public ModelAndView springBeans() {

		return new ModelAndView("/admin/spring-beans");

	}

	@RequestMapping("/cluster-info")
	@ResponseBody
	public ModelAndView clusterInfo() {

		ClusterManager clusterManager = Kernel.getApplicationContext().getBean(ClusterManager.class);

		Ignite ignite = Kernel.getApplicationContext().getBean(Ignite.class);
		List<ObjectNode> list = new ArrayList<>();
		for (ClusterNode clusterNode : ignite.cluster().nodes()) {

			ObjectNode n = new ObjectMapper().createObjectNode();
			n.put("id", clusterNode.id().toString());

			n.put("host", Joiner.on(", ").join(clusterNode.hostNames()));
			n.put("igniteVersion", clusterNode.version().toString());

			long heartbeat = clusterNode.metrics().getLastUpdateTime();

			n.put("master", true);
			long secsAgo = Math.max(0, System.currentTimeMillis() - heartbeat) / 1000L;
			if (heartbeat == 0) {
				n.put("lastHeartbeatSecs", "never");
			} else {
				n.put("lastHeartbeatSecs", secsAgo);
			}
			list.add(n);
		}
		return new ModelAndView("/admin/cluster-info", "list", list);

	}

	@RequestMapping("/encrypt-string")
	@ResponseBody
	public ModelAndView encryptString(HttpServletRequest request) {

		String alias = request.getParameter("alias");
		String plaintext = request.getParameter("plaintext");

		Map<String, Object> data = com.google.common.collect.Maps.newHashMap();
		if (request.getMethod().equals("POST") && !Strings.isNullOrEmpty(alias) && !Strings.isNullOrEmpty(plaintext)) {

			String ciphertext = encrypt(plaintext.trim(), alias);
			data.put("ciphertext", ciphertext);
		}
		try {

			List tmp = Collections.list(crypto.getKeyStoreManager().getKeyStore().aliases());
			data.put("aliases", tmp);
			return new ModelAndView("/admin/encrypt-string", data);
		} catch (GeneralSecurityException e) {
			throw new MacGyverException();
		}

	}

	@RequestMapping("/services")
	@ResponseBody
	public ModelAndView services() {
		Map<String, ServiceDefinition> defMap = serviceRegistry.getServiceDefinitions();

		List<JsonNode> serviceList = Lists.newArrayList();
		for (ServiceDefinition def : defMap.values()) {
			ObjectNode n = mapper.createObjectNode();
			n.put("serviceName", def.getName());
			n.put("serviceType", def.getServiceFactory().getServiceType());
			serviceList.add(n);
		}

		Map<String, Object> model = ImmutableMap.of("services", serviceList);
		return new ModelAndView("/admin/services", model);

	}

	boolean currentUserHasExecutePermissions() {
		return AuthUtil.currentUserHasRole(MacGyverRole.ROLE_MACGYVER_ADMIN);
	}

	@RequestMapping("/scripts")
	@ResponseBody

	public ModelAndView scripts(HttpServletRequest request) {
		List<JsonNode> list = Lists.newArrayList();
		try {

			String hash = request.getParameter("hash");
			if (!Strings.isNullOrEmpty(hash)) {
				try {

					Optional<Resource> r = findResourceByHash(hash);
					if (r.isPresent()) {
						scheduleImmediate(r.get());
					}

				} catch (IOException e) {
					throw new MacGyverException(e);
				}
			}

			ExtensionResourceProvider extensionProvider = Kernel.getInstance().getApplicationContext()
					.getBean(ExtensionResourceProvider.class);
			extensionProvider.refresh();
			ObjectMapper mapper = new ObjectMapper();

			for (Resource r : extensionProvider.findResources()) {
				ResourceProvider rp = r.getResourceProvider();

				if (r.getPath().startsWith("scripts/")) {
					ObjectNode n = mapper.createObjectNode();
					n.put("resource", r.getPath());
					if (rp.getClass().equals(FileSystemResourceProvider.class)) {
						n.put("providerType", "filesystem");
					} else if (rp.getClass().getName().contains("Git")) {
						n.put("providerType", "git");
					}
					n.put("hash", r.getHash());
					n.put("executeAllowed", currentUserHasExecutePermissions());
					list.add(n);

				}

			}

		} catch (IOException e) {
			throw new MacGyverException(e);
		}
		return new ModelAndView("/admin/scripts", "list", list);

	}

	public void scheduleImmediate(Resource r) {
		try {
			DirectScriptExecutor service = Kernel.getApplicationContext().getBean(Ignite.class).services()
					.serviceProxy(IgniteSchedulerService.class.getName(), DirectScriptExecutor.class, true);

			service.executeScriptImmediately(r.getPath());

		} catch (IOException e) {
			throw new MacGyverException(e);
		}
	}

	protected ExtensionResourceProvider getExtensionResourceProvider() {
		return Kernel.getInstance().getApplicationContext().getBean(ExtensionResourceProvider.class);
	}

	Optional<Resource> findResourceByHash(String hash) throws IOException {
		for (Resource r : getExtensionResourceProvider().findResources()) {
			String resourceHash = r.getHash();
			if (resourceHash.equals(hash)) {
				return Optional.of(r);
			}
		}
		return Optional.absent();
	}

	public String encrypt(String plaintext, String alias) {

		try {

			Crypto crypto = Kernel.getApplicationContext().getBean(Crypto.class);

			String val = crypto.encryptString(plaintext, alias);

			return val;

		} catch (RuntimeException | GeneralSecurityException e) {
			throw new RuntimeException();
		}
	}

	List<ServiceFactory> getServiceFactories() {
		Map<String, ServiceFactory> map = Kernel.getApplicationContext().getBeansOfType(ServiceFactory.class);
		List<ServiceFactory> list = Lists.newArrayList(map.values());

		return list;
	}

	@Override
	public void call(UIContext ctx) {
		logger.info("DECORATING: " + ctx);
		ctx.getOrCreateMenuItem("dashboard").label("Dashboard");
		ctx.getOrCreateMenuItem("dashboard", "home").label("Home").url("/home");

		if (AuthUtil.currentUserHasRole(MacGyverRole.ROLE_MACGYVER_ADMIN)) {
			ctx.getOrCreateMenuItem("admin").label("Admin");
			ctx.getOrCreateMenuItem("admin", "scripts").label("Scripts").url("/core/admin/scripts");
			ctx.getOrCreateMenuItem("admin", "cluster-info").label("Cluster").url("/core/admin/cluster-info");
			ctx.getOrCreateMenuItem("admin", "encrypt-string").label("Encrypt String")
					.url("/core/admin/encrypt-string");
			ctx.getOrCreateMenuItem("admin", "services").label("Services").url("/core/admin/services");
			ctx.getOrCreateMenuItem("admin", "spring").label("Spring").url("/core/admin/spring-beans");
			ctx.getOrCreateMenuItem("admin", "neo4j-browser").label("Neo4j").url("/browser");
		}

	}
}
