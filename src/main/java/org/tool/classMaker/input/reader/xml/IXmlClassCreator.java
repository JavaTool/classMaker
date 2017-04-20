package org.tool.classMaker.input.reader.xml;

import org.jdom.Element;
import org.tool.classMaker.struct.IClasses;

public interface IXmlClassCreator {
	
	void create(IClasses classes, Element root, String _package);

}
