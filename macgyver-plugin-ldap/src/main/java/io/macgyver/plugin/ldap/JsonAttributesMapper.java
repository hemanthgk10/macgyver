package io.macgyver.plugin.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.io.BaseEncoding;

public class JsonAttributesMapper implements AttributesMapper {
	final ObjectMapper MAPPER = new ObjectMapper();

	Logger logger = LoggerFactory.getLogger(JsonAttributesMapper.class);

	Predicate<Attribute> attributeExcludes = Predicates.alwaysFalse();

	public Object mapFromAttributes(Attributes attributes)
			throws NamingException {
		NamingEnumeration<Attribute> attrs = (NamingEnumeration<Attribute>) attributes
				.getAll();
		ObjectNode val = MAPPER.createObjectNode();
		while (attrs.hasMoreElements()) {
			Attribute attr = attrs.next();
			if (attributeExcludes.apply(attr)) {
				logger.debug("skipping attribute: {}", attr);
			} else {
				if (attr.size() == 1) {

					Object attrVal = attr.get();
					if (attrVal instanceof String) {

						String v = (String) attr.get();

						if (StringUtils.isAsciiPrintable(v)) {
							val.put(attr.getID(), v);

						}
					} else if (attrVal instanceof byte[]) {
						byte[] data = (byte[]) attrVal;
						if (data != null && data.length <= 128) {
							val.put(attr.getID(),
									BaseEncoding.base16().encode(data));
						}

					}

				} else {
					ArrayNode vals = MAPPER.createArrayNode();

					for (int i = 0; i < attr.size(); i++) {
						Object v = attr.get(i);
						if (v != null && (v instanceof String)) {
							vals.add(v.toString());
						}

					}
					if (vals.size() > 0) {
						val.set(attr.getID(), vals);
					}
				}
			}

		}

		return val;
	}

	public void setExcludeAttributePredicate(Predicate<Attribute> predicate) {
		Preconditions.checkNotNull(predicate);
		this.attributeExcludes = predicate;
	}

	public void addExcludeAttributePredicate(Predicate<Attribute> predicate) {
		Preconditions.checkNotNull(predicate);
		Predicates.or(this.attributeExcludes, predicate);
	}
}
