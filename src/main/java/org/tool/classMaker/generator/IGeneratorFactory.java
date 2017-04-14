package org.tool.classMaker.generator;

import org.tool.classMaker.struct.IClass;
import org.tool.classMaker.struct.IEnum;
import org.tool.classMaker.struct.IField;
import org.tool.classMaker.struct.IImport;
import org.tool.classMaker.struct.IImportGroup;
import org.tool.classMaker.struct.IInterface;
import org.tool.classMaker.struct.IMethod;
import org.tool.classMaker.struct.ISubEnum;

public interface IGeneratorFactory {
	
	IGenerator<IField> createFieldGenerator();
	
	IGenerator<IMethod> createMethodGenerator();
	
	IGenerator<IInterface> createInterfaceGenerator();
	
	IGenerator<IClass> createClassGenerator();
	
	IGenerator<IEnum> createEnumGenerator();
	
	IGenerator<ISubEnum> createSubEnumGenerator();
	
	IGenerator<IImport> createImportGenerator();
	
	IGenerator<IImportGroup> createImportGroupGenerator();

}
