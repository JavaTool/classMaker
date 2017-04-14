package org.tool.classMaker.struct;

import java.util.List;

public interface IMethod extends IBase {
	
	String NONE_RETURN = "void";
	
	String CONSTRUCTOR_RETURN = "";
	
	/**
	 * Need package.
	 * @return
	 */
	String getReturnType();
	
	boolean isSynchronized();
	
	boolean isAbstract();
	
	boolean isInterface();
	
	List<IField> getParams();
	
	List<String> getContents();
	
	List<IField> getExceptions();

}
