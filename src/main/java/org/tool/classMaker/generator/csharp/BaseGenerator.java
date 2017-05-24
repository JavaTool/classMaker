package org.tool.classMaker.generator.csharp;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.struct.IBase;

public abstract class BaseGenerator<T extends IBase> implements IGenerator<T> {

	public final String generate(T t, String tab) {
		return generateBody(t, tab + "\t", generateBase(t, tab + "\t"));
	}
	
	protected abstract String generateBody(T t, String tab, String base);
	
	private String generateBase(T t, String tab) {
		StringBuilder builder = new StringBuilder();
		String note = t.getNote() != null && t.getNote().length() > 0 ? t.getNote() : "Generate by classMaker.";
		if (note.length() > 0) {
			builder.append(tab).append("/**").append(LN);
			builder.append(tab).append(" * ").append(note).append(LN);
			builder.append(tab).append(" */").append(LN);
		}
		if (t.getAnnotations().size() == 1) {
			String annotation = t.getAnnotations().get(0);
			if (annotation.equals("override")) {
				builder.append(tab);
				builder.append(annotation).append(BLANK);
			} else {
				builder.append(tab).append(annotation).append(LN);
				builder.append(tab);
			}
		} else {
			builder.append(tab);
		}
		generateHead(t, builder);
		return builder.toString();
	}
	
	protected abstract void generateHead(T t, StringBuilder builder);

}
