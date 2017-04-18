package org.tool.classMaker.input.reader.proto;

import java.util.List;

import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;

import com.google.common.collect.Lists;

public final class ProtoReader_A extends LineReader {
	
	@SuppressWarnings("unused")
	private final String name;
	
	private final TypeCreator<CMClass> classCreator;
	
	private final TypeCreator<CMEnum> enumCreator;
	
	private final List<String> structLines;
	
	private String _package;
	
	public ProtoReader_A(String name) {
		this.name = name;
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
			
		} else if (structLines.size() > 0 && line.trim().length() > 0 && !line.trim().startsWith("//")) {
			if (line.trim().startsWith("{")) {
				
			} else if (line.trim().startsWith("}")) {
				String[] infos = structLines.remove(0).split(" ");
				String type = infos[0];
				String name = infos[1].replace("{", "");
				(type.startsWith("message") ? classCreator : enumCreator).create(classes, name, _package, structLines);
				structLines.clear();
			} else {
				structLines.add(line);
			}
		}
	}
	
	abstract static class TypeCreator<T> {
		
		public abstract T create(IClasses classes, String name, String _package, List<String> structLines);
		
	}
	
	private static class EnumCreator extends TypeCreator<CMEnum> {

		@Override
		public CMEnum create(IClasses classes, String name, String _package, List<String> structLines) {
			CMEnum cmEnum = new CMEnum();
			cmEnum.setAccess(Access.PUBLIC);
			cmEnum.setName(name);
			cmEnum.setPackage(_package);
			classes.getEnums().put(name, cmEnum);
			return cmEnum;
		}
		
	}

}
