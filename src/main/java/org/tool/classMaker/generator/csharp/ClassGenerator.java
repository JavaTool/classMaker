package org.tool.classMaker.generator.csharp;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IClass;

final class ClassGenerator extends TypeGenerator<IClass> {

	public ClassGenerator(IGeneratorFactory generatorFactory) {
		super(generatorFactory);
	}

	@Override
	protected IGeneratorFactory createGeneratorFactory(IGeneratorFactory generatorFactory) {
		return generatorFactory;
	}

	@Override
	protected String getType() {
		return "class";
	}

	@Override
	protected String generateExtends(IClass t) {
		String supper = t.getSupper() == null ? null : t.getSupper().getPackage().replace("base", "basis") + "." + t.getSupper().getName();
		return supper == null ? "" :": " + supper;
	}

	@Override
	protected String getTypeInterface() {
		return ":";
	}

	@Override
	protected String generateFront(IClass t, String tab) {
		return "";
	}

}
