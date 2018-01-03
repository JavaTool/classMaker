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
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
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
		
		ListMultimap<String, Integer> fieldListMap = LinkedListMultimap.create();
		for (int i = 0;i < count;i++) {
			if (enRow.getCell(i) != null) {
				fieldListMap.put(enRow.getCell(i).getStringCellValue(), i);
			}
		}
		
		for (String fieldName : fieldListMap.keySet()) {
			List<Integer> indexList = fieldListMap.get(fieldName);
			CMField field = createCMField(cnRow, enRow, typeRow, indexList.get(0));
			if (indexList.size() > 1) {
				field.setType(field.getType() + "[]");
			}
			clz.getFields().add(field);
			clz.getMethods().add(CMStructBuilder.createGetter(field));
			clz.getMethods().add(CMStructBuilder.createSetter(field));
			inter.getMethods().add(CMStructBuilder.createGetterOfInterface(field));
		}
		
		clz.getMethods().add(createArrayFromExcel(clz, sheet, fieldListMap));
		clz.getMethods().add(createArrayFromText(clz, sheet, fieldListMap));
	}
	
	private static CMMethod createArrayFromExcel(CMClass clz, Sheet sheet, ListMultimap<String, Integer> fieldListMap) {
		((CMImportGroup) clz.getImportGroup()).addImport(CMStructBuilder.createCMImport("org.tool.server.utils.ExcelUtil"));
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("arrayFromExcel");
		method.setStatic(true);
		method.getParams().add(CMStructBuilder.createMethodParam("provider", "cg.base.io.IExcelProvider"));
		String className = clz.getName();
		method.setReturnType(className + "[]");
		method.getContents().add("org.apache.poi.ss.usermodel.Sheet sheet = provider.getWorkbook(\"" + className + "\").getSheetAt(0);");
		method.getContents().add("if (sheet == null) {");
		method.getContents().add("\treturn null;");
		method.getContents().add("}");
		method.getContents().add("int count = sheet.getLastRowNum();");
		method.getContents().add(className + "[] array = new " + className + "[count - 2];");
		method.getContents().add("for (int i = 3, index = 0;i <= count;i++, index++) {");
		method.getContents().add("\torg.apache.poi.ss.usermodel.Row row = sheet.getRow(i);");
		method.getContents().add("\tarray[index] = new " + className + "();");
		Row typeRow = sheet.getRow(2);
		StringBuilder builder = new StringBuilder();
		for (String fieldName : fieldListMap.keySet()) {
			builder.setLength(0);
			List<Integer> indexList = fieldListMap.get(fieldName);
			builder.append("\t").append("array[index].set");
			builder.append(Utils.firstUpper(fieldName)).append("(");
			String type = typeRow.getCell(indexList.get(0)).getStringCellValue();
			if (indexList.size() > 1) {
				builder.append("new ").append(type).append("[]{");
				for (Integer index : indexList) {
					builder.append("ExcelUtil.readCellAs").append(Utils.firstUpper(type));
					builder.append("(row.getCell(").append(index).append(")), ");
				}
				builder.setLength(builder.length() - 2);
				builder.append("});");
			} else {
				builder.append("ExcelUtil.readCellAs").append(Utils.firstUpper(type));
				builder.append("(row.getCell(").append(indexList.get(0)).append(")));");
			}
			method.getContents().add(builder.toString());
		}
		method.getContents().add("}");
		method.getContents().add("sheet = null;");
		method.getContents().add("return array;");
		return method;
	}
	
	private static CMMethod createArrayFromText(CMClass clz, Sheet sheet, ListMultimap<String, Integer> fieldListMap) {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("arrayFromText");
		method.setStatic(true);
		method.getParams().add(CMStructBuilder.createMethodParam("provider", "cg.base.io.ITextProvider"));
		String className = clz.getName();
		method.setReturnType(className + "[]");
		method.getContents().add("String[] texts = provider.getTextResource(\"" + className.replaceAll("Conf", "") + "\");");
		method.getContents().add("if (texts == null) {");
		method.getContents().add("\treturn null;");
		method.getContents().add("}");
		method.getContents().add("int count = texts.length;");
		method.getContents().add(className + "[] array = new " + className + "[count];");
		method.getContents().add("for (int i = 0;i < count;i++) {");
		method.getContents().add("\tString[] infos = texts[i].split(\"\\t\", -2);");
		method.getContents().add("\tarray[i] = new " + className + "();");
		Row typeRow = sheet.getRow(2);
		StringBuilder builder = new StringBuilder();
		boolean needUtil = false;
		for (String fieldName : fieldListMap.keySet()) {
			builder.setLength(0);
			List<Integer> indexList = fieldListMap.get(fieldName);
			builder.append("\t").append("array[i].set");
			builder.append(Utils.firstUpper(fieldName)).append("(");
			String type = typeRow.getCell(indexList.get(0)).getStringCellValue();
			String methodName = type.equals("String") ? "" : ("MathUtil.stringTo" + Utils.firstUpper(type));
			if (indexList.size() > 1) {
				builder.append("new ").append(type).append("[]{");
				for (Integer index : indexList) {
					builder.append(methodName);
					builder.append("(infos[").append(index).append("]), ");
				}
				builder.setLength(builder.length() - 2);
				builder.append("});");
			} else {
				builder.append(methodName);
				builder.append("(infos[").append(indexList.get(0)).append("]));");
			}
			if (methodName.length() > 0) {
				needUtil = true;
			}
			method.getContents().add(builder.toString());
		}
		if (needUtil) {
			((CMImportGroup) clz.getImportGroup()).addImport(CMStructBuilder.createCMImport("cg.base.util.MathUtil"));
		}
		method.getContents().add("}");
		method.getContents().add("texts = null;");
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
