package org.tool.classMaker.generator.csharp;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IImport;
import org.tool.classMaker.struct.IImportGroup;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;
import org.tool.classMaker.struct.ISubEnum;

public class GeneratorFactory implements IGeneratorFactory {

	public IGenerator<IField> createFieldGenerator() {
		return new FieldGenerator();
	}

	public IGenerator<IMethod> createMethodGenerator() {
		return new MethodGenerator();
	}

	public IGenerator<IInterface> createInterfaceGenerator() {
		return new InterfaceGenerator(this);
	}

	public IGenerator<IClass> createClassGenerator() {
		return new ClassGenerator(this);
	}

	public IGenerator<IEnum> createEnumGenerator() {
		return new EnumGenerator(this);
	}

	public IGenerator<ISubEnum> createSubEnumGenerator() {
		return new SubEnumGenerator();
	}

	@Override
	public IGenerator<IImport> createImportGenerator() {
		return new ImportGenerator();
	}

	@Override
	public IGenerator<IImportGroup> createImportGroupGenerator() {
		return new ImportGroupGenerator(createImportGenerator());
	}

}
