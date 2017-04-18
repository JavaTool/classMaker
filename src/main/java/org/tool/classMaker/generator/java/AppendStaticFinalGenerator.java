package org.tool.classMaker.generator.java;

import org.tool.classMaker.struct.IBase;

public abstract class AppendStaticFinalGenerator<T extends IBase> extends BaseGenerator<T> {

	@Override
	protected final void generateHead(T t, StringBuilder builder) {
		if (t.getAccess().getText().length() > 0) {
			builder.append(t.getAccess().getText()).append(BLANK);
		}
		if (t.isStatic()) {
			builder.append("static").append(BLANK);
		}
		if (t.isFinal()) {
			builder.append("final").append(BLANK);
		}
	}

}
