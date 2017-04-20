package org.tool.classMaker.input.reader.xml;

import java.util.List;

import org.jdom.Element;
import org.tool.classMaker.Utils;
import org.tool.classMaker.input.struct.CMClass;
import org.tool.classMaker.input.struct.CMField;
import org.tool.classMaker.input.struct.CMImportGroup;
import org.tool.classMaker.input.struct.CMInterface;
import org.tool.classMaker.input.struct.CMMethod;
import org.tool.classMaker.input.struct.CMStructBuilder;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;

public class JavaBindClassCreator_A implements IXmlClassCreator {

	@Override
	public void create(IClasses classes, Element root, String _package) {
		@SuppressWarnings("unchecked")
		List<Element> list = root.getChildren();
		CMClass cmClass = CMStructBuilder.createCMClass(list.size(), list.size() + 3);
		cmClass.setName("Configuration");
		cmClass.setPackage(_package);
		cmClass.getAnnotations().add("XmlRootElement(name=\"configuration\")");
		CMInterface implInter = CMStructBuilder.createCMInterface(0);
		implInter.setName("IConfigurationHolder");
		cmClass.getInterfaces().add(implInter);
		addImports((CMImportGroup) cmClass.getImportGroup());
		cmClass.getFields().add(createMapField());
		cmClass.getMethods().add(createGetMethod());
		cmClass.getMethods().add(createGetMethodByDefault());
		cmClass.getMethods().add(createFromFileMethod());
		
		list.forEach(element -> {
			String name = element.getName();
			cmClass.getFields().add(createStaticField(name));
			cmClass.getMethods().add(createGetter(name));
		});
	}
	
	private static final CMField createMapField() {
		CMField mapField = new CMField();
		mapField.setName("elements");
		mapField.setAccess(Access.PRIVATE);
		mapField.setDefaultValue("Maps.newHashMap()");
		mapField.setType("Map<String, String>");
		mapField.setFinal(true);
		return mapField;
	}
	
	private static final CMMethod createGetMethod() {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("getConfigurationValue");
		method.setReturnType("String");
		method.getParams().add(CMStructBuilder.createMethodParam("key", "String"));
		method.getContents().add("return elements.get(key);");
		return method;
	}
	
	private static final CMMethod createGetMethodByDefault() {
		CMMethod method = createGetMethod();
		method.getParams().add(CMStructBuilder.createMethodParam("defaultValue", "String"));
		method.getContents().clear();
		method.getContents().add("return elements.containsKey(key) ? elements.get(key) : defaultValue;");
		return method;
	}
	
	private static final CMMethod createFromFileMethod() {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setName("createFromFile");
		method.setStatic(true);
		method.setReturnType("Configuration");
		method.getParams().add(CMStructBuilder.createMethodParam("file", "File"));
		method.getExceptions().add(CMStructBuilder.createMethodParam("JAXBException", ""));
		method.getContents().add("JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);");
		method.getContents().add("Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();");
		method.getContents().add("Configuration configuration = (Configuration) unmarshaller.unmarshal(file);");
		method.getContents().add("return configuration;");
		return method;
	}
	
	private static final CMField createStaticField(String name) {
		CMField staticField = new CMField();
		staticField.setAccess(Access.PUBLIC);
		staticField.setName(name.toUpperCase());
		staticField.setDefaultValue(name);
		staticField.setFinal(true);
		staticField.setType("String");
		staticField.setStatic(true);
		return staticField;
	}
	
	private static final CMMethod createGetter(String name) {
		CMMethod method = CMStructBuilder.createPublicCMMethod();
		method.setAccess(Access.PRIVATE);
		method.setName("set" + Utils.firstUpper(name));
		method.getAnnotations().add("XmlElement");
		method.setReturnType(CMMethod.NONE_RETURN);
		method.getParams().add(CMStructBuilder.createMethodParam("value", "String"));
		method.getContents().add("elements.put(" + name.toUpperCase() + ", value);");
		return method;
	}
	
	private static final void addImports(CMImportGroup importGroup) {
		importGroup.addImport(CMStructBuilder.createCMImport("org.tool.server.io.IConfigurationHolder"));
		importGroup.addImport(CMStructBuilder.createCMImport("java.util.Map"));
		importGroup.addImport(CMStructBuilder.createCMImport("com.google.common.collect.Maps"));
		importGroup.addImport(CMStructBuilder.createCMImport("javax.xml.bind.annotation.XmlRootElement"));
		importGroup.addImport(CMStructBuilder.createCMImport("javax.xml.bind.annotation.XmlElement"));
		importGroup.addImport(CMStructBuilder.createCMImport("javax.xml.bind.JAXBContext"));
		importGroup.addImport(CMStructBuilder.createCMImport("javax.xml.bind.JAXBException"));
		importGroup.addImport(CMStructBuilder.createCMImport("javax.xml.bind.Unmarshaller"));
	}

}
