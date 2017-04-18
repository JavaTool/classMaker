package org.tool.classMaker.maker;

import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.input.reader.IReader;
import org.tool.classMaker.input.stream.IInputStreamProvider;
import org.tool.classMaker.output.Output;

public final class Maker {
	
	private IReader reader;
	
	private IGeneratorFactory generatorFactory;
	
	private String outputDir;
	
	private String _package;
	
	public void make(IInputStreamProvider inputStreamProvider) throws Exception {
		Output output = new Output(generatorFactory);
		reader.setPackage(_package);
		while (inputStreamProvider.hasNext()) {
			output.output(outputDir, reader.read(inputStreamProvider.provide()));
		}
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
