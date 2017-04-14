package org.tool.classMaker.struct;

public interface IField extends IBase {
	
	/**
	 * Need package.
	 * @return
	 */
	String getType();
	
	String getDefaultValue();
	
	boolean isTransient();
	
	boolean isVolatile();
	
	boolean isNeedGetter();
	
	boolean isNeedSetter();

}
