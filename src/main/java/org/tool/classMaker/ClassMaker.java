package org.tool.classMaker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.IInputStreamProvider;
import org.tool.classMaker.maker.Maker;
import org.tool.classMaker.struct.IClassesVisitor;

final class ClassMaker {
	
	private static final String[] DEFAULT_ARGS = new String[] {
//			"D:/My_space/CrossGateProject/program/proto/proto_src", // url
			"D:/note/ErrorMessage.xlsx", // url
			"org.tool.classMaker.generator.java.append.AppendGeneratorFactory", // factory
//			"org.tool.classMaker.input.reader.excel.ConfReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false", // reader
//			"org.tool.classMaker.input.reader.excel.BeanReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator", // reader
//			"org.tool.classMaker.input.reader.proto.ProtoReader_A:a", // reader
			"org.tool.classMaker.input.reader.excel.ErrorReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false;D:/OP_space/data/excel/_ErrorMessage.xlsx;D:/OP_space/data/excel/_Text.xlsx;D:/OP_space/mmo.build/mmo.core/src/main/java/com/game2sky/publib/constants/CommonErrorCodeConstants.java", // reader
//			"org.tool.classMaker.input.reader.xml.DomReader:org.tool.classMaker.input.reader.xml.JavaBindClassCreator_A", // reader
//			"D:/My_space/CrossGateBase/src/", // output dir
			"D:/OP_space/mmo.build/mmo.core/src/main/java/", // output dir
//			"cg.base.io.message", // package
			"com.game2sky.publib.constants", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.java:*", // inputProvider
//			"org.tool.classMaker.input.reader.proto.ProtoClassesVisitor_A", // classesVisitor
			"", // classesVisitor
	};
	
	protected static final String[] CG_EXCEL_ARGS = new String[] {
			"D:/My_space/CrossGateResource", // url
			"org.tool.classMaker.generator.java.GeneratorFactory", // factory
			"org.tool.classMaker.input.reader.excel.ConfReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false", // reader
			"D:/My_space/CrossGateBase/src/", // output dir
			"cg.base.conf", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.xlsx:Conf", // inputProvider
			"", // classesVisitor
	};
	
	protected static final String[] PROTO_ARGS = new String[] {
			"D:/My_space/CrossGateProject/program/proto/proto", // url
			"org.tool.classMaker.generator.java.GeneratorFactory", // factory
			"org.tool.classMaker.input.reader.proto.ProtoReader_A:a", // reader
			"D:/My_space/CrossGateBase/src/", // output dir
			"cg.base.io.message", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.proto:MessageId;*", // inputProvider
			"org.tool.classMaker.input.reader.proto.ProtoClassesVisitor_A", // classesVisitor
	};
	
	protected static final String[] OP_ERROR_ARGS = new String[] {
			"D:/note/ErrorMessage.xlsx", // url
			"org.tool.classMaker.generator.java.append.AppendGeneratorFactory", // factory
			"org.tool.classMaker.input.reader.excel.ErrorReader_A:org.tool.classMaker.input.reader.excel.XLSXCreator;false;D:/OP_space/data/excel/_ErrorMessage.xlsx;D:/OP_space/data/excel/_Text.xlsx;D:/OP_space/mmo.build/mmo.core/src/main/java/com/game2sky/publib/constants/CommonErrorCodeConstants.java", // reader
			"D:/OP_space/mmo.build/mmo.core/src/main/java/", // output dir
			"com.game2sky.publib.constants", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.java:*", // inputProvider
			"", // classesVisitor
	};
	
	protected static final String[] CSHARP_ARGS = new String[] {
			"D:/My_space/CrossGateProject/program/proto/proto", // url
			"org.tool.classMaker.generator.csharp.GeneratorFactory", // factory
			"org.tool.classMaker.input.reader.proto.ProtoReader_C:a", // reader
			"D:/My_space/CrossGateClient_Unity/Assets/Scripts/", // output dir
			"cg.base.io.message", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.proto:MessageId;*", // inputProvider
			"org.tool.classMaker.input.reader.proto.ProtoClassesVisitor_A", // classesVisitor
	};

	protected static final String[] CASSANDRA_ARGS = new String[] {
			"/Users/FuHuiyuan/Documents/cassandra_class.txt", // url
			"org.tool.classMaker.generator.java.GeneratorFactory", // factory
			"org.tool.classMaker.input.reader.cassandra.CassandraReader:a", // reader
			"/Users/FuHuiyuan/IdeaProjects/template-service/src/main/java/", // output dir
			"com.yinxiang.microservice.template.bean", // package
			"org.tool.classMaker.input.stream.FileStreamProvider:.java:*", // inputProvider
			"", // classesVisitor
	};

	public static void main(String[] args) {
		args = args.length < DEFAULT_ARGS.length ? CASSANDRA_ARGS : args;
		
		Maker maker = new Maker();
		try {
			maker.setGeneratorFactory((IGeneratorFactory) Class.forName(args[1]).newInstance());
			String[] readerInfos = args[2].split(":", 2);
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
