package io.macgyver.cli;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestCLI extends CLI {

	public static class MemoryConfigManager extends ConfigManager {

	
		@Override
		public ObjectNode getData() {
			return data;
		}

		@Override
		public void setConfigDir(File f) {

		}

		@Override
		public File getConfigDir() {
			throw new UnsupportedOperationException();
		}

		@Override
		public File getConfigFile() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void loadConfig() throws IOException {

		}

		@Override
		public void saveConfig() throws IOException {

		}

	}

	public TestCLI() {
		super(new MemoryConfigManager());
	
		
	}
	
	public TestCLI withConfig(String key, String val) {

		getConfigManager().getData().put(key, val);
	
		
		return this;
	}

}
