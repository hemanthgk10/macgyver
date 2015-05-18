package io.macgyver.plugin.ldap;

import javax.naming.directory.Attribute;

import com.google.common.base.Predicate;

public class DirectoryPredicates {

	public Predicate<Attribute> attributeNamePredicate(String regex) {
		return new AttributeNamePredicaate(regex);
	}
}
