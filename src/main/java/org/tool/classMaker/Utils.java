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
	
	public static String _ToUppercase(String info) {
		StringBuilder builder = new StringBuilder();
		String[] infos = info.split("_");
		if (infos.length > 1) {
			for (String str : infos) {
				builder.append(firstUpper(str.toLowerCase()));
			}
			return builder.toString();
		} else {
			return firstUpper(info.toLowerCase());
		}
	}
	
	public static String uppercaseTo_(String info) {
		StringBuilder builder = new StringBuilder(info.substring(0, 1).toUpperCase());
		for (int i = 1;i < info.length();i++) {
			if (Character.isUpperCase(info.charAt(i))) {
				builder.append("_");
			}
			builder.append(info.substring(i, i + 1).toUpperCase());
		}
		return builder.toString();
	}
	
	public static boolean checkIndex(String[] args, int index) {
		return args != null && args.length > index && args[index].length() > 0;
	}
	
	public static void main(String[] args) {
		System.out.println(splitPackage("org.tool.classMaker.Utils")[0]);
		System.out.println(splitPackage("org.tool.classMaker.Utils")[1]);
	}

}
