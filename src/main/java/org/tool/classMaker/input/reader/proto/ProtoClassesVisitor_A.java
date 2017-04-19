package org.tool.classMaker.input.reader.proto;

import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IClassesVisitor;

public final class ProtoClassesVisitor_A implements IClassesVisitor {

	@Override
	public void visit(IClasses classes) {
		classes.getEnums().clear();
	}

}
