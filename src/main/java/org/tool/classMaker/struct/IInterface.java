package org.tool.classMaker.struct;

import java.util.List;

public interface IInterface extends IBase {
	
	String getPackage();
	
	List<IMethod> getMethods();
	
	List<IField> getFields();
	
	List<IInterface> getInterfaces();
	
	List<IInterface> getInnerClasses();
	
	IImportGroup getImportGroup();

}
