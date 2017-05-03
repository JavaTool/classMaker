package org.tool.classMaker.input.reader.proto;

import java.util.List;
import java.util.Map;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.reader.proto.ProtoReader_A.TypeCreator;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.ISubEnum;

import com.google.common.collect.Lists;

class ClassCreator extends TypeCreator<CMClass> {
	
	private static final CMClass SUPER = createMessageSupper();

	@Override
	public CMClass create(IClasses classes, String name, List<String> structLines, Map<String, String> notes) {
		String className = Utils._ToUppercase(name);
		CMClass cmClass = CMStructBuilder.createCMClass(1, (structLines.size() << 1) + 6);
		cmClass.setAccess(Access.PUBLIC);
		cmClass.setName(className);
		cmClass.setPackage(_package + ".proto");
		cmClass.getFields().add(createBuilderField(name));
		cmClass.setSuper(SUPER);
		List<String> enumNames = transformSubEnums(classes.getEnums().get("MessageId").getSubEnums());
		cmClass.getMethods().add(createConstructorDefault(enumNames, name, className));
		cmClass.getMethods().add(createBuildFromBytes(enumNames, name, className));
		cmClass.getMethods().add(createBuildFromProto(enumNames, name, className));
		cmClass.getMethods().add(createBuildMethod(name));
		cmClass.getMethods().add(createToByteArrayMethod());
		for (String line : structLines) {
			CMField field = createLineField(line);
			boolean isRepeated = line.split("=")[0].replaceAll("\t", "").split(" ")[0].equals("repeated");
			cmClass.getMethods().add(createGetter(field, isRepeated));
			cmClass.getMethods().add(createSetter(field, isRepeated));
		}
		cmClass.getInterfaces().add(createInterface(classes, cmClass, name));
		cmClass.getMethods().add(createBuildFromInterface(enumNames, name, cmClass));
		CMImportGroup importGroup = ((CMImportGroup) cmClass.getImportGroup());
		importGroup.addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + ".*"));
		importGroup.addImport(CMStructBuilder.createCMImport(_package + ".interfaces.*"));
		if (enumNames.contains("MI_" + name)) {
			importGroup.addImport(CMStructBuilder.createCMImport(protoPackage + ".MessageIdProto.MessageId"));
		}
		classes.getClasses().put(name, cmClass);
		new RepeatedUtilsBuilder(_package).appendRepeatedUtils(classes, cmClass);
		return cmClass;
	}
	
	private CMInterface createInterface(IClasses classes, CMClass cmClass, String name) {
		CMInterface inter = CMStructBuilder.createCMInterface(cmClass.getMethods().size());
		inter.setName("I" + cmClass.getName());
		inter.setPackage(_package + ".interfaces");
		cmClass.getMethods().forEach(m -> {
			if (m.getName().startsWith("get") || m.getName().startsWith("set")) {
				CMMethod method = CMStructBuilder.createPublicCMMethod();
				method.setAbstract(true);
				method.setInterface(true);
				method.setName(m.getName());
				method.setParams(m.getParams());
				method.setReturnType(m.getReturnType());
				method.setNote(m.getNote());
				inter.getMethods().add(method);
			}
		});
		CMImportGroup importGroup = ((CMImportGroup) inter.getImportGroup());
		importGroup.addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + ".*"));
		CMMethod method = createBuildMethod(name);
		method.setAbstract(true);
		method.setInterface(true);
		inter.getMethods().add(method);
		classes.getInterfaces().put(inter.getName(), inter);
		return inter;
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
		method.getAnnotations().add("Override");
		if (isRepeated && !isDefaultJavaType(type)) {
			method.getContents().add("return RepeatedUtils.to" + type.substring(1) + "(" + build + ");");
		} else {
			method.getContents().add("return " + (isDefaultJavaType(type) ? build : (type.substring(1) + ".from(" + build + ")")) + ";");
		}
		return method;
	}
	
	static boolean isDefaultJavaType(String type) {
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
		case "void" : 
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
		method.getAnnotations().add("Override");
		if (isRepeated && !isDefaultJavaType(type)) {
			method.getContents().add("builder." + methodName + "(RepeatedUtils.from" + type.substring(1) + "(" + field.getName() + "));");
		} else {
			method.getContents().add("builder." + methodName + "(" + field.getName() + (isDefaultJavaType(type) ?  "" : ".build()") + ");");
		}
		return method;
	}
	
	private static CMField createLineField(String line) {
		String[] infos = line.split("=")[0].replaceAll("\t", "").split(" ");
		CMField field = new CMField();
		field.setName(infos[2]);
		String type = transformJavaType(infos[1]);
		type = isDefaultJavaType(type) ? type : "I" + type;
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
		cmClass.setPackage("org.tool.server.io.message");
		cmClass.setName("Message");
		return cmClass;
	}
	
	private static CMMethod createConstructorDefault(List<String> enumNames, String name, String className) {
		CMMethod constructor = CMStructBuilder.createPublicCMMethod();
		constructor.setName(className);
		constructor.setReturnType(CMMethod.CONSTRUCTOR_RETURN);
		constructor.getContents().add(enumNames.contains("MI_" + name) ? "super(MessageId.MI_" + name + "_VALUE);" : "super(0);");
		return constructor;
	}
	
	private static CMMethod createBuildFrom(String className) {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("from");
		method.setReturnType(className);
		method.setStatic(true);
		method.getContents().add(className + " ret = new " + className + "();");
		return method;
	}
	
	private static CMMethod createBuildFromBytes(List<String> enumNames, String name, String className) {
		CMMethod method = createBuildFrom(className);
		method.getContents().add("ret.builder.mergeFrom(datas);");
		method.getContents().add("return ret;");
		method.getParams().add(CMStructBuilder.createMethodParam("datas", "byte[]"));
		method.getExceptions().add(CMStructBuilder.createMethodParam("Exception", ""));
		return method;
	}
	
	private static CMMethod createBuildFromProto(List<String> enumNames, String name, String className) {
		CMMethod method = createBuildFrom(className);
		method.getContents().add("ret.builder.mergeFrom(proto);");
		method.getContents().add("return ret;");
		method.getParams().add(CMStructBuilder.createMethodParam("proto", name));
		return method;
	}
	
	private CMMethod createBuildFromInterface(List<String> enumNames, String name, CMClass cmClass) {
		CMMethod method = createBuildFrom(cmClass.getName());
		cmClass.getMethods().forEach(m -> {
			if (m.getName().startsWith("get")) {
				String fieldName = m.getName().replaceFirst("get", "");
				method.getContents().add("ret.set" + fieldName + "(vo.get" + fieldName + "());");
			}
		});
		method.getContents().add("return ret;");
		method.getParams().add(CMStructBuilder.createMethodParam("vo", "I" + cmClass.getName()));
		return method;
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
