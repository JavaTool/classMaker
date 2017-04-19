package org.tool.classMaker.struct;

import java.util.Map;

public interface IClasses {
	
	Map<String, IInterface> getInterfaces();
	
	Map<String, IClass> getClasses();
	
	Map<String, IEnum> getEnums();
	
	void accpet(IClassesVisitor vistor);

}
