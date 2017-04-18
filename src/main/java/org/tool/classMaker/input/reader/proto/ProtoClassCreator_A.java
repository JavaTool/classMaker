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
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;

import com.google.common.collect.Lists;

class ClassCreator extends TypeCreator<CMClass> {
	
	private static final CMClass SUPER = createMessageSupper();

	@Override
	public CMClass create(IClasses classes, String name, List<String> structLines) {
		String className = Utils._ToUppercase(name);
		CMClass cmClass = CMStructBuilder.createCMClass(1, (structLines.size() << 1) + 4);
		cmClass.setAccess(Access.PUBLIC);
		cmClass.setName(className);
		cmClass.setPackage(_package);
		cmClass.getFields().add(createBuilderField(name));
		cmClass.setSuper(SUPER);
		cmClass.getMethods().add(createConstructor1(name, className));
		cmClass.getMethods().add(createConstructor2(name, className));
		cmClass.getMethods().add(createBuildMethod(name));
		cmClass.getMethods().add(createToByteArrayMethod());
		for (String line : structLines) {
			IField field = createLineField(line);
			cmClass.getMethods().add(createGetter(field));
			cmClass.getMethods().add(createSetter(field));
		}
		((CMImportGroup) cmClass.getImportGroup()).addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + "." + name));
		((CMImportGroup) cmClass.getImportGroup()).addImport(CMStructBuilder.createCMImport(protoPackage + ".MessageIdProto.MessageId"));
		classes.getClasses().put(name, cmClass);
		return cmClass;
	}
	
	private static CMMethod createGetter(IField field) {
		CMMethod method = CMStructBuilder.createGetter(field);
		method.getContents().clear();
		method.getContents().add("return builder." + method.getName() + "();");
		return method;
	}
	
	private static CMMethod createSetter(IField field) {
		CMMethod method = CMStructBuilder.createSetter(field);
		method.getContents().clear();
		method.getContents().add("builder." + method.getName() + "(" + field.getName() + ");");
		return method;
	}
	
	private static IField createLineField(String line) {
		String[] infos = line.split("=")[0].replaceAll("\t", "").split(" ");
		CMField field = new CMField();
		field.setName(infos[2]);
		String type = transformJavaType(infos[1]);
		field.setType(infos[0].equals("repeated") ? "java.util.List<" + type + ">" : type);
		return field;
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
		default : 
			return protoType;
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
		return field;
	}
	
	private static CMClass createMessageSupper() {
		CMClass cmClass = CMStructBuilder.createCMClass(0, 0);
		cmClass.setPackage("org.tool.server.io.proto");
		cmClass.setName("Message");
		return cmClass;
	}
	
	private static CMMethod createConstructor1(String name, String className) {
		CMMethod constructor = CMStructBuilder.createPublicCMMethod();
		constructor.setName(className);
		constructor.setReturnType(CMMethod.CONSTRUCTOR_RETURN);
		
		constructor.getContents().add("super(MessageId.MI_" + name + "_VALUE);");
		constructor.getContents().add("builder = " + name + ".newBuilder();");
		
		return constructor;
	}
	
	private static CMMethod createConstructor2(String name, String className) {
		CMMethod constructor = createConstructor1(name, className);
		constructor.getContents().add("builder.mergeFrom(datas);");
		constructor.getParams().add(CMStructBuilder.createMethodParam("datas", "byte[]"));
		constructor.getExceptions().add(CMStructBuilder.createMethodParam("Exception", ""));
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
