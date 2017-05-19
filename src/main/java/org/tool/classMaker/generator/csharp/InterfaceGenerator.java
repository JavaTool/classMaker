package org.tool.classMaker.generator.csharp;

import java.util.Map;

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

import com.google.common.collect.Maps;

final class InterfaceGenerator extends TypeGenerator<IInterface> {

	public InterfaceGenerator(IGeneratorFactory generatorFactory) {
		super(generatorFactory);
	}

	@Override
	protected IGeneratorFactory createGeneratorFactory(IGeneratorFactory generatorFactory) {
		return new InterfaceGeneratorFactory(generatorFactory);
	}

	@Override
	protected String getType() {
		return "interface";
	}

	@Override
	protected String generateExtends(IInterface t) {
		return "";
	}

	@Override
	protected String getTypeInterface() {
		return "extends";
	}

	@Override
	protected String generateFront(IInterface t, String tab) {
		return "";
	}
	
	private static class InterfaceFieldGenerator implements IGenerator<IField> {
		
		private final IGenerator<IField> fieldGenerator;
		
		public InterfaceFieldGenerator(IGenerator<IField> fieldGenerator) {
			this.fieldGenerator = fieldGenerator;
		}

		public String generate(IField t, String tab) {
			return fieldGenerator.generate(t, tab).replace("public ", "").replace("static ", "").replace("final ", "");
		}
		
	}
	
	private static class InterfaceGeneratorFactory implements IGeneratorFactory {
		
		private final IGeneratorFactory generatorFactory;
		
		@SuppressWarnings("rawtypes")
		private final Map<Class, IGenerator> generators;
		
		public InterfaceGeneratorFactory(IGeneratorFactory generatorFactory) {
			this.generatorFactory = generatorFactory;
			generators = Maps.newHashMap();
		}

		public IGenerator<IField> createFieldGenerator() {
			return getGenerator(IField.class, new InterfaceFieldGenerator(generatorFactory.createFieldGenerator()));
		}
		
		private <T> IGenerator<T> getGenerator(Class<T> clz, IGenerator<T> defaultGenerator) {
			@SuppressWarnings("unchecked")
			IGenerator<T> ret = generators.get(clz);
			if (ret == null) {
				ret = defaultGenerator;
				generators.put(IField.class, ret);
			}
			return ret;
		}

		public IGenerator<IMethod> createMethodGenerator() {
			return getGenerator(IMethod.class, generatorFactory.createMethodGenerator());
		}

		public IGenerator<IInterface> createInterfaceGenerator() {
			return getGenerator(IInterface.class, new InterfaceGenerator(generatorFactory));
		}

		public IGenerator<IClass> createClassGenerator() {
			return getGenerator(IClass.class, generatorFactory.createClassGenerator());
		}

		public IGenerator<IEnum> createEnumGenerator() {
			return getGenerator(IEnum.class, generatorFactory.createEnumGenerator());
		}

		public IGenerator<ISubEnum> createSubEnumGenerator() {
			return getGenerator(ISubEnum.class, generatorFactory.createSubEnumGenerator());
		}

		@Override
		public IGenerator<IImport> createImportGenerator() {
			return getGenerator(IImport.class, generatorFactory.createImportGenerator());
		}

		@Override
		public IGenerator<IImportGroup> createImportGroupGenerator() {
			return getGenerator(IImportGroup.class, generatorFactory.createImportGroupGenerator());
		}
		
	}

}
