package org.tool.classMaker.input.reader;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.tool.classMaker.input.reader.excel.IExcelLoaderCreator;
import org.tool.classMaker.input.struct.CMClasses;
import org.tool.classMaker.struct.IClasses;

public abstract class ExcelReader implements IReader {
	
	private final IExcelLoaderCreator excelLoaderCreator;
	
	private final boolean readAll;
	
	private String _package;
	
	@Override
	public void setPackage(String _package) {
		this._package = _package;
	}

	public ExcelReader(IExcelLoaderCreator excelLoaderCreator, boolean readAll) {
		this.excelLoaderCreator = excelLoaderCreator;
		this.readAll = readAll;
	}

	@Override
	public final IClasses read(InputStream inputStream) throws Exception {
		Workbook workbook = excelLoaderCreator.load(inputStream);
		IClasses classes = new CMClasses();
		for (int i = 0;i < (readAll ? 1 : workbook.getNumberOfSheets());i++) {
			read(classes, workbook.getSheetAt(i), _package, i);
		}
		return classes;
	}
	
	protected abstract void read(IClasses classes, Sheet sheet, String _package, int index) throws Exception;

}
