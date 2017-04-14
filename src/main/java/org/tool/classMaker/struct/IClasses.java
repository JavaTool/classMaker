package org.tool.classMaker.struct;

import java.util.List;

public interface IClasses {
	
	List<IInterface> getInterfaces();
	
	List<IClass> getClasses();
	
	List<IEnum> getEnums();

}
