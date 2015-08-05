/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.core.web.vaadin.views.admin;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;

import io.macgyver.core.Kernel;
import io.macgyver.core.cluster.ClusterManager;
import io.macgyver.core.web.vaadin.IndexedJsonContainer;
import io.macgyver.core.web.vaadin.ViewConfig;
import io.macgyver.core.web.vaadin.views.StandardMacGyverView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;

@ViewConfig(menuPath = { "Admin", "Cluster Info" }, viewName = "admin/cluster")
public class ClusterView extends StandardMacGyverView {

	Table table;
	IndexedJsonContainer container;

	public ClusterView() {
		super();

		container = new IndexedJsonContainer();

		table = new Table("Cluster Info", container);
		table.addContainerProperty("id", String.class, null, "Id", null,
				Align.LEFT);
		table.addContainerProperty("host", String.class, null, "Host", null,
				Align.LEFT);

		table.addContainerProperty("master", String.class, null, "Master",
				null, Align.LEFT);

		table.addContainerProperty("lastHeartbeatSecs", String.class, null,
				"Last Heartbeat (Seconds Ago)", null, Align.LEFT);

		table.setWidth("100%");
		table.setHeight("100%");

		addComponent(table);

	}

	public void refresh() {
		container.removeAllItems();

		ClusterManager clusterManager = Kernel.getApplicationContext().getBean(
				ClusterManager.class);

		Ignite ignite = Kernel.getApplicationContext().getBean(Ignite.class);
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

			container.addJsonObject(n);

		}

	}

	@Override
	public void enter(ViewChangeEvent event) {
		refresh();

	}

}
