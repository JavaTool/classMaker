package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IImportGroup;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;

public class CMInterface extends CMBase implements IInterface {
	
	private String _package;
	
	private List<IMethod> methods;
	
	private List<IField> fields;
	
	private List<IInterface> interfaces;
	
	private List<IInterface> innerClasses;
	
	private IImportGroup importGroup;

	public final String getPackage() {
		return _package;
	}

	public final List<IMethod> getMethods() {
		return methods;
	}

	public final List<IField> getFields() {
		return fields;
	}

	public final List<IInterface> getInterfaces() {
		return interfaces;
	}

	public final List<IInterface> getInnerClasses() {
		return innerClasses;
	}

	public final void setPackage(String _package) {
		this._package = _package;
	}

	public final void setMethods(List<IMethod> methods) {
		this.methods = methods;
	}

	public final void setFields(List<IField> fields) {
		this.fields = fields;
	}

	public final void setInterfaces(List<IInterface> interfaces) {
		this.interfaces = interfaces;
	}

	public final void setInnerClasses(List<IInterface> innerClasses) {
		this.innerClasses = innerClasses;
	}

	@Override
	public IImportGroup getImportGroup() {
		return importGroup;
	}

	public void setImportGroup(IImportGroup importGroup) {
		this.importGroup = importGroup;
	}

}
