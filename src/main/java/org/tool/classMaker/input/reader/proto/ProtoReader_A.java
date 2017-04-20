package org.tool.classMaker.input.reader.proto;

import java.util.List;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.input.struct.CMSubEnum;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.ISubEnum;

import com.google.common.collect.Lists;

public final class ProtoReader_A extends LineReader {
	
	private final TypeCreator<CMClass> classCreator;
	
	private final TypeCreator<CMEnum> enumCreator;
	
	private final List<String> structLines;
	
	private final List<String> classNames;
	
	private String protoName;
	
	private String _package;
	
	private String protoPackage;
	
	public ProtoReader_A(String protoName) {
		this.protoName = protoName;
		classCreator = new ClassCreator();
		enumCreator = new EnumCreator();
		structLines = Lists.newLinkedList();
		classNames = Lists.newLinkedList();
	}

	@Override
	public void setPackage(String _package) {
		this._package = _package;
	}

	@Override
	protected void read(IClasses classes, String line) throws Exception {
		if (line.startsWith("message") || line.startsWith("enum")) {
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
				if (type.startsWith("message")) {
					classNames.add(name);
				}
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
			List<ISubEnum> subs = Lists.newArrayListWithCapacity(structLines.size());
			structLines.forEach(line -> {
				String[] infos = line.split("=");
				CMSubEnum cmSubEnum = new CMSubEnum();
				cmSubEnum.setAccess(Access.PUBLIC);
				cmSubEnum.setName(infos[0].trim());
				subs.add(cmSubEnum);
			});
			cmEnum.setSubEnums(subs);
			classes.getEnums().put(name, cmEnum);
			return cmEnum;
		}
		
	}

	@Override
	protected void readFinish(IClasses classes) {
		String className = "RepeatedUtils";
		IClass utilsClass = classes.getClasses().get(className);
		if (utilsClass != null) {
			((CMImportGroup) utilsClass.getImportGroup()).addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + ".*"));
		}
		CMInterface cmInterface = createProcessor(_package, Utils.firstUpper(protoName) + "Processor");
		classNames.forEach(name -> {
			for (ISubEnum enu : classes.getEnums().get("MessageId").getSubEnums()) {
				if (!name.startsWith("SC_") && enu.getName().equals("MI_" + name)) {
					IInterface inter = classes.getInterfaces().get("I" + Utils._ToUppercase(name));
					CMMethod method = CMStructBuilder.createPublicCMMethod();
					method.setAbstract(true);
					method.setInterface(true);
					method.setName("process" + inter.getName().replaceFirst("ICs", "").replaceFirst("IVo", ""));
					method.setReturnType(CMMethod.NONE_RETURN);
					CMField param1 = new CMField();
					param1.setName("csMessage");
					param1.setType(inter.getName());
					((CMImportGroup) cmInterface.getImportGroup()).addImport(CMStructBuilder.createCMImport(inter.getPackage() + "." + inter.getName()));
					method.getParams().add(param1);
					CMField param2 = new CMField();
					param2.setName("sender");
					param2.setType("ISender");
					method.getParams().add(param2);
					cmInterface.getMethods().add(method);
					break;
				}
			}
		});
		if (cmInterface.getMethods().size() > 0) {
			classes.getInterfaces().put(cmInterface.getName(), cmInterface);
		}
		classNames.clear();
	}
	
	private static CMInterface createProcessor(String _package, String name) {
		CMInterface cmInterface = CMStructBuilder.createCMInterface(0);
		cmInterface.setPackage(_package);
		cmInterface.setName("I" + name);
		((CMImportGroup) cmInterface.getImportGroup()).addImport(CMStructBuilder.createCMImport("org.tool.server.io.dispatch.ISender"));
		return cmInterface;
	}

}
