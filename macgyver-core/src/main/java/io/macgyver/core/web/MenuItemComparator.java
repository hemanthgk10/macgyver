package io.macgyver.core.web;

import java.util.Comparator;

import io.macgyver.core.web.UIContext.MenuItem;

public class MenuItemComparator implements Comparator<MenuItem> {

	public MenuItemComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(MenuItem o1, MenuItem o2) {
		
		int n1 = o1.getModel().path("order").asInt(UIContext.MenuItem.DEFAULT_ORDER);
		int n2 = o2.getModel().path("order").asInt(UIContext.MenuItem.DEFAULT_ORDER);
		

		if (n1>n2) {
			return 1;
		}
		if (n1<n2) {
			return -1;
		}


		int x = o1.getModel().path("label").asText().compareToIgnoreCase(o2.getModel().path("label").asText());
		
		return x;
	}

}
