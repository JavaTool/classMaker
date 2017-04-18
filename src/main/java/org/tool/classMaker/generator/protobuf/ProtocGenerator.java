package org.tool.classMaker.generator.protobuf;

import java.io.File;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.output.Output;

public final class ProtocGenerator implements IGenerator<File> {

	@Override
	public String generate(File t, String tab) {
		StringBuilder builder = new StringBuilder();
		for (File file : t.listFiles(pathname -> {
			return pathname.getName().endsWith(".proto");
		})) {
			builder.append("protoc --proto_path=proto_src --java_out=").append(tab).append(" proto_src/").append(file.getName()).append("\r\n");
		}
		builder.append("protoc /s /e /Y").append("\r\n");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		String text = new ProtocGenerator().generate(new File(args[0]), args[1]);
		try {
			Output.output(text, new File(args[2]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
