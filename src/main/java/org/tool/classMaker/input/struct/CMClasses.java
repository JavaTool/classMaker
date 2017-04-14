package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IInterface;

import com.google.common.collect.Lists;

public final class CMClasses implements IClasses {
	
	private List<IInterface> interfaces;
	
	private List<IClass> classes;
	
	private List<IEnum> enums;
	
	public CMClasses() {
		interfaces = Lists.newLinkedList();
		classes = Lists.newLinkedList();
		enums = Lists.newLinkedList();
	}

	public List<IInterface> getInterfaces() {
		return interfaces;
	}

	public List<IClass> getClasses() {
		return classes;
	}

	public List<IEnum> getEnums() {
		return enums;
	}

	public void setInterfaces(List<IInterface> interfaces) {
		this.interfaces = interfaces;
	}

	public void setClasses(List<IClass> classes) {
		this.classes = classes;
	}

	public void setEnums(List<IEnum> enums) {
		this.enums = enums;
	}

}
