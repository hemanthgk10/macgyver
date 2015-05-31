package io.macgyver.plugin.artifactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import io.macgyver.okrest.OkRestTarget;

public class DateSearchBuilder extends AbstractSearchBuilder<DateSearchBuilder> {

	Set<DateField> dateFields = Sets.newHashSet();

	public static enum DateField {

		LAST_MODIFIED("lastModified"), LAST_DOWNLOADED("lastDownloaded"), CREATED(
				"created");

		private final String id;

		DateField(String f) {
			id = f;
		}

		public String value() {
			return id;
		}
	}

	public DateSearchBuilder(OkRestTarget r) {
		super(r, "/api/search/dates");

	}

	public <C extends DateSearchBuilder> C from(long f) {
		target = target.queryParameter("from", "" + f);
		return (C) this;
	}

	public <C extends DateSearchBuilder> C fromDaysAgo(long d) {

		long delta = TimeUnit.DAYS.toMillis(d);

		long t0 = System.currentTimeMillis() - delta;
		return from(t0);
	}

	public <C extends DateSearchBuilder> C to(long f) {
		target = target.queryParameter("to", "" + f);
		return (C) this;
	}

	public <C extends DateSearchBuilder> C toDaysAgo(long d) {

		long delta = TimeUnit.DAYS.toMillis(d);

		long t0 = System.currentTimeMillis() - delta;
		return to(t0);
	}

	@Override
	public void formatRequest() throws IOException {

		super.formatRequest();
		
		if (dateFields.isEmpty()) {
			dateFields.add(DateField.CREATED);
		}

		String s = "";
		for (DateField d : dateFields) {
			if (s.length() > 0) {
				s = s + ",";
			}
			s = s + d.value();

		}

		target = target.queryParameter("dateFields", s);
	}

	public <C extends DateSearchBuilder> C forDateField(DateField... df) {

		for (DateField d : df) {

			dateFields.add(d);

		}

		return (C) this;
	}

}
