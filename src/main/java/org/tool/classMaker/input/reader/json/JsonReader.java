package org.tool.classMaker.input.reader.json;

import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMEnum;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IInterface;

import com.alibaba.fastjson.JSONObject;

public final class JsonReader extends LineReader {
	
	public static final String KEY_TYPE = "type";
	
	public static final String VALUE_TYPE_INTERFACE = "interface";
	
	public static final String VALUE_TYPE_CLASS = "class";
	
	public static final String VALUE_TYPE_ENUM = "enum";
	
	public JsonReader(String config) {}

	@Override
	public void setPackage(String _package) {}

	@Override
	protected void read(IClasses classes, String line) throws Exception {
		JSONObject json = JSONObject.parseObject(line);
		String type = json.getString(KEY_TYPE);
		json.remove(KEY_TYPE);
		String text = json.toJSONString();
		switch (type) {
		case VALUE_TYPE_INTERFACE : 
			IInterface inter = JSONObject.parseObject(text, CMInterface.class);
			classes.getInterfaces().put(inter.getName(), inter);
			break;
		case VALUE_TYPE_CLASS : 
			IClass clz = JSONObject.parseObject(text, CMClass.class);
			classes.getInterfaces().put(clz.getName(), clz);
			break;
		case VALUE_TYPE_ENUM : 
			IEnum enu = JSONObject.parseObject(text, CMEnum.class);
			classes.getInterfaces().put(enu.getName(), enu);
			break;
		default : 
			throw new Exception("Unknow type : " + type);
		}
	}

	@Override
	protected void readFinish(IClasses classes) {}

}
