package org.tool.classMaker.input.reader.xml;

import java.io.InputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.struct.IClasses;

public final class DomReader implements IReader {
	
	private String _package;
	
	private final IXmlClassCreator xmlClassCreator;
	
	public DomReader(IXmlClassCreator xmlClassCreator) {
		this.xmlClassCreator = xmlClassCreator;
	}
	
	public DomReader(String config) throws Exception {
		this((IXmlClassCreator) Class.forName(config).newInstance());
	}

	@Override
	public final void read(IClasses classes, InputStream inputStream) throws Exception {
		SAXBuilder sb = new SAXBuilder();
        sb.setValidation(false);
    	Document doc = sb.build(inputStream);
    	Element root = doc.getRootElement();
    	xmlClassCreator.create(classes, root, _package);
	}

	@Override
	public final void setPackage(String _package) {
		this._package = _package;
	}

}
