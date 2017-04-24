package org.tool.classMaker.input.reader.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ErrorReader_A extends ExcelReader {
	
	private File errorExcel;
	
	private File _textExcel;
	
	private File javaFile;
	
	private Map<Integer, ErrorInfo> existsErrors;
	
	private Set<String> existsName;
	
	private Map<Integer, ErrorInfo> errors;
	
	private List<String> lines;

	public ErrorReader_A(String config) throws Exception {
		super(config.split(";"));
		
		errorExcel = new File(config.split(";")[2]);
		_textExcel = new File(config.split(";")[3]);
		javaFile = new File(config.split(";")[4]);
		lines = Lists.newLinkedList();
		
		existsErrors = readErrorExcel();
		existsName = readErrorJava(javaFile);
		errors = Maps.newLinkedHashMap();
	}

	public ErrorReader_A(IExcelLoaderCreator excelLoaderCreator) {
		super(excelLoaderCreator, false);
	}

	@Override
	protected void read(IClasses classes, Sheet sheet, String _package, int index) throws Exception {
		CMClass cmClass = CMStructBuilder.createCMClass(0, 0);
		cmClass.setName("CommonErrorCodeConstants");
		cmClass.setStatic(true);
		cmClass.setPackage(_package);
		int count = sheet.getLastRowNum();
		List<IField> fields = cmClass.getFields();
		for (int i = 1;i <= count;i++) {
			ErrorInfo errorInfo = ErrorInfo.read(sheet.getRow(i));
			if (existsErrors.containsKey(errorInfo.errorCode)) {
				System.out.println("Exists error : " + errorInfo.errorCode);
				if (existsName.contains(errorInfo.staticName)) {
					System.out.println("Exists staticName : " + errorInfo.staticName);
				} else {
					fields.add(errorInfo.toFiled());
				}
			} else {
				if (existsName.contains(errorInfo.staticName)) {
					System.out.println("Exists staticName : " + errorInfo.staticName);
				} else {
					fields.add(errorInfo.toFiled());
					errors.put(errorInfo.errorCode, errorInfo);
				}
			}
		}
		cmClass.getAnnotations().addAll(lines);
		classes.getClasses().put(cmClass.getName(), cmClass);
		
		appendErrorExcel();
		appendTextExcel();
	}
	
	private static InputStream readFile(File file) throws Exception {
		return new FileInputStream(file);
	}
	
	private void appendErrorExcel() throws Exception {
		Workbook workbook = read(readFile(errorExcel));
		Sheet sheet = workbook.getSheetAt(0);
		int count = sheet.getLastRowNum();
		for (Map.Entry<Integer, ErrorInfo> entry : errors.entrySet()) {
			ErrorInfo errorInfo = entry.getValue();
			Row row = sheet.createRow(count++);
			row.createCell(0).setCellValue(errorInfo.errorCode);
			row.createCell(1).setCellValue(errorInfo.name);
			row.createCell(2).setCellValue(errorInfo.errType);
			row.createCell(3).setCellValue(errorInfo.info);
		}
		workbook.write(new FileOutputStream(errorExcel));
	}
	
	private void appendTextExcel() throws Exception {
		Workbook workbook = read(readFile(_textExcel));
		Sheet sheet = workbook.getSheetAt(0);
		int count = sheet.getLastRowNum();
		for (Map.Entry<Integer, ErrorInfo> entry : errors.entrySet()) {
			ErrorInfo errorInfo = entry.getValue();
			Row row = sheet.createRow(count++);
			row.createCell(0).setCellValue(errorInfo.name);
			row.createCell(1).setCellValue(errorInfo.info);
		}
		workbook.write(new FileOutputStream(_textExcel));
	}
	
	private Set<String> readErrorJava(File file) throws Exception {
		Set<String> names = Sets.newHashSet();
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(readFile(file), CHAR_SET))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().startsWith("public static final int")) {
					names.add(line.replace("public static final int", "").split("=")[0].trim());
				}
				lines.add(line);
			}
		}
		return names;
	}
	
	private Map<Integer, ErrorInfo> readErrorExcel() throws Exception {
		Map<Integer, ErrorInfo> map = Maps.newHashMap();
		Workbook workbook = read(readFile(errorExcel));
		Sheet sheet = workbook.getSheetAt(0);
		int count = sheet.getLastRowNum();
		for (int i = 6;i <= count;i++) {
			Row row = sheet.getRow(i);
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.errorCode = (int) row.getCell(0).getNumericCellValue();
			errorInfo.name = row.getCell(1).getStringCellValue();
			errorInfo.errType = (int) row.getCell(2).getNumericCellValue();
			errorInfo.info = row.getCell(3).getStringCellValue();
			map.put(errorInfo.errorCode, errorInfo);
		}
		return map;
	}
	
	private static class ErrorInfo {
		
		private int errorCode;
		
		private String name;
		
		private int errType;
		
		private String info;
		
		private String staticName;
		
		private static ErrorInfo read(Row row) {
			ErrorInfo errorInfo = new ErrorInfo();
			errorInfo.errorCode = (int) row.getCell(0).getNumericCellValue();
			errorInfo.name = row.getCell(1).getStringCellValue();
			errorInfo.errType = (int) row.getCell(2).getNumericCellValue();
			errorInfo.info = row.getCell(3).getStringCellValue();
			errorInfo.staticName = row.getCell(4).getStringCellValue();
			return errorInfo;
		}
		
		private IField toFiled() {
			CMField field = new CMField();
			field.setAccess(Access.PUBLIC);
			field.setDefaultValue(errorCode + "");
			field.setFinal(true);
			field.setName(staticName);
			field.setNote(info);
			field.setStatic(true);
			field.setType("int");
			return field;
		}
		
	}

}
