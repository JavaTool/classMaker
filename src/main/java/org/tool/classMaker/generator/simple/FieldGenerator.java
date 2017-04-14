package org.tool.classMaker.generator.simple;

import org.tool.classMaker.struct.IField;

final class FieldGenerator extends AppendStaticFinalGenerator<IField> {

	@Override
	protected String generateBody(IField t, String tab, String base) {
		StringBuilder builder = new StringBuilder(LN);
		builder.append(base);
		builder.append(t.getType()).append(BLANK).append(t.getName());
		String defaultValue = t.getDefaultValue() != null && t.getDefaultValue().length() > 0 ? t.getDefaultValue() : "";
		if (defaultValue.length() > 0) {
			builder.append(BLANK).append("=").append(BLANK).append(defaultValue);
		}
		builder.append(";").append(LN);
		return builder.toString();
	}

}
