package org.tool.classMaker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.IInputStreamProvider;
import org.tool.classMaker.maker.Maker;
import org.tool.classMaker.struct.IClassesVisitor;

final class ClassMaker {
	
	private static final String[] DEFAULT_ARGS = new String[] {
			"D:/My_space/CrossGateSource/config/configuration.xml", // url
			"org.tool.classMaker.generator.java.GeneratorFactory", // factory
//			"org.tool.classMaker.input.reader.excel.ConfReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false", // reader
//			"org.tool.classMaker.input.reader.excel.BeanReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator", // reader
//			"org.tool.classMaker.input.reader.proto.ProtoReader_A:a", // reader
			"org.tool.classMaker.input.reader.xml.DomReader:org.tool.classMaker.input.reader.xml.JavaBindClassCreator_A", // reader
			"D:/My_space/CrossGateSource/src/", // output dir
			"cg.source", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:proto:MessageId;*", // inputProvider
//			"org.tool.classMaker.input.reader.proto.ProtoClassesVisitor_A", // classesVisitor
			"", // classesVisitor
	};

	public static void main(String[] args) {
		args = args.length < DEFAULT_ARGS.length ? DEFAULT_ARGS : args;
		
		Maker maker = new Maker();
		try {
			maker.setGeneratorFactory((IGeneratorFactory) Class.forName(args[1]).newInstance());
			String[] readerInfos = args[2].split(":", -2);
			maker.setReader((IReader) Class.forName(readerInfos[0]).getConstructor(String.class).newInstance(readerInfos[1]));
			maker.setOutputDir(args[3]);
			maker.setPackage(args[4]);
			String[] providerInfos = args[5].split(":", -2);
			@SuppressWarnings("unchecked")
			Class<IInputStreamProvider> providerClass = (Class<IInputStreamProvider>) Class.forName(providerInfos[0]);
			IInputStreamProvider provider = providerClass.getConstructor(String.class, String.class, String.class).newInstance(args[0], providerInfos[1], providerInfos[2]);
			IClassesVisitor vistor = args[6].length() == 0 ? null : (IClassesVisitor) Class.forName(args[6]).newInstance();
			maker.make(provider, vistor);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
