package org.tool.classMaker.struct;

public enum Access implements IAccess {
	
	PUBLIC("public"),
	PRIVATE("private"),
	PROTECTED("protected"),
	FRIENDLY("");
	;
	
	private final String text;
	
	private Access(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
