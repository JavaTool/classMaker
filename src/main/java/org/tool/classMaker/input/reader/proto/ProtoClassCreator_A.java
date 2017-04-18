package org.tool.classMaker.input.reader.proto;

import java.util.List;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.reader.proto.ProtoReader_A.TypeCreator;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IField;

class ClassCreator extends TypeCreator<CMClass> {
	
	private static final CMClass SUPPER = createMessageSupper();

	@Override
	public CMClass create(IClasses classes, String name, String _package, List<String> structLines) {
		String className = Utils._ToUppercase(name);
		CMClass cmClass = CMStructBuilder.createCMClass(1, (structLines.size() << 1) + 4);
		cmClass.setAccess(Access.PUBLIC);
		cmClass.setName(className);
		cmClass.setPackage(_package);
		cmClass.getFields().add(createBuilderField(name));
		cmClass.setSupper(SUPPER);
		cmClass.getMethods().add(createConstructor1(name, className));
		cmClass.getMethods().add(createConstructor2(name, className));
		cmClass.getMethods().add(createBuildMethod(name));
		cmClass.getMethods().add(createToByteArrayMethod());
		for (String line : structLines) {
			IField field = createLineField(line);
			cmClass.getMethods().add(CMStructBuilder.createGetter(field));
			cmClass.getMethods().add(CMStructBuilder.createSetter(field));
		}
		classes.getClasses().put(name, cmClass);
		return cmClass;
	}
	
	private static IField createLineField(String line) {
		String[] infos = line.split("=")[0].replaceAll("\t", "").split(" ");
		CMField field = new CMField();
		field.setName(infos[2]);
		field.setType(infos[0].equals("repeated") ? "java.util.List<" + infos[1] + ">" : infos[1]);
		return field;
	}
	
	private static IField createBuilderField(String type) {
		CMField field = new CMField();
		field.setAccess(Access.PRIVATE);
		field.setFinal(true);
		field.setName("builder");
		field.setType(type);
		field.setNeedGetter(true);
		field.setNeedSetter(false);
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
		
		constructor.getContents().add("supper(MessageId.MI_" + name + "_VALUE);");
		constructor.getContents().add("builder = " + name + ".newBuilder();");
		
		return constructor;
	}
	
	private static CMMethod createConstructor2(String name, String className) {
		CMMethod constructor = createConstructor1(name, className);
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
