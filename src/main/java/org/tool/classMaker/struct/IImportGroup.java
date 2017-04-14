package org.tool.classMaker.struct;

import java.util.Collection;
import java.util.Set;

public interface IImportGroup {
	
	Collection<IImport> getImports(String group);
	
	Set<String> getGroup();

}
