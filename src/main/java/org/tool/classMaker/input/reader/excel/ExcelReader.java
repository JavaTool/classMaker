package org.tool.classMaker.input.reader.excel;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.struct.IClasses;

public abstract class ExcelReader implements IReader {
	
	private final IExcelLoaderCreator excelLoaderCreator;
	
	private final boolean readAll;
	
	private String _package;

	public ExcelReader(IExcelLoaderCreator excelLoaderCreator, boolean readAll) {
		this.excelLoaderCreator = excelLoaderCreator;
		this.readAll = readAll;
	}
	
	public ExcelReader(String[] configs) throws Exception {
		this((IExcelLoaderCreator) Class.forName(configs[0]).newInstance(), configs.length > 1 && configs[1].toLowerCase().equals("true"));
	}
	
	@Override
	public void setPackage(String _package) {
		this._package = _package;
	}

	@Override
	public final void read(IClasses classes, InputStream inputStream) throws Exception {
		Workbook workbook = excelLoaderCreator.load(inputStream);
		for (int i = 0;i < (readAll ? 1 : workbook.getNumberOfSheets());i++) {
			read(classes, workbook.getSheetAt(i), _package, i);
		}
	}
	
	protected abstract void read(IClasses classes, Sheet sheet, String _package, int index) throws Exception;

	@Deprecated
	@Override
	public void clear() {}

}
