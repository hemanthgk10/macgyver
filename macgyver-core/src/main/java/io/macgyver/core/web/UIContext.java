package io.macgyver.core.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import io.macgyver.core.Kernel;

public class UIContext {
	static ObjectMapper mapper = new ObjectMapper();

	ObjectNode descriptor;

	Comparator<MenuItem> comparator = new MenuItemComparator();

	public UIContext() {
		descriptor = mapper.createObjectNode();
		ObjectNode menu = mapper.createObjectNode();
		menu.put("id", "root");
		menu.set("items", mapper.createArrayNode());
		descriptor.set("menu", menu);
	}

	public static UIContext forCurrentUser() {
		return Kernel.getApplicationContext().getBean(UIContextManager.class).forCurrentUser();
	}
	public class MenuItem {

		ObjectNode itemNode;

		public MenuItem(ObjectNode n) {
			this.itemNode = n;
		}

		public ObjectNode getModel() {
			return itemNode;
		}

		public MenuItem label(String label) {
			itemNode.put("label", label);
			return this;
		}

		public MenuItem url(String url) {
			itemNode.put("url", url);
			return this;
		}

		public String getUrl() {
			return itemNode.path("url").asText();
		}

		public String getId() {
			return itemNode.path("id").asText();
		}

		public String getLabel() {
			return itemNode.path("label").asText();
		}

		public boolean isSelected() {
			for (MenuItem item: getItems()) {
				if (item.getUrl().length()>0 && MacGyverWebContext.get().getServletRequest().getRequestURI().startsWith(item.getUrl()))  {
					return true;
				}
			}
			return false;
		}
		public List<MenuItem> getItems() {

			List<MenuItem> tmp = Lists.newArrayList();
			for (JsonNode n : Lists.newArrayList(itemNode.path("items").iterator())) {
				MenuItem m = new MenuItem((ObjectNode) n);
				tmp.add(m);
			}
			return tmp;
		}

		public void sort() {
			sort(this);
		}

		public void sort(MenuItem m) {
			List<MenuItem> tmp = getItems();
			Collections.sort(tmp, comparator);
			ArrayNode n = (ArrayNode) m.getModel().get("items");
			n.removeAll();
			for (MenuItem t : tmp) {
				n.add(t.getModel());
			}
			tmp.stream().forEach(it -> {
				it.sort();
			});

		}
	}

	public ObjectNode getModel() {
		return descriptor;
	}

	public MenuItem getRootMenu() {
		return new MenuItem((ObjectNode) descriptor.path("menu"));
	}

	public void sort() {
		getRootMenu().sort();
	}

	public MenuItem getOrCreateMenuItem(String... path) {
		JsonNode ptr = descriptor.path("menu");
		for (int i = 0; i < path.length; i++) {
			JsonNode match = null;
			for (JsonNode n : Lists.newArrayList(ptr.path("items").iterator())) {
				if (n.path("id").asText().equals(path[i])) {
					match = n;
				}
			}
			if (match != null) {
				ptr = match;
			} else {
				ObjectNode nn = mapper.createObjectNode();
				nn.put("id", path[i]);
				nn.set("items", mapper.createArrayNode());

				((ArrayNode) ptr.path("items")).add(nn);
				ptr = nn;
			}
		}
		return new MenuItem((ObjectNode) ptr);
	}

}
