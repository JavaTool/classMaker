package org.tool.classMaker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.IInputStreamProvider;
import org.tool.classMaker.maker.Maker;

final class ClassMaker {
	
	private static final String[] DEFAULT_ARGS = new String[] {
			"D:/My_space/CrossGateProject/program/proto/proto_src/Account.proto", // url
			"org.tool.classMaker.generator.java.GeneratorFactory", // factory
//			"org.tool.classMaker.input.reader.excel.ConfReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false", // reader
//			"org.tool.classMaker.input.reader.excel.BeanReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator", // reader
			"org.tool.classMaker.input.reader.proto.ProtoReader_A:a", // reader
			"D:/My_space/CrossGateBase/src/", // output dir
			"cg.base.io.newMessage", // package
			"org.tool.classMaker.input.stream.FileStreamProvider", // input
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
			maker.make((IInputStreamProvider) Class.forName(args[5]).getConstructor(String.class).newInstance(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
