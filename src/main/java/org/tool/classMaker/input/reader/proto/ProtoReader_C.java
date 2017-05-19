package org.tool.classMaker.input.reader.proto;

import java.util.List;
import java.util.Map;

import org.tool.classMaker.input.reader.proto.ProtoReader_A.TypeCreator;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.input.struct.CMSubEnum;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.ISubEnum;

import com.google.common.collect.Lists;

public final class ProtoReader_C extends ProtoReader {

	public ProtoReader_C(String protoName) {
		super(protoName);
	}

	@Override
	protected TypeCreator<CMClass> createClassCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TypeCreator<CMEnum> createEnumCreator() {
		return new EnumCreator();
	}

	@Override
	protected void readFinish(IClasses classes) {
		// TODO Auto-generated method stub

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

}
