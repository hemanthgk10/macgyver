package io.macgyver.plugin.ldap;

import java.util.regex.Pattern;

import javax.naming.directory.Attribute;

import com.google.common.base.Predicate;

public class AttributeNamePredicaate implements Predicate<Attribute> {

	Pattern pattern;
	
	public AttributeNamePredicaate(String regex) {
		pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	}
	@Override
	public boolean apply(Attribute attr) {
		return pattern.matcher(attr.getID()).matches();
		
	}

}
