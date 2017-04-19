package org.tool.classMaker.maker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.IInputStreamProvider;
import org.tool.classMaker.input.struct.CMClasses;
import org.tool.classMaker.output.Output;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IClassesVisitor;

public final class Maker {
	
	private IReader reader;
	
	private IGeneratorFactory generatorFactory;
	
	private String outputDir;
	
	private String _package;
	
	public void make(IInputStreamProvider inputStreamProvider, IClassesVisitor classesVisitor) throws Exception {
		Output output = new Output(generatorFactory);
		reader.setPackage(_package);
		IClasses classes = new CMClasses();
		while (inputStreamProvider.hasNext()) {
			reader.read(classes, inputStreamProvider.provide());
			reader.clear();
		}
		classes.accpet(classesVisitor == null ? (t -> {}) : classesVisitor);
		output.output(outputDir, classes);
	}

	public void setReader(IReader reader) {
		this.reader = reader;
	}

	public void setGeneratorFactory(IGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setPackage(String _package) {
		this._package = _package;
	}

}
