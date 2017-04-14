package org.tool.classMaker.generator.simple;

import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.ISubEnum;

final class SubEnumGenerator extends BaseGenerator<ISubEnum> {

	@Override
	protected String generateBody(ISubEnum t, String tab, String base) {
		StringBuilder builder = new StringBuilder(LN);
		builder.append(base);
		builder.append(t.getName());
		if (t.getParams().size() > 0) {
			builder.append("(");
			for (IField param : t.getParams()) {
				builder.append(param.getName()).append(",").append(BLANK);
			}
			builder.setLength(builder.length() - 2);
			builder.append(")");
		}
		return builder.toString();
	}

	@Override
	protected void generateHead(ISubEnum t, StringBuilder builder) {}

}
