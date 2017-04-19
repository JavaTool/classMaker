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
	
	public FileStreamProvider(String url, String fileType, String patten) {
		File file = new File(url);
		files = (file.isDirectory() ? listFiles(file, fileType, patten.split(";")) : Lists.newArrayList(file)).iterator();
	}
	
	private List<File> listFiles(File dir, String fileType, String[] pattens) {
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
