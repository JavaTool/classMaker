package org.tool.classMaker.generator.java.append;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IField;

final class ClassGenerator implements IGenerator<IClass> {
	
	private final IGeneratorFactory generatorFactory;
	
	public ClassGenerator(IGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	@Override
	public String generate(IClass t, String tab) {
		StringBuilder builder = new StringBuilder();
		IGenerator<IField> fieldGenerator = generatorFactory.createFieldGenerator();
		t.getAnnotations().forEach(line -> {
			if (line.startsWith("}")) {
				t.getFields().forEach(field -> builder.append(fieldGenerator.generate(field, "\t")));
			}
			builder.append(LN);
			builder.append(line).append(LN);
		});
		return builder.toString();
	}

}
