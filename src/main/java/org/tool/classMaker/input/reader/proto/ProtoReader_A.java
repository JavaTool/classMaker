package org.tool.classMaker.input.reader.proto;

import java.util.List;

import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;

import com.google.common.collect.Lists;

public final class ProtoReader_A extends LineReader {
	
	private final TypeCreator<CMClass> classCreator;
	
	private final TypeCreator<CMEnum> enumCreator;
	
	private final List<String> structLines;
	
	private String protoName;
	
	private String _package;
	
	private String protoPackage;
	
	public ProtoReader_A(String protoName) {
		this.protoName = protoName;
		classCreator = new ClassCreator();
		enumCreator = new EnumCreator();
		structLines = Lists.newLinkedList();
	}

	@Override
	public void setPackage(String _package) {
		this._package = _package;
	}

	@Override
	protected void read(IClasses classes, String line) throws Exception {
		if (line.startsWith("message")) {
			structLines.add(line);
		} else if (line.startsWith("import ")) {
			
		} else if (line.startsWith("option java_package ")) {
			protoPackage = line.split("\"")[1];
		} else if (line.startsWith("option java_outer_classname ")) {
			protoName = line.split("\"")[1];
		} else if (structLines.size() > 0 && line.trim().length() > 0 && !line.trim().startsWith("//")) {
			if (line.trim().startsWith("{")) {
				
			} else if (line.trim().startsWith("}")) {
				String[] infos = structLines.remove(0).split(" ");
				String type = infos[0];
				String name = infos[1].replace("{", "");
				(type.startsWith("message") ? classCreator : enumCreator).setPackage(_package);
				(type.startsWith("message") ? classCreator : enumCreator).setProtoName(protoName);
				(type.startsWith("message") ? classCreator : enumCreator).setProtoPackage(protoPackage);
				(type.startsWith("message") ? classCreator : enumCreator).create(classes, name, structLines);
				structLines.clear();
			} else {
				structLines.add(line);
			}
		}
	}
	
	abstract static class TypeCreator<T> {
		
		protected String protoName;
		
		protected String _package;
		
		protected String protoPackage;
		
		public abstract T create(IClasses classes, String name, List<String> structLines);

		public void setProtoName(String protoName) {
			this.protoName = protoName;
		}

		public void setPackage(String _package) {
			this._package = _package;
		}

		public void setProtoPackage(String protoPackage) {
			this.protoPackage = protoPackage;
		}
		
	}
	
	private static class EnumCreator extends TypeCreator<CMEnum> {

		@Override
		public CMEnum create(IClasses classes, String name, List<String> structLines) {
			CMEnum cmEnum = new CMEnum();
			cmEnum.setAccess(Access.PUBLIC);
			cmEnum.setName(name);
			cmEnum.setPackage(_package);
			classes.getEnums().put(name, cmEnum);
			return cmEnum;
		}
		
	}

}
