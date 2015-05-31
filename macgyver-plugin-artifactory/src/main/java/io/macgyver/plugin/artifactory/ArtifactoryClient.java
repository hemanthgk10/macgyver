package io.macgyver.plugin.artifactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.macgyver.okrest.OkRestTarget;

public interface ArtifactoryClient {

	OkRestTarget getBaseTarget();
	GavcSearchBuilder gavcSearch();
	PropertySearchBuilder propertySearch();
	DateSearchBuilder dateSearch();
	
	File fetchArtifactToDir(String path, File target) throws IOException;
	File fetchArtifactToFile(String path, File out) throws IOException;
	File fetchArtifactToTempFile(String path) throws IOException;
	InputStream fetchArtifact(String path) throws IOException;
	void delete(String path) throws IOException;
}
