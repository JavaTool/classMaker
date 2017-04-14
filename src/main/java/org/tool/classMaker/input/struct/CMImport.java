package org.tool.classMaker.input.struct;

import org.tool.classMaker.struct.IImport;

public final class CMImport implements IImport {
	
	private String content;
	
	private boolean isStatic;

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public boolean isStatic() {
		return isStatic;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

}
