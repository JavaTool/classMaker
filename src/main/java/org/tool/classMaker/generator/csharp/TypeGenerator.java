package org.tool.classMaker.generator.csharp;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;

public abstract class TypeGenerator<T extends IInterface> extends AppendStaticFinalGenerator<T> {
	
	protected final IGeneratorFactory generatorFactory;
	
	public TypeGenerator(IGeneratorFactory generatorFactory) {
		this.generatorFactory = createGeneratorFactory(generatorFactory);
	}
	
	protected abstract IGeneratorFactory createGeneratorFactory(IGeneratorFactory generatorFactory);

	@Override
	protected final String generateBody(final T t, final String tab, final String base) {
		StringBuilder builder = new StringBuilder();
		appendContentHead(builder, t, tab, base);
		appendContentBody(builder, t, tab);
		appendContentEnd(builder, t, tab);
		return builder.toString();
	}
	
	private void appendContentHead(StringBuilder builder, T t, String tab, String base) {
		builder.append(LN);
		builder.append(generatorFactory.createImportGroupGenerator().generate(t.getImportGroup(), tab));
		if (t.getPackage().length() > 0) {
			builder.append("namespace").append(BLANK).append(t.getPackage().replace("base", "basis")).append(LN);
			builder.append("{").append(LN);
		}
		builder.append(base);
		String extendsText = generateExtends(t);
		builder.append(getType()).append(BLANK).append(t.getName()).append(BLANK).append(extendsText).append(extendsText.length() > 0 ? BLANK : "");
		appendInterface(builder, t);
		builder.append(LN);
		builder.append("\t").append("{").append(LN);
	}
	
	private void appendContentBody(StringBuilder builder, T t, String tab) {
		builder.append(generateFront(t, tab));
		appendFields(builder, t, tab);
		appendMethods(builder, t, tab);
		appendInners(builder, t, tab);
	}
	
	private void appendContentEnd(StringBuilder builder, T t, String tab) {
		builder.append(LN);
		builder.append(tab).append("}").append(LN);
		builder.append(LN);
		builder.append("}").append(LN);
	}
	
	private void appendInterface(StringBuilder builder, T t) {
		if (t.getInterfaces().size() > 0) {
			if (generateExtends(t).length() == 0) {
				builder.append(getTypeInterface()).append(BLANK);
			} else {
				builder.append(",").append(BLANK);
			}
		}
		for (IInterface inter : t.getInterfaces()) {
			builder.append(inter.getPackage().replace("base", "basis")).append(inter.getPackage().length() > 0 ? "." : "").append(inter.getName()).append(",").append(BLANK);
		}
		if (t.getInterfaces().size() > 0) {
			builder.setLength(builder.length() - 2);
			builder.append(BLANK);
		}
	}
	
	private void appendFields(StringBuilder builder, T t, String tab) {
		IGenerator<IField> fieldGenerator = generatorFactory.createFieldGenerator();
		for (IField field : t.getFields()) {
			builder.append(fieldGenerator.generate(field, tab));
		}
	}
	
	private void appendMethods(StringBuilder builder, T t, String tab) {
		IGenerator<IMethod> methodGenerator = generatorFactory.createMethodGenerator();
		for (IMethod method : t.getMethods()) {
			builder.append(methodGenerator.generate(method, tab));
		}
	}
	
	private void appendInners(StringBuilder builder, T t, String tab) {
		IGenerator<IEnum> enumGenerator = generatorFactory.createEnumGenerator();
		IGenerator<IClass> classGenerator = generatorFactory.createClassGenerator();
		IGenerator<IInterface> interfaceGenerator = generatorFactory.createInterfaceGenerator();
		for (IInterface inner : t.getInnerClasses()) {
			builder.append(LN);
			String innerTab = tab + LN;
			if (inner instanceof IEnum) {
				builder.append(enumGenerator.generate((IEnum) inner, innerTab));
			} else if (inner instanceof IClass) {
				builder.append(classGenerator.generate((IClass) inner, innerTab));
			} else {
				builder.append(interfaceGenerator.generate(inner, innerTab));
			}
		}
	}
	
	protected abstract String getType();
	
	protected abstract String generateExtends(T t);
	
	protected abstract String getTypeInterface();
	
	protected abstract String generateFront(T t, final String tab);

}
