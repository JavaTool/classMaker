package org.tool.classMaker.struct;

import java.util.List;

public interface IBase {
	
	String getName();
	
	String getNote();
	
	List<String> getAnnotations();
	
	boolean isFinal();
	
	boolean isStatic();
	
	IAccess getAccess();

}
