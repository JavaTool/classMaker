package org.tool.classMaker.generator.java;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.struct.IImport;

final class ImportGenerator implements IGenerator<IImport> {

	@Override
	public String generate(IImport t, String tab) {
		return "import " + (t.isStatic() ? "static " : "") + t.getContent();
	}

}
