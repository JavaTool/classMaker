package org.tool.classMaker.input.stream;

import java.io.InputStream;

public interface IInputStreamCreator {
	
	InputStream get(String url) throws Exception;

}
