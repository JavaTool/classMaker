package org.tool.classMaker.input.reader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.tool.classMaker.struct.IClasses;

public abstract class LineReader implements IReader {

	@Override
	public final void read(IClasses classes, InputStream inputStream) throws Exception {
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream, CHAR_SET))) {
			String line;
			while ((line = reader.readLine()) != null) {
				read(classes, line);
			}
			readFinish(classes);
		}
	}
	
	protected abstract void read(IClasses classes, String line) throws Exception;
	
	protected abstract void readFinish(IClasses classes);

}
