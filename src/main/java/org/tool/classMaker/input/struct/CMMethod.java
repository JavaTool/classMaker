package org.tool.classMaker.input.struct;

import java.util.List;

import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IMethod;

public final class CMMethod extends CMBase implements IMethod {
	
	private String returnType;
	
	private boolean isSynchronized;
	
	private boolean isAbstract;
	
	private boolean isInterface;
	
	private List<IField> params;
	
	private List<String> contents;
	
	private List<IField> exceptions;

	@Override
	public String getReturnType() {
		return returnType;
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isInterface() {
		return isInterface;
	}

	@Override
	public List<IField> getParams() {
		return params;
	}

	@Override
	public List<String> getContents() {
		return contents;
	}

	@Override
	public List<IField> getExceptions() {
		return exceptions;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public void setParams(List<IField> params) {
		this.params = params;
	}

	public void setContents(List<String> contents) {
		this.contents = contents;
	}

	public void setExceptions(List<IField> exceptions) {
		this.exceptions = exceptions;
	}

}
