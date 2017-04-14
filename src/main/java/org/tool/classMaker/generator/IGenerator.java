package org.tool.classMaker.generator;

public interface IGenerator<T> {
	
	String LN = "\r\n";
	
	String BLANK = " ";
	
	String generate(T t, String tab);

}
