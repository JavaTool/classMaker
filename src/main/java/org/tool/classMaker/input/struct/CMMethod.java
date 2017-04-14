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

	public String getReturnType() {
		return returnType;
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public List<IField> getParams() {
		return params;
	}

	public List<String> getContents() {
		return contents;
	}

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
