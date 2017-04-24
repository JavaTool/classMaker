package org.tool.classMaker.generator.java.append;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.generator.java.GeneratorFactory;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IImport;
import org.tool.classMaker.struct.IImportGroup;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;
import org.tool.classMaker.struct.ISubEnum;

public final class AppendGeneratorFactory implements IGeneratorFactory {
	
	private final IGeneratorFactory generatorFactory;
	
	public AppendGeneratorFactory(IGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
	
	public AppendGeneratorFactory() {
		this(new GeneratorFactory());
	}

	@Override
	public IGenerator<IField> createFieldGenerator() {
		return new FieldGenerator();
	}

	@Override
	public IGenerator<IMethod> createMethodGenerator() {
		return generatorFactory.createMethodGenerator();
	}

	@Override
	public IGenerator<IInterface> createInterfaceGenerator() {
		return generatorFactory.createInterfaceGenerator();
	}

	@Override
	public IGenerator<IClass> createClassGenerator() {
		return new ClassGenerator(this);
	}

	@Override
	public IGenerator<IEnum> createEnumGenerator() {
		return generatorFactory.createEnumGenerator();
	}

	@Override
	public IGenerator<ISubEnum> createSubEnumGenerator() {
		return generatorFactory.createSubEnumGenerator();
	}

	@Override
	public IGenerator<IImport> createImportGenerator() {
		return generatorFactory.createImportGenerator();
	}

	@Override
	public IGenerator<IImportGroup> createImportGroupGenerator() {
		return generatorFactory.createImportGroupGenerator();
	}

}
