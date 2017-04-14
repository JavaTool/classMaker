package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.Utils;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class CMStructBuilder {
	
	public static CMInterface createCMInterface(int methodCount) {
		CMInterface cmInterface = new CMInterface();
		cmInterface.setAccess(Access.PUBLIC);
		List<String> annotations = ImmutableList.of();
		cmInterface.setAnnotation(annotations);
		List<IField> fields = ImmutableList.of();
		cmInterface.setFields(fields);
		cmInterface.setFinal(false);
		cmInterface.setStatic(false);
		cmInterface.setImportGroup(new CMImportGroup());
		List<IInterface> innerClasses = ImmutableList.of();
		cmInterface.setInnerClasses(innerClasses);
		List<IMethod> methods = Lists.newArrayListWithCapacity(methodCount);
		cmInterface.setMethods(methods);
		List<IInterface> interfaces = ImmutableList.of();
		cmInterface.setInterfaces(interfaces);
		cmInterface.setPackage("");
		return cmInterface;
	}
	
	public static CMClass createCMClass(int fieldCount, int methodCount) {
		CMClass clz = new CMClass();
		clz.setAccess(Access.PUBLIC);
		List<String> annotations = ImmutableList.of();
		clz.setAnnotation(annotations);
		List<IField> fields = Lists.newArrayListWithCapacity(fieldCount);
		clz.setFields(fields);
		List<IInterface> innerClasses = ImmutableList.of();
		clz.setInnerClasses(innerClasses);
		List<IMethod> methods = Lists.newArrayListWithCapacity(methodCount);
		clz.setMethods(methods);
		clz.setFinal(false);
		clz.setStatic(false);
		List<IInterface> interfaces = ImmutableList.of();
		clz.setInterfaces(interfaces);
		clz.setImportGroup(new CMImportGroup());
		clz.setPackage("");
		return clz;
	}
	
	public static CMMethod createGetter(IField field) {
		CMMethod method = createPublicCMMethod();
		method.setName("get" + Utils.firstUpper(field.getName()));
		List<String> contents = Lists.newArrayList("return " + field.getName() + ";");
		method.setContents(contents);
		method.setNote("Getter of " + field.getNote());
		List<IField> params = ImmutableList.of();
		method.setParams(params);
		method.setReturnType(field.getType());
		return method;
	}
	
	public static CMMethod createGetterOfInterface(IField field) {
		CMMethod method = createGetter(field);
		method.setInterface(true);
		method.setAbstract(true);
		List<String> contents = ImmutableList.of();
		method.setContents(contents);
		return method;
	}
	
	public static CMMethod createSetter(IField field) {
		CMMethod method = createPublicCMMethod();
		method.setName("set" + Utils.firstUpper(field.getName()));
		List<String> contents = Lists.newArrayList("this." + field.getName() + " = " + field.getName() + ";");
		method.setContents(contents);
		method.setNote("Setter of " + field.getNote());
		List<IField> params = Lists.newArrayList(field);
		method.setParams(params);
		method.setReturnType(IMethod.NONE_RETURN);
		return method;
	}
	
	public static CMMethod createPublicCMMethod() {
		CMMethod method = new CMMethod();
		method.setAccess(Access.PUBLIC);
		method.setAbstract(false);
		method.setFinal(false);
		method.setStatic(false);
		method.setInterface(false);
		method.setSynchronized(false);
		List<String> annotations = ImmutableList.of();
		method.setAnnotation(annotations);
		List<IField> exceptions = ImmutableList.of();
		method.setExceptions(exceptions);
		return method;
	}

}
