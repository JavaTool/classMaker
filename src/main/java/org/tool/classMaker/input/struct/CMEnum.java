package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.ISubEnum;

public final class CMEnum extends CMInterface implements IEnum {
	
	private List<ISubEnum> subEnums;

	@Override
	public List<ISubEnum> getSubEnums() {
		return subEnums;
	}

	public void setSubEnums(List<ISubEnum> subEnums) {
		this.subEnums = subEnums;
	}

}
