package org.tool.classMaker.input.struct;

import org.tool.classMaker.struct.IField;

public final class CMField extends CMBase implements IField {
	
	private String type;
	
	private String defaultValue;
	
	private boolean isTransient;
	
	private boolean isVolatile;
	
	private boolean needGetter;
	
	private boolean needSetter;

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isTransient() {
		return isTransient;
	}

	@Override
	public boolean isVolatile() {
		return isVolatile;
	}

	@Override
	public boolean isNeedGetter() {
		return needGetter;
	}

	@Override
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

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

}
