package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.ISubEnum;

public class CMSubEnum extends CMBase implements ISubEnum {
	
	private List<IField> params;

	@Override
	public List<IField> getParams() {
		return params;
	}

	public void setParams(List<IField> params) {
		this.params = params;
	}

}
