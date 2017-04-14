package org.tool.classMaker.input.struct;

import org.tool.classMaker.struct.IField;

public final class CMField extends CMBase implements IField {
	
	private String type;
	
	private String defaultValue;
	
	private boolean isTransient;
	
	private boolean isVolatile;
	
	private boolean needGetter;
	
	private boolean needSetter;

	public String getType() {
		return type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public boolean isVolatile() {
		return isVolatile;
	}

	public boolean isNeedGetter() {
		return needGetter;
	}

	public boolean isNeedSetter() {
		return needSetter;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setVolatile(boolean isVolatile) {
		this.isVolatile = isVolatile;
	}

	public void setNeedGetter(boolean needGetter) {
		this.needGetter = needGetter;
	}

	public void setNeedSetter(boolean needSetter) {
		this.needSetter = needSetter;
	}

}
