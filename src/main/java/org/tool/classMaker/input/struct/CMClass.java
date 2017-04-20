package org.tool.classMaker.input.struct;

import org.tool.classMaker.struct.IClass;

public final class CMClass extends CMInterface implements IClass {
	
	private IClass _super;

	@Override
	public IClass getSupper() {
		return _super;
	}

	public void setSuper(IClass _super) {
		this._super = _super;
	}

}
