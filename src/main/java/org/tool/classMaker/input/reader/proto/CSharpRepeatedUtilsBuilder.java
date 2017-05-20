package org.tool.classMaker.input.reader.proto;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;

import com.google.common.collect.Lists;

final class CSharpRepeatedUtilsBuilder {
	
	private String _package;
	
	public CSharpRepeatedUtilsBuilder(String _package) {
		this._package = _package;
	}
	
	public void appendRepeatedUtils(IClasses classes, CMClass cmClass) {
		String className = "RepeatedUtils";
		IClass utilsClass = classes.getClasses().get(className);
		if (utilsClass == null) {
			utilsClass = createRepeatedUtils();
			classes.getClasses().put(className, utilsClass);
		}

		utilsClass.getMethods().add(createFromCMMethod(cmClass));
		utilsClass.getMethods().add(createToCMMethod(cmClass));
	}
	
	private static CMMethod createFromCMMethod(CMClass cmClass) {
		String className = cmClass.getName();
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setStatic(true);
		method.setName("from" + className);
		String returnType = "IList<" + Utils.uppercaseTo_(className) + ">";
		method.setReturnType(returnType);
		CMField param = new CMField();
		param.setName("list");
		param.setType("IList<I" + className + ">");
		method.setParams(Lists.newArrayList(param));
		method.getContents().add(returnType + " ret = new List<" + Utils.uppercaseTo_(className) + ">();");
		method.getContents().add("foreach (" + className + " o in list) {");
		method.getContents().add("\tret.Add(o.build());");
		method.getContents().add("}");
		method.getContents().add("return ret;");
		return method;
	}
	
	private static CMMethod createToCMMethod(CMClass cmClass) {
		String className = cmClass.getName();
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setStatic(true);
		method.setName("to" + className);
		String returnType = "IList<I" + className + ">";
		method.setReturnType(returnType);
		CMField param = new CMField();
		param.setName("list");
		param.setType("IList<" + Utils.uppercaseTo_(className) + ">");
		method.setParams(Lists.newArrayList(param));
		method.getContents().add(returnType + " ret = new List<I" + className + ">();");
		method.getContents().add("foreach (" + Utils.uppercaseTo_(className) + " o in list) {");
		method.getContents().add("\tret.Add(" + className + ".from(o));");
		method.getContents().add("}");
		method.getContents().add("return ret;");
		return method;
	}
	
	private IClass createRepeatedUtils() {
		CMClass utilsClass = CMStructBuilder.createCMClass(0, 0);
		utilsClass.setAccess(Access.PUBLIC);
		utilsClass.setName("RepeatedUtils");
		utilsClass.setPackage(_package + ".proto");
		utilsClass.setFileType("cs");
		
		CMImportGroup importGroup = ((CMImportGroup) utilsClass.getImportGroup());
		importGroup.addImport(CMStructBuilder.createCMImport("System.Collections.Generic"));
		importGroup.addImport(CMStructBuilder.createCMImport(_package + ".interfaces"));
		importGroup.addImport(CMStructBuilder.createCMImport("cg.basis.io.proto"));
		return utilsClass;
	}

}
