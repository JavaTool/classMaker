package org.tool.classMaker.input.reader.proto;

import java.util.List;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.reader.proto.ProtoReader_A.TypeCreator;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.ISubEnum;

import com.google.common.collect.Lists;

class ClassCreator extends TypeCreator<CMClass> {
	
	private static final CMClass SUPER = createMessageSupper();

	@Override
	public CMClass create(IClasses classes, String name, List<String> structLines) {
		String className = Utils._ToUppercase(name);
		CMClass cmClass = CMStructBuilder.createCMClass(1, (structLines.size() << 1) + 5);
		cmClass.setAccess(Access.PUBLIC);
		cmClass.setName(className);
		cmClass.setPackage(_package);
		cmClass.getFields().add(createBuilderField(name));
		cmClass.setSuper(SUPER);
		List<String> enumNames = transformSubEnums(classes.getEnums().get("MessageId").getSubEnums());
		cmClass.getMethods().add(createConstructor1(enumNames, name, className));
		cmClass.getMethods().add(createConstructor2(enumNames, name, className));
		cmClass.getMethods().add(createConstructor3(enumNames, name, className));
		cmClass.getMethods().add(createBuildMethod(name));
		cmClass.getMethods().add(createToByteArrayMethod());
		for (String line : structLines) {
			IField field = createLineField(line);
			boolean isRepeated = line.split("=")[0].replaceAll("\t", "").split(" ")[0].equals("repeated");
			cmClass.getMethods().add(createGetter(field, isRepeated));
			cmClass.getMethods().add(createSetter(field, isRepeated));
		}
		CMImportGroup importGroup = ((CMImportGroup) cmClass.getImportGroup());
		importGroup.addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + ".*"));
		if (enumNames.contains("MI_" + name)) {
			importGroup.addImport(CMStructBuilder.createCMImport(protoPackage + ".MessageIdProto.MessageId"));
		}
		classes.getClasses().put(name, cmClass);
		appendRepeatedUtils(classes, cmClass);
		return cmClass;
	}
	
	private void appendRepeatedUtils(IClasses classes, CMClass cmClass) {
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
	
	private static List<String> transformSubEnums(List<ISubEnum> subs) {
		List<String> list = Lists.newArrayListWithCapacity(subs.size());
		subs.forEach(sub -> list.add(sub.getName()));
		return list;
	}
	
	private static CMMethod createGetter(IField field, boolean isRepeated) {
		CMMethod method = CMStructBuilder.createGetter(field);
		method.getContents().clear();
		String type = field.getType();
		type = isRepeated ? type.split("<")[1].replace(">", "") : type;
		String methodName = method.getName();
		methodName = isRepeated ? methodName + "List" : methodName;
		String build = "builder." + methodName + "()";
		if (isRepeated && !isDefaultJavaType(type)) {
//			method.getContents().add("java.util.List<" + type + "> list = com.google.common.collect.Lists.newLinkedList();");
//			method.getContents().add(build + ".forEach(vo -> list.add(new " + type + "(vo)));");
			method.getContents().add("return RepeatedUtils.to" + type + "(" + build + ");");
		} else {
			method.getContents().add("return " + (isDefaultJavaType(type) ? build : ("new " + type + "(" + build + ")")) + ";");
		}
		return method;
	}
	
	private static boolean isDefaultJavaType(String type) {
		switch (type) {
		case "String" : 
		case "int" : 
		case "Integer" : 
		case "long" : 
		case "Long" : 
		case "boolean" : 
		case "Boolean" : 
		case "float" : 
		case "Float" : 
		case "double" : 
		case "Double" : 
		case "bytes" : 
			return true;
		default : 
			return false;
		}
	}
	
	private static CMMethod createSetter(IField field, boolean isRepeated) {
		CMMethod method = CMStructBuilder.createSetter(field);
		method.getContents().clear();
		String type = field.getType();
		type = isRepeated ? type.split("<")[1].replace(">", "") : type;
		String methodName = method.getName();
		methodName = isRepeated ? methodName.replace("set", "addAll") : methodName;
		if (isRepeated && !isDefaultJavaType(type)) {
//			method.getContents().add("java.util.List<" + Utils.uppercaseTo_(type) + "> list = com.google.common.collect.Lists.newLinkedList();");
//			method.getContents().add(field.getName() + ".forEach(o -> list.add(o.build()));");
			method.getContents().add("builder." + methodName + "(RepeatedUtils.from" + type + "(" + field.getName() + "));");
		} else {
			method.getContents().add("builder." + methodName + "(" + field.getName() + (isDefaultJavaType(type) ?  "" : ".build()") + ");");
		}
		return method;
	}
	
	private static IField createLineField(String line) {
		String[] infos = line.split("=")[0].replaceAll("\t", "").split(" ");
		CMField field = new CMField();
		field.setName(infos[2]);
		String type = transformJavaType(infos[1]);
		field.setType(infos[0].equals("repeated") ? "java.util.List<" + baseTypeToPackageType(type) + ">" : type);
		field.setNote(line.contains("//") ? line.split("//")[1].trim() : field.getName());
		return field;
	}
	
	private static String baseTypeToPackageType(String type) {
		switch (type) {
		case "int" : 
			return "Integer";
		case "long" : 
			return "Long";
		case "boolean" : 
			return "Boolean";
		case "float" : 
			return "Float";
		case "double" : 
			return "Double";
		default : 
			return type;
		}
	}
	
	private static String transformJavaType(String protoType) {
		switch (protoType) {
		case "string" : 
			return "String";
		case "int32" : 
			return "int";
		case "int64" : 
			return "long";
		case "bool" : 
			return "boolean";
		case "bytes" : 
			return "com.google.protobuf.ByteString";
		default : 
			return isDefaulType(protoType) ? protoType : Utils._ToUppercase(protoType);
		}
	}
	
	private static boolean isDefaulType(String protoType) {
		switch (protoType) {
		case "string" : 
		case "int32" : 
		case "int64" : 
		case "bool" : 
		case "float" : 
		case "double" : 
			return true;
		default : 
			return false;
		}
	}
	
	private static IField createBuilderField(String type) {
		CMField field = new CMField();
		field.setAccess(Access.PRIVATE);
		field.setFinal(true);
		field.setName("builder");
		field.setType(type + ".Builder");
		field.setNeedGetter(true);
		field.setNeedSetter(false);
		field.setAnnotations(Lists.newLinkedList());
		field.setDefaultValue(type + ".newBuilder()");
		return field;
	}
	
	private static CMClass createMessageSupper() {
		CMClass cmClass = CMStructBuilder.createCMClass(0, 0);
		cmClass.setPackage("org.tool.server.io.proto");
		cmClass.setName("Message");
		return cmClass;
	}
	
	private static CMMethod createConstructor1(List<String> enumNames, String name, String className) {
		CMMethod constructor = CMStructBuilder.createPublicCMMethod();
		constructor.setName(className);
		constructor.setReturnType(CMMethod.CONSTRUCTOR_RETURN);
		constructor.getContents().add(enumNames.contains("MI_" + name) ? "super(MessageId.MI_" + name + "_VALUE);" : "super(0);");
		return constructor;
	}
	
	private static CMMethod createConstructor2(List<String> enumNames, String name, String className) {
		CMMethod constructor = createConstructor1(enumNames, name, className);
		constructor.getContents().clear();
		constructor.getContents().add("this();");
		constructor.getContents().add("builder.mergeFrom(datas);");
		constructor.getParams().add(CMStructBuilder.createMethodParam("datas", "byte[]"));
		constructor.getExceptions().add(CMStructBuilder.createMethodParam("Exception", ""));
		return constructor;
	}
	
	private static CMMethod createConstructor3(List<String> enumNames, String name, String className) {
		CMMethod constructor = createConstructor1(enumNames, name, className);
		constructor.getContents().clear();
		constructor.getContents().add("this();");
		constructor.getContents().add("builder.mergeFrom(proto);");
		constructor.getParams().add(CMStructBuilder.createMethodParam("proto", name));
		return constructor;
	}
	
	private static CMMethod createToByteArrayMethod() {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("toByteArray");
		method.setReturnType("byte[]");
		method.getContents().add("return build().toByteArray();");
		method.getAnnotations().add("Override");
		return method;
	}
	
	private static CMMethod createBuildMethod(String name) {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("build");
		method.setReturnType(name);
		method.getContents().add("return builder.build();");
		return method;
	}
	
}
