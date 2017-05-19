package org.tool.classMaker.input.reader.proto;

import java.util.List;
import java.util.Map;

import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.reader.proto.ProtoReader_A.TypeCreator;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.struct.IClasses;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class ProtoReader extends LineReader {
	
	protected final TypeCreator<CMClass> classCreator;
	
	protected final TypeCreator<CMEnum> enumCreator;
	
	protected final List<String> structLines;
	
	protected final List<String> classNames;
	
	protected String protoName;
	
	protected String _package;
	
	protected String protoPackage;
	
	protected String note;
	
	protected Map<String, String> notes;
	
	public ProtoReader(String protoName) {
		this.protoName = protoName;
		classCreator = createClassCreator();
		enumCreator = createEnumCreator();
		structLines = Lists.newLinkedList();
		classNames = Lists.newLinkedList();
		notes = Maps.newHashMap();
	}
	
	protected abstract TypeCreator<CMClass> createClassCreator();
	
	protected abstract TypeCreator<CMEnum> createEnumCreator();

	@Override
	public void setPackage(String _package) {
		this._package = _package;
	}

	@Override
	protected void read(IClasses classes, String line) throws Exception {
		if (line.startsWith("message") || line.startsWith("enum")) {
			structLines.add(line);
			notes.clear();
		} else if (line.startsWith("import ")) {
			
		} else if (line.trim().startsWith("//")) {
			note = line.trim().substring(2).trim();
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
				(type.startsWith("message") ? classCreator : enumCreator).create(classes, name, structLines, notes);
				if (type.startsWith("message")) {
					classNames.add(name);
				}
				structLines.clear();
			} else {
				structLines.add(line);
				notes.put(line, note);
				note = "";
			}
		}
	}

}
