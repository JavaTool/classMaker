package org.tool.classMaker.input.reader.excel;

import java.util.Collection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.tool.classMaker.Utils;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImport;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class BeanReader_A extends ExcelReader {
	
	public static final String SHEET_BEAN = "Beans";
	
	public static final String SHEET_FIELD = "Fields";
	
	public static final String SHEET_ANNOTATIONS = "Annotations";

	public BeanReader_A(IExcelLoaderCreator excelLoaderCreator) {
		super(excelLoaderCreator, true);
	}
	
	public BeanReader_A(String config) throws Exception {
		this((IExcelLoaderCreator) Class.forName(config).newInstance());
	}

	@Override
	protected void read(IClasses classes, Sheet sheet, String _package, int index) throws Exception {
		String name = sheet.getSheetName();
		switch (name) {
		case SHEET_ANNOTATIONS : 
			new AnnotationSheetReader(_package).read(classes, sheet);
			break;
		case SHEET_BEAN : 
			new BeanSheetReader(_package).read(classes, sheet);
			break;
		case SHEET_FIELD : 
			new FieldSheetReader(_package).read(classes, sheet);
			break;
		}
	}

}

abstract class BaseBeanSheetReader implements ISheetReader {

	protected final String _package;
	
	public BaseBeanSheetReader(String _package) {
		this._package = _package;
	}
	
	protected static CMClass createCMClass(String name, int fieldCount, String _package) {
		CMClass clz = CMStructBuilder.createCMClass(fieldCount, fieldCount << 1);
		clz.setName(name);
		clz.setNote("This is a generator file.");
		clz.setPackage(_package);
		return clz;
	}
	
	protected static CMInterface createCMInterface(String name, int methodCount, String _package) {
		CMInterface inter = CMStructBuilder.createCMInterface(methodCount);
		inter.setName(ConfReader_A.makeInterfaceName(name));
		inter.setNote("This is a generator file.");
		inter.setPackage(_package);
		return inter;
	}
	
	private static String makeInterfaceName(String name) {
		return "I" + name;
	}
	
	protected final CMClass getCMClass(IClasses classes, String name, int fieldCount) {
		CMClass clz = (CMClass) classes.getClasses().get(name);
		if (clz == null) {
			clz = createCMClass(name, fieldCount, _package);
			classes.getClasses().put(name, clz);
			
			CMInterface inter = createCMInterface(makeInterfaceName(name), (fieldCount << 1) + 1, _package);
			classes.getClasses().put(inter.getName(), clz);
		}
		return clz;
	}
	
}

final class AnnotationSheetReader extends BaseBeanSheetReader {

	public AnnotationSheetReader(String _package) {
		super(_package);
	}

	@Override
	public void read(IClasses classes, Sheet sheet) throws Exception {
		int count = sheet.getLastRowNum();
		for (int i = 1;i < count;i++) {
			Row row = sheet.getRow(i);
			Annotation annotation = new Annotation();
			annotation.name = row.getCell(0).getStringCellValue();
			if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
				annotation._package = row.getCell(1).getStringCellValue();
			}
			annotation.clz = row.getCell(2).getStringCellValue();
			CMClass clz = getCMClass(classes, annotation.clz, 0);
			if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
				annotation.field = row.getCell(3).getStringCellValue();
				for (IField field : clz.getFields()) {
					if (field.getName().equals(annotation.field)) {
						field.getAnnotations().add(annotation._package + (annotation._package.length() > 0 ? "." : "") + annotation.name);
						break;
					}
				}
			} else {
				clz.getAnnotations().add(annotation._package + (annotation._package.length() > 0 ? "." : "") + annotation.name);
			}
		}
	}
	
	private static class Annotation {
		
		private String name;
		
		private String _package;
		
		private String clz;
		
		private String field;
		
	}
	
}

final class BeanSheetReader extends BaseBeanSheetReader {
	
	public BeanSheetReader(String _package) {
		super(_package);
	}

	@Override
	public void read(IClasses classes, Sheet sheet) throws Exception {
		Multimap<String, String> innerClassMap = HashMultimap.create();
		int count = sheet.getLastRowNum();
		for (int i = 1;i < count;i++) {
			Row row = sheet.getRow(i);
			String name = row.getCell(0).getStringCellValue();
			CMClass clz = getCMClass(classes, name, 0);
			clz.setAccess(Access.valueOf(row.getCell(7).getStringCellValue().toUpperCase()));
			clz.setFinal(row.getCell(5).getBooleanCellValue());
			if (row.getCell(8).getCellType() == Cell.CELL_TYPE_STRING) {
				CMImportGroup importGroup = new CMImportGroup();
				for (String imp : row.getCell(8).getStringCellValue().split(";")) {
					CMImport impor = new CMImport();
					impor.setStatic(false);
					impor.setContent(imp);
					importGroup.addImport(impor);
				}
				clz.setImportGroup(importGroup);
			}
			for (String text : row.getCell(3).getStringCellValue().split(";")) {
				clz.getInterfaces().add(createCMInterface(text, 0, ""));
			}
			clz.setNote(row.getCell(1).getStringCellValue());
			if (row.getCell(2) != null) {
				String supper = row.getCell(2).getStringCellValue();
				clz.setSuper(createCMClass(Utils.splitPackage(supper)[0], 0, Utils.splitPackage(supper)[1]));
			}
			clz.setStatic(row.getCell(6).getBooleanCellValue());
			if (row.getCell(4) != null) {
				innerClassMap.put(row.getCell(4).getStringCellValue(), clz.getName());
			}
		}
		
		for (String key : innerClassMap.keySet()) {
			for (String inner : innerClassMap.get(key)) {
				classes.getClasses().get(key).getInnerClasses().add(classes.getClasses().get(inner));
			}
		}
	}
	
}

final class FieldSheetReader extends BaseBeanSheetReader {

	public FieldSheetReader(String _package) {
		super(_package);
	}

	@Override
	public void read(IClasses classes, Sheet sheet) throws Exception {
		addFields(classes, readFields(classes, sheet));
	}
	
	private Multimap<String, CMField> readFields(IClasses classes, Sheet sheet) throws Exception {
		Multimap<String, CMField> fieldMap = HashMultimap.create();
		int count = sheet.getLastRowNum();
		for (int i = 1;i < count;i++) {
			Row row = sheet.getRow(i);
			CMField field = new CMField();
			field.setName(row.getCell(0).getStringCellValue());
			field.setNote(row.getCell(1).getStringCellValue());
			field.setFinal(row.getCell(3).getStringCellValue().equals("TRUE"));
			field.setStatic(row.getCell(4).getStringCellValue().equals("TRUE"));
			field.setAccess(Access.valueOf(row.getCell(5).getStringCellValue()));
			field.setType(row.getCell(6).getStringCellValue());
			if (row.getCell(7).getCellType() == Cell.CELL_TYPE_STRING) {
				field.setDefaultValue(row.getCell(7).getStringCellValue());
			}
			field.setTransient(row.getCell(8).getStringCellValue().equals("TRUE"));
			field.setVolatile(row.getCell(9).getStringCellValue().equals("TRUE"));
			field.setNeedGetter(true);
			field.setNeedSetter(true);
			fieldMap.put(row.getCell(2).getStringCellValue(), field);
		}
		return fieldMap;
	}
	
	private void addFields(IClasses classes, Multimap<String, CMField> fieldMap) {
		for (String key : fieldMap.keySet()) {
			Collection<CMField> fields = fieldMap.get(key);
			CMClass clz = getCMClass(classes, key, fields.size());
			for (IField field : clz.getFields()) {
				for (CMField readField : fields) {
					if (field.getName().equals(readField.getName())) {
						readField.setAnnotations(field.getAnnotations());
						break;
					}
				}
			}
			clz.getFields().clear();
			clz.getFields().addAll(fields);
			
			CMMethod method = CMStructBuilder.createPublicCMMethod();
			method.getAnnotations().add("Override");
			method.setReturnType("String");
			method.setAccess(Access.PUBLIC);
			method.setName("toString");
			method.getContents().add("StringBuilder builder = new StringBuilder(getClass().getName());");
			method.getContents().add("builder.append(\" [\");");
			for (CMField field : fields) {
				method.getContents().add("builder.append(\"" + field.getName() + "\" : " + field.getName() + "\" ; \");");
			}
			method.getContents().add("builder.append(\"].\");");
			clz.getMethods().add(method);
		}
	}
	
}
