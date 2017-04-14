package org.tool.classMaker.input.reader;

import java.io.InputStream;

import org.tool.classMaker.struct.IClasses;

public interface IReader {
	
	String CHAR_SET = "utf-8";
	
	IClasses read(InputStream inputStream) throws Exception;
	
	void setPackage(String _package);

}
