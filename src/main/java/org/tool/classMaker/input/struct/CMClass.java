package org.tool.classMaker.input.struct;

import org.tool.classMaker.struct.IClass;

public final class CMClass extends CMInterface implements IClass {
	
	private IClass supper;

	public IClass getSupper() {
		return supper;
	}

	public void setSupper(IClass supper) {
		this.supper = supper;
	}

}
