package org.tool.classMaker.generator.simple;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.ISubEnum;

final class EnumGenerator extends TypeGenerator<IEnum> {

	public EnumGenerator(IGeneratorFactory generatorFactory) {
		super(generatorFactory);
	}

	@Override
	protected IGeneratorFactory createGeneratorFactory(IGeneratorFactory generatorFactory) {
		return generatorFactory;
	}

	@Override
	protected String getType() {
		return "enum";
	}

	@Override
	protected String generateExtends(IEnum t) {
		return "";
	}

	@Override
	protected String getTypeInterface() {
		return "implements";
	}

	@Override
	protected String generateFront(IEnum t, final String tab) {
		StringBuilder builder = new StringBuilder(LN);
		IGenerator<ISubEnum> subEnumGenerator = generatorFactory.createSubEnumGenerator();
		for (ISubEnum subEnum : t.getSubEnums()) {
			builder.append(subEnumGenerator.generate(subEnum, tab)).append(",").append(BLANK).append(LN);
		}
		builder.append(tab).append(";").append(LN);
		return builder.toString();
	}

}
