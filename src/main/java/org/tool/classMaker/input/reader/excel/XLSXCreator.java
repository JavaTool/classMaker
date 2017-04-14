package org.tool.classMaker.input.reader.excel;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class XLSXCreator implements IExcelLoaderCreator {

	@Override
	public Workbook load(InputStream inputStream) throws Exception {
		return new XSSFWorkbook(inputStream);
	}

}
