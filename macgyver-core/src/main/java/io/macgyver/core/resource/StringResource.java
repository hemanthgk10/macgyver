package io.macgyver.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

public class StringResource extends Resource {

	String value = null;
	
	public StringResource(String value) {
		this(value,null);
	}
	
	public StringResource(String s, String path) {
		super(null,path);
		Preconditions
		.checkNotNull(s);
		this.value = s;
	}
	
	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(value.getBytes());
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getHash() throws IOException {
		return Hashing.sha1().hashString(value, Charset.defaultCharset()).toString();
	}

	@Override
	public String getContentAsString() throws IOException {
		return value;
	}

	

}
