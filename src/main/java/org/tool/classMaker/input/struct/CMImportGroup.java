package org.tool.classMaker.input.struct;

import java.util.Collection;
import java.util.Set;

import org.tool.classMaker.struct.IImport;
import org.tool.classMaker.struct.IImportGroup;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class CMImportGroup implements IImportGroup {
	
	private final Multimap<String, IImport> imports;
	
	public CMImportGroup(Multimap<String, IImport> imports) {
		this.imports = imports;
	}
	
	public CMImportGroup() {
		this(createDefault());
	}
	
	private static Multimap<String, IImport> createDefault() {
		return HashMultimap.create();
	}

	@Override
	public Collection<IImport> getImports(String group) {
		return imports.get(group);
	}

	@Override
	public Set<String> getGroup() {
		return imports.keySet();
	}
	
	public void addImport(IImport imp) {
		imports.put(imp.getContent().split("\\.")[0], imp);
	}

}
