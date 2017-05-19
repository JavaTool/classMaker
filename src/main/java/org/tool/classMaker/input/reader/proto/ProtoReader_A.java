package org.tool.classMaker.input.reader.proto;

import java.util.List;
import java.util.Map;

import org.tool.classMaker.Utils;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
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

public final class ProtoReader_A extends ProtoReader {
	
	public ProtoReader_A(String protoName) {
		super(protoName);
	}
	
	abstract static class TypeCreator<T> {
		
		protected String protoName;
		
		protected String _package;
		
		protected String protoPackage;
		
		public abstract T create(IClasses classes, String name, List<String> structLines, Map<String, String> notes);

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
		public CMEnum create(IClasses classes, String name, List<String> structLines, Map<String, String> notes) {
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
				cmSubEnum.setNote(notes.get(line));
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
		CMInterface cmInterface = createProcessor(_package + ".processor", Utils.firstUpper(protoName) + "Processor");
		classNames.forEach(name -> {
			for (ISubEnum enu : classes.getEnums().get("MessageId").getSubEnums()) {
				if (!name.startsWith("SC_") && enu.getName().equals("MI_" + name)) {
					IInterface inter = classes.getClasses().get(name);
					CMMethod method = CMStructBuilder.createPublicCMMethod();
					method.setAbstract(true);
					method.setInterface(true);
					method.setName("process" + inter.getName().replaceFirst("Cs", ""));
					method.setReturnType(CMMethod.NONE_RETURN);
					((CMImportGroup) cmInterface.getImportGroup()).addImport(CMStructBuilder.createCMImport(inter.getPackage() + "." + inter.getName()));
					method.getParams().add(CMStructBuilder.createMethodParam("csMessage", inter.getName()));
					method.getParams().add(CMStructBuilder.createMethodParam("sender", "IMessageSender"));
					cmInterface.getMethods().add(method);
					break;
				}
			}
		});
		if (utilsClass != null) {
			((CMImportGroup) utilsClass.getImportGroup()).addImport(CMStructBuilder.createCMImport(protoPackage + "." + protoName + ".*"));
			classes.getEnums().get("MessageId").getSubEnums().forEach(sub -> {
				if (sub.getNote().startsWith("@") && sub.getNote().split("=")[1].trim().equals(protoName.replace("Protos", "")) && !sub.getName().startsWith("MI_SC")) {
					CMMethod method = CMStructBuilder.createPublicCMMethod();
					method.setAbstract(true);
					method.setInterface(true);
					method.setName("process" + Utils._ToUppercase(sub.getName().replaceFirst("MI_CS_", "").replaceFirst("MI_VO_", "")));
					method.setReturnType(CMMethod.NONE_RETURN);
					method.getParams().add(CMStructBuilder.createMethodParam("serial", "int"));
					method.getParams().add(CMStructBuilder.createMethodParam("sender", "IMessageSender"));
					cmInterface.getMethods().add(method);
				}
			});
		}
		if (cmInterface.getMethods().size() > 0) {
			classes.getInterfaces().put(cmInterface.getName(), cmInterface);
		}
		classNames.clear();
	}
	
	private static CMInterface createProcessor(String _package, String name) {
		CMInterface cmInterface = CMStructBuilder.createCMInterface(0);
		cmInterface.setPackage(_package);
		cmInterface.setName("I" + name);
		((CMImportGroup) cmInterface.getImportGroup()).addImport(CMStructBuilder.createCMImport("org.tool.server.io.message.IMessageSender"));
		return cmInterface;
	}

	@Override
	protected TypeCreator<CMClass> createClassCreator() {
		return new ClassCreator_A();
	}

	@Override
	protected TypeCreator<CMEnum> createEnumCreator() {
		return new EnumCreator();
	}

}
