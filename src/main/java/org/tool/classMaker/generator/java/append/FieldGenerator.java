package org.tool.classMaker.generator.java.append;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.struct.IField;

final class FieldGenerator implements IGenerator<IField> {

	@Override
	public String generate(IField t, String tab) {
		StringBuilder builder = new StringBuilder(tab);
		if (t.getAccess().getText().length() > 0) {
			builder.append(t.getAccess().getText()).append(BLANK);
		}
		if (t.isStatic()) {
			builder.append("static").append(BLANK);
		}
		if (t.isFinal()) {
			builder.append("final").append(BLANK);
		}
		builder.append(t.getType()).append(BLANK).append(t.getName());
		if (t.getDefaultValue() != null) {
			builder.append(BLANK).append("=").append(BLANK).append(t.getDefaultValue());
		}
		builder.append(";").append(BLANK).append("//").append(BLANK).append(t.getNote()).append(LN);
		return builder.toString();
	}

}
