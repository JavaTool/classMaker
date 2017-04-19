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

final class RepeatedUtilsBuilder {
	
	private String _package;
	
	public RepeatedUtilsBuilder(String _package) {
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
		String returnType = "List<" + Utils.uppercaseTo_(className) + ">";
		method.setReturnType(returnType);
		CMField param = new CMField();
		param.setName("list");
		param.setType("List<" + className + ">");
		method.setParams(Lists.newArrayList(param));
		method.getContents().add(returnType + " ret = Lists.newLinkedList();");
		method.getContents().add("list.forEach(o -> ret.add(o.build()));");
		method.getContents().add("return ret;");
		return method;
	}
	
	private static CMMethod createToCMMethod(CMClass cmClass) {
		String className = cmClass.getName();
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setStatic(true);
		method.setName("to" + className);
		String returnType = "List<" + className + ">";
		method.setReturnType(returnType);
		CMField param = new CMField();
		param.setName("list");
		param.setType("List<" + Utils.uppercaseTo_(className) + ">");
		method.setParams(Lists.newArrayList(param));
		method.getContents().add(returnType + " ret = Lists.newLinkedList();");
		method.getContents().add("list.forEach(o -> ret.add(new " + className + "(o)));");
		method.getContents().add("return ret;");
		return method;
	}
	
	private IClass createRepeatedUtils() {
		CMClass utilsClass = CMStructBuilder.createCMClass(0, 0);
		utilsClass.setAccess(Access.PUBLIC);
		utilsClass.setFinal(true);
		utilsClass.setName("RepeatedUtils");
		utilsClass.setPackage(_package);
		
		CMImportGroup importGroup = ((CMImportGroup) utilsClass.getImportGroup());
		importGroup.addImport(CMStructBuilder.createCMImport("java.util.List"));
		importGroup.addImport(CMStructBuilder.createCMImport("com.google.common.collect.Lists"));
		return utilsClass;
	}

}
