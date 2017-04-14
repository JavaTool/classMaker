package org.tool.classMaker.input.reader.excel;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

public interface IExcelLoaderCreator {
	
	Workbook load(InputStream inputStream) throws Exception;

}
