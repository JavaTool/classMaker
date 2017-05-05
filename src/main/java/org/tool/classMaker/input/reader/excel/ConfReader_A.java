package org.tool.classMaker.input.reader.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.tool.classMaker.Utils;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class ConfReader_A extends ExcelReader {
	
	private final boolean supportInfoSheet;

	public ConfReader_A(IExcelLoaderCreator excelLoaderCreator, boolean readAll, boolean supportInfoSheet) {
		super(excelLoaderCreator, readAll);
		this.supportInfoSheet = supportInfoSheet;
	}
	
	public ConfReader_A(String config) throws Exception {
		this(config.split(";"));
	}
	
	private ConfReader_A(String[] configs) throws Exception {
		super(configs);
		supportInfoSheet = configs.length > 2 && configs[2].toLowerCase().equals("true");
	}

	@Override
	protected void read(IClasses classes, Sheet sheet, String _package, int index) throws Exception {
		(index == 0 && supportInfoSheet ? new SheetReader_A_Info(_package) : new SheetReader_A_Class(_package)).read(classes, sheet);
	}
	
	static String makeInterfaceName(String name) {
		return "I" + name;
	}

}

final class SheetReader_A_Info implements ISheetReader {
	
	private final String _package;
	
	public SheetReader_A_Info(String _package) {
		this._package = _package;
	}

	@Override
	public void read(IClasses classes, Sheet sheet) throws Exception {
		int oount = sheet.getLastRowNum();
		for (int i = 1;i <= oount;i++) {
			Row row = sheet.getRow(i);
			String name = row.getCell(0).getStringCellValue();
			String note = row.getCell(1).getStringCellValue();
			String[] annotations = row.getCell(2).getStringCellValue().split(";");
			String supper = row.getCell(3).getStringCellValue();
			String[] interfaces = row.getCell(4).getStringCellValue().split(";");
			
			CMInterface inter = createCMInterface(name, 0, _package);
			inter.setNote(note);
			classes.getInterfaces().put(inter.getName(), inter);
			
			CMClass clz = createCMClass(name, 0, _package, inter);
			clz.setNote(note);
			clz.setSuper(createCMClass(Utils.splitPackage(supper)[0], 0, Utils.splitPackage(supper)[1], null));
			List<String> annotationList = Lists.newArrayList(annotations);
			clz.setAnnotations(annotationList);
			List<IInterface> interfaceList = Lists.newArrayList(new IInterface[]{inter});
			for (String in : interfaces) {
				interfaceList.add(createCMInterface(Utils.splitPackage(in)[0], 0, Utils.splitPackage(in)[1]));
			}
			clz.setInterfaces(interfaceList);
			classes.getClasses().put(clz.getName(), clz);
		}
	}
	
	static CMInterface createCMInterface(String name, int methodCount, String _package) {
		CMInterface inter = CMStructBuilder.createCMInterface(methodCount);
		inter.setName(ConfReader_A.makeInterfaceName(name));
		inter.setNote("This is a generator file.");
		inter.setPackage(_package);
		return inter;
	}
	
	static CMClass createCMClass(String name, int fieldCount, String _package, IInterface inter) {
		CMClass clz = CMStructBuilder.createCMClass(fieldCount, fieldCount << 1);
		clz.setName(name);
		clz.setNote("This is a generator file.");
		clz.setPackage(_package);
		if (inter != null) {
			clz.getInterfaces().add(inter);
		}
		return clz;
	}
	
}

final class SheetReader_A_Class implements ISheetReader {
	
	private final String _package;
	
	public SheetReader_A_Class(String _package) {
		this._package = _package;
	}

	@Override
	public void read(IClasses classes, Sheet sheet) throws Exception {
		Row cnRow = sheet.getRow(0);
		Row enRow = sheet.getRow(1);
		Row typeRow = sheet.getRow(2);
		int count = cnRow.getLastCellNum();
		String name = sheet.getSheetName();
		
		CMInterface inter = (CMInterface) classes.getInterfaces().get(ConfReader_A.makeInterfaceName(name));
		if (inter == null) {
			inter = SheetReader_A_Info.createCMInterface(name, count, _package);
			classes.getInterfaces().put(inter.getName(), inter);
		} else {
			List<IMethod> interfaceMethods = Lists.newArrayListWithCapacity(count);
			inter.setMethods(interfaceMethods);
		}
		CMClass clz = (CMClass) classes.getClasses().get(name);
		if (clz == null) {
			clz = SheetReader_A_Info.createCMClass(name, count, _package, inter);
			classes.getClasses().put(clz.getName(), clz);
		} else {
			List<IField> fields = Lists.newArrayListWithCapacity(count);
			clz.setFields(fields);
			List<IMethod> classMethods = Lists.newArrayListWithCapacity((count << 1) + 1);
			clz.setMethods(classMethods);
		}
		
		for (int i = 0;i < count;i++) {
			CMField field = createCMField(cnRow, enRow, typeRow, i);
			clz.getFields().add(field);
			clz.getMethods().add(CMStructBuilder.createGetter(field));
			clz.getMethods().add(CMStructBuilder.createSetter(field));
			inter.getMethods().add(CMStructBuilder.createGetterOfInterface(field));
		}
		clz.getMethods().add(createArrayFromExcel(clz, sheet));
	}
	
	private static CMMethod createArrayFromExcel(CMClass clz, Sheet sheet) {
		((CMImportGroup) clz.getImportGroup()).addImport(CMStructBuilder.createCMImport("org.tool.server.utils.ExcelUtil"));
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("arrayFromExcel");
		method.setStatic(true);
		method.getParams().add(CMStructBuilder.createMethodParam("provider", "cg.base.io.IExcelProvider"));
		String className = clz.getName();
		method.setReturnType(className + "[]");
		method.getContents().add("org.apache.poi.ss.usermodel.Sheet sheet = provider.getWorkbook(\"" + className + "\").getSheetAt(0);");
		method.getContents().add("int count = sheet.getLastRowNum();");
		method.getContents().add(className + "[] array = new " + className + "[count - 3];");
		method.getContents().add("for (int i = 3;i <= count;i++) {");
		method.getContents().add("\torg.apache.poi.ss.usermodel.Row row = sheet.getRow(i);");
		method.getContents().add("\tarray[i] = new " + className + "();");
		Row enRow = sheet.getRow(1);
		Row typeRow = sheet.getRow(2);
		for (int i = 0;i < enRow.getLastCellNum();i++) {
			StringBuilder builder = new StringBuilder();
			builder.append("\t").append("array[i].set").append(Utils.firstUpper(enRow.getCell(i).getStringCellValue())).append("(");
			String type = typeRow.getCell(i).getStringCellValue();
			builder.append("ExcelUtil.readCellAs").append(Utils.firstUpper(type));
			builder.append("(row.getCell(").append(i).append(")));");
			method.getContents().add(builder.toString());
		}
		method.getContents().add("}");
		method.getContents().add("return array;");
		return method;
	}
	
	private static CMField createCMField(Row cnRow, Row enRow, Row typeRow, int i) {
		CMField field = new CMField();
		field.setAccess(Access.PRIVATE);
		field.setNeedGetter(true);
		field.setNeedSetter(true);
		List<String> annotations = ImmutableList.of();
		field.setAnnotations(annotations);
		field.setName(enRow.getCell(i).getStringCellValue());
		field.setNote(cnRow.getCell(i).getStringCellValue());
		field.setType(typeRow.getCell(i).getStringCellValue());
		return field;
	}
	
}
