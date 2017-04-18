package org.tool.classMaker.generator.java;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.struct.IImport;
import org.tool.classMaker.struct.IImportGroup;

final class ImportGroupGenerator implements IGenerator<IImportGroup> {
	
	private final IGenerator<IImport> importGenerator;
	
	public ImportGroupGenerator(IGenerator<IImport> importGenerator) {
		this.importGenerator = importGenerator;
	}

	@Override
	public String generate(IImportGroup t, String tab) {
		StringBuilder staticBuilder = new StringBuilder(), unstaticBuilder = new StringBuilder();
		for (String group : t.getGroup()) {
			for (IImport imp : t.getImports(group)) {
				(imp.isStatic() ? staticBuilder : unstaticBuilder).append(importGenerator.generate(imp, tab)).append(";").append(LN);
			}
		}
		if (staticBuilder.length() > 0) {
			staticBuilder.append(LN);
		}
		if (unstaticBuilder.length() > 0) {
			unstaticBuilder.append(LN);
		}
		return staticBuilder.toString() + unstaticBuilder.toString();
	}
	
}
