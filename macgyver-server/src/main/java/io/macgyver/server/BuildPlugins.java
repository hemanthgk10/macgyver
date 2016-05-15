package io.macgyver.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class BuildPlugins {

	public static void main(String[] args) throws IOException {

		ObjectMapper m = new ObjectMapper();

		Properties p = new Properties();
		p.load(new FileReader("../gradle.properties"));

		String version = p.getProperty("MACGYVER_VERSION");

		ObjectNode n = m.createObjectNode();
		n.put("version", version);

		ArrayNode plugins = m.createArrayNode();
		n.set("plugins", plugins);
		
		Pattern pattern = Pattern.compile(".*'(.+)'.*");
		File settingsGradle = new File("../settings.gradle");
		List<String> candidates = Lists.newArrayList();
		
		Files.readLines(settingsGradle, Charsets.UTF_8).forEach(it->{
			Matcher matcher = pattern.matcher(it);
			if (matcher.matches()) {
				candidates.add(matcher.group(1));
			}	
		});
		
		candidates.removeIf(it -> !it.startsWith("macgyver-plugin"));
		
		ObjectNode pi = m.createObjectNode();
		pi.put("name","macgyver-core");
		pi.put("enabled", true);
		plugins.add(pi);
		candidates.forEach(it-> {
				ObjectNode pluginNode = m.createObjectNode();
				pluginNode.put("name", it);
				pluginNode.put("enabled", false);
				plugins.add(pluginNode);
			
		});
		
		File targetPlugins = new File("./src/main/resources/templates/plugins.json");
		System.out.println(m.writerWithDefaultPrettyPrinter().writeValueAsString(n));
		m.writerWithDefaultPrettyPrinter().writeValue(targetPlugins, n);
	}
}
