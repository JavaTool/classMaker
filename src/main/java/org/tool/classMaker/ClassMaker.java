package org.tool.classMaker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.FileStream;
import org.tool.classMaker.maker.Maker;

final class ClassMaker {
	
	private static final String[] DEFAULT_ARGS = new String[] {
			"D:/Test1.xlsx", // url
			"org.tool.classMaker.generator.simple.GeneratorFactory", // factory
//			"org.tool.classMaker.input.reader.excel.ConfReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false", // reader
			"org.tool.classMaker.input.reader.excel.BeanReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator", // reader
			"D:/My_space/classMaker/src/main/java/", // output dir
			"org.tool.classMaker.test" // package
	};

	public static void main(String[] args) {
		args = args.length < DEFAULT_ARGS.length ? DEFAULT_ARGS : args;
		
		Maker maker = new Maker();
		try {
			maker.setGeneratorFactory((IGeneratorFactory) Class.forName(args[1]).newInstance());
			String[] readerInfos = args[2].split(":");
			maker.setReader((IReader) Class.forName(readerInfos[0]).getConstructor(String.class).newInstance(readerInfos[1]));
			maker.setOutputDir(args[3]);
			maker.setPackage(args[4]);
			maker.make(new FileStream().get(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
