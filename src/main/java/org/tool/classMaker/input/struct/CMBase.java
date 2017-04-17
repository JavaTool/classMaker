package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IAccess;
import org.tool.classMaker.struct.IBase;

public abstract class CMBase implements IBase {
	
	private String name;
	
	private String note;
	
	private List<String> annotations;
	
	private boolean isFinal;
	
	private boolean isStatic;
	
	private IAccess access;

	public String getName() {
		return name;
	}

	public String getNote() {
		return note;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public IAccess getAccess() {
		return access;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public void setAccess(IAccess access) {
		this.access = access;
	}

}
