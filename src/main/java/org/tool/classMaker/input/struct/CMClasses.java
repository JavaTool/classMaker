package org.tool.classMaker.input.struct;

import java.util.Map;

import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IClassesVisitor;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IInterface;

import com.google.common.collect.Maps;

public final class CMClasses implements IClasses {
	
	private Map<String, IInterface> interfaces;
	
	private Map<String, IClass> classes;
	
	private Map<String, IEnum> enums;
	
	public CMClasses() {
		interfaces = Maps.newHashMap();
		classes = Maps.newHashMap();
		enums = Maps.newHashMap();
	}

	public Map<String, IInterface> getInterfaces() {
		return interfaces;
	}

	public Map<String, IClass> getClasses() {
		return classes;
	}

	public Map<String, IEnum> getEnums() {
		return enums;
	}

	public void setInterfaces(Map<String, IInterface> interfaces) {
		this.interfaces = interfaces;
	}

	public void setClasses(Map<String, IClass> classes) {
		this.classes = classes;
	}

	public void setEnums(Map<String, IEnum> enums) {
		this.enums = enums;
	}

	@Override
	public void accpet(IClassesVisitor visitor) {
		visitor.visit(this);
	}

}
