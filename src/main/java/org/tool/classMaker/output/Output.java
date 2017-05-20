package org.tool.classMaker.output;

import java.io.File;
import java.io.FileWriter;

import org.tool.classMaker.generator.IGenerator;
import org.tool.classMaker.generator.IGeneratorFactory;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IInterface;

import com.google.common.base.Preconditions;

public final class Output {
	
	private final IGeneratorFactory generatorFactory;
	
	public Output(IGeneratorFactory generatorFactory) {
		this.generatorFactory = generatorFactory;
	}
	
	public void output(String outputDir, IClasses classes) throws Exception {
		File dir = new File(outputDir);
		Preconditions.checkArgument(dir.exists(), outputDir + " not exists.");
		Preconditions.checkArgument(dir.isDirectory(), outputDir + " is not a dir.");
		output(generatorFactory.createInterfaceGenerator(), classes.getInterfaces().values(), dir);
		output(generatorFactory.createClassGenerator(), classes.getClasses().values(), dir);
		output(generatorFactory.createEnumGenerator(), classes.getEnums().values(), dir);
	}
	
	private <T extends IInterface> void output(IGenerator<T> generator, Iterable<T> list, File dir) throws Exception {
		for (T t : list) {
			output(generator.generate(t, ""), createFile(dir, t));
		}
	}
	
	private static File createFile(File dir, IInterface inter) {
		return new File(dir, inter.getPackage().replaceAll("\\.", "/") + "/" + inter.getName() + "." + inter.getFileType());
	}
	
	public static void output(String content, File file) throws Exception {
		if (content != null && content.length() > 0) {
			file.delete();
			file.getParentFile().mkdirs();
			file.createNewFile();
			try (FileWriter writer = new FileWriter(file)) {
				writer.append(content);
				writer.flush();
				System.out.println("Generate " + file.getPath());
			}
		}
	}

}
