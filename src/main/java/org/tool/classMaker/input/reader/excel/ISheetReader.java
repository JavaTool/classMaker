package org.tool.classMaker.input.reader.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.tool.classMaker.struct.IClasses;

interface ISheetReader {
	
	void read(IClasses classes, Sheet sheet) throws Exception;

}
