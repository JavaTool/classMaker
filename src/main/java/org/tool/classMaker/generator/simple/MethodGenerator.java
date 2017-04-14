package org.tool.classMaker.generator.simple;

import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IMethod;

final class MethodGenerator extends BaseGenerator<IMethod> {

	@Override
	protected String generateBody(IMethod t, String tab, String base) {
		StringBuilder builder = new StringBuilder(LN);
		builder.append(base);
		// synchronized
		if (t.isSynchronized()) {
			builder.append("synchronized").append(BLANK);
		}
		// return
		String returnType = t.getReturnType() != null && t.getReturnType().length() > 0 ? t.getReturnType() : "";
		if (returnType.length() > 0) {
			builder.append(returnType).append(BLANK);
		}
		// name
		builder.append(t.getName()).append("(");
		// params
		if (t.getParams().size() > 0) {
			for (IField param : t.getParams()) {
				builder.append(param.getType()).append(BLANK).append(param.getName()).append(",").append(BLANK);
			}
			builder.setLength(builder.length() - 2);
		}
		builder.append(")");
		// exception
		if (t.getExceptions().size() > 0) {
			builder.append("throws ");
		}
		for (IField exception : t.getExceptions()) {
			builder.append(exception.getType()).append(",").append(BLANK);
		}
		if (t.getExceptions().size() > 0) {
			builder.setLength(builder.length() - 2);
			builder.append(BLANK);
		}
		if (t.isAbstract()) {
			builder.append(";").append(LN);
		} else {
			builder.append("{").append(LN);
			// content
			for (String content : t.getContents()) {
				builder.append(tab).append("\t").append(content).append(LN);
			}
			// terminal
			builder.append(tab).append("}").append(LN);
		}
		return builder.toString();
	}

	@Override
	protected void generateHead(IMethod t, StringBuilder builder) {
		if (t.isInterface()) { // interface
			if (t.isStatic()) {
				builder.append(Access.PUBLIC.getText()).append(BLANK).append("static").append(BLANK);
			}
		} else { // class or enum
			String access = t.getAccess().getText();
			if (access.length() > 0) {
				builder.append(access).append(BLANK);
			}
			
			if (t.isAbstract()) {
				builder.append("abstract").append(BLANK);
			} else {
				if (t.isStatic()) {
					builder.append("static").append(BLANK);
				} else if (t.isFinal() && !t.getAccess().equals(Access.PRIVATE)) {
					builder.append("final").append(BLANK);
				}
			}
		}
	}

}
