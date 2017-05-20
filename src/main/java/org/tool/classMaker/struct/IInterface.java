package org.tool.classMaker.struct;

import java.util.List;

public interface IInterface extends IBase {
	
	String getPackage();
	
	String getFileType();
	
	List<IMethod> getMethods();
	
	List<IField> getFields();
	
	List<IInterface> getInterfaces();
	
	List<IInterface> getInnerClasses();
	
	IImportGroup getImportGroup();

}
