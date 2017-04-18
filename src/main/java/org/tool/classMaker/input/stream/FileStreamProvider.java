package org.tool.classMaker.input.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public final class FileStreamProvider implements IInputStreamProvider {
	
	private final Iterator<File> files;
	
	private final String fileType;
	
	private final String[] pattens;
	
	public FileStreamProvider(String url, String fileType, String patten) {
		File file = new File(url);
		files = (file.isDirectory() ? listFiles(file) : Lists.newArrayList(file)).iterator();
		this.fileType = fileType;
		this.pattens = patten.split(";");
	}
	
	private List<File> listFiles(File dir) {
		Multimap<Integer, File> map = HashMultimap.create();
		for (File file : dir.listFiles(file -> {
			return file.getName().endsWith(fileType);
		})) {
			String name = file.getName();
			for (int i = 0;i < pattens.length;i++) {
				if (name.startsWith(pattens[i]) || pattens[i].equals("*")) {
					map.put(i, file);
					break;
				}
			}
		}
		List<File> files = Lists.newLinkedList();
		for (int i = 0;i < pattens.length;i++) {
			files.addAll(map.get(i));
		}
		return files;
	}

	@Override
	public InputStream provide() throws Exception {
		return new FileInputStream(files.next());
	}

	@Override
	public boolean hasNext() {
		return files.hasNext();
	}

}
