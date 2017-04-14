package org.tool.classMaker.input.reader.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.tool.classMaker.input.reader.ExcelReader;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IInterface;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class ConfReader_A extends ExcelReader {

	public ConfReader_A(IExcelLoaderCreator excelLoaderCreator, boolean readAll) {
		super(excelLoaderCreator, readAll);
	}
	
	public ConfReader_A(String config) throws Exception {
		this((IExcelLoaderCreator) Class.forName(config.split(";")[0]).newInstance(), config.split(";")[1].toLowerCase().equals("treu"));
	}

	@Override
	protected void read(IClasses classes, Sheet sheet, String _package) throws Exception {
		Row cnRow = sheet.getRow(0);
		Row enRow = sheet.getRow(1);
		Row typeRow = sheet.getRow(2);
		int count = cnRow.getLastCellNum();
		
		CMInterface inter = createCMInterface(sheet, count, _package);
		CMClass clz = createCMClass(sheet, count, _package);
		List<IInterface> interfaces = Lists.newArrayList(new IInterface[]{inter});
		clz.setInterfaces(interfaces);
		
		for (int i = 0;i < count;i++) {
			CMField field = new CMField();
			field.setAccess(Access.PRIVATE);
			field.setNeedGetter(true);
			field.setNeedSetter(true);
			List<String> annotations = ImmutableList.of();
			field.setAnnotation(annotations);
			field.setName(enRow.getCell(i).getStringCellValue());
			field.setNote(cnRow.getCell(i).getStringCellValue());
			field.setType(typeRow.getCell(i).getStringCellValue());
			
			clz.getFields().add(field);
			clz.getMethods().add(CMStructBuilder.createGetter(field));
			clz.getMethods().add(CMStructBuilder.createSetter(field));
			inter.getMethods().add(CMStructBuilder.createGetterOfInterface(field));
		}
		
		classes.getInterfaces().add(inter);
		classes.getClasses().add(clz);
	}
	
	private CMInterface createCMInterface(Sheet sheet, int methodCount, String _package) {
		CMInterface inter = CMStructBuilder.createCMInterface(methodCount);
		inter.setName("I" + sheet.getSheetName());
		inter.setNote("This is a generator file.");
		inter.setPackage(_package);
		return inter;
	}
	
	private CMClass createCMClass(Sheet sheet, int fieldCount, String _package) {
		CMClass clz = CMStructBuilder.createCMClass(fieldCount, fieldCount << 1);
		clz.setName(sheet.getSheetName());
		clz.setNote("This is a generator file.");
		clz.setPackage(_package);
		return clz;
	}

}
