package org.tool.classMaker.input.reader.excel;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public final class XLSCreator implements IExcelLoaderCreator {

	@Override
	public Workbook load(InputStream inputStream) throws Exception {
		return new HSSFWorkbook(inputStream);
	}

}
