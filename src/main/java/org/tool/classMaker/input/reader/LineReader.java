package org.tool.classMaker.input.reader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.tool.classMaker.input.struct.CMClasses;
import org.tool.classMaker.struct.IClasses;

public abstract class LineReader implements IReader {

	@Override
	public final IClasses read(InputStream inputStream) throws Exception {
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream, CHAR_SET))) {
			String line;
			IClasses classes = new CMClasses();
			while ((line = reader.readLine()) != null) {
				read(classes, line);
			}
			return classes;
		}
	}
	
	protected abstract void read(IClasses classes, String line) throws Exception;

}
