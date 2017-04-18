package org.tool.classMaker.input.stream;

import java.io.InputStream;

public interface IInputStreamProvider {
	
	InputStream provide() throws Exception;
	
	boolean hasNext();

}
