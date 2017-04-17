package org.tool.classMaker;

public class Utils {
	
	public static String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1) : "");
	}
	
	public static String firstLower(String str) {
		return str.substring(0, 1).toLowerCase() + (str.length() > 1 ? str.substring(1) : "");
	}
	
	public static String[] splitPackage(String _package) {
		String[] ret = new String[2];
		int index = _package.lastIndexOf('.');
		ret[0] = _package.substring(0, index);
		ret[1] = _package.substring(index + 1);
		return ret;
	}
	
	public static void main(String[] args) {
		System.out.println(splitPackage("org.tool.classMaker.Utils")[0]);
		System.out.println(splitPackage("org.tool.classMaker.Utils")[1]);
	}

}
