package org.tool.classMaker.input.stream;

import java.io.FileInputStream;
import java.io.InputStream;

public final class FileStream implements IInputStreamCreator {

	@Override
	public InputStream get(String url) throws Exception {
		return new FileInputStream(url);
	}

}
