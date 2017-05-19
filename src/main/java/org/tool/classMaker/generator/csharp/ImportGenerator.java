package org.tool.classMaker.generator.csharp;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.struct.IImport;

final class ImportGenerator implements IGenerator<IImport> {

	@Override
	public String generate(IImport t, String tab) {
		return "using " + t.getContent();
	}

}
