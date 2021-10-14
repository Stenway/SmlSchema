package com.stenway.sml.schema;

import java.util.Collection;
import java.util.LinkedHashMap;

public class SsItems<T extends SsItem> {
	LinkedHashMap<String, T> dictionary = new LinkedHashMap<>();
	private String typeStr;
	SsNamespace namespace;
	
	public SsItems(String typeStr, SsNamespace namespace) {
		this.typeStr = typeStr;
		this.namespace = namespace;
	}
	
	public void add(T item) {
		String key = item.Id.toLowerCase();
		if (dictionary.containsKey(key)) {
			throw new SmlSchemaException("An "+typeStr+" with ID '"+item.Id+"' already exists in the namespace");
		}
		dictionary.put(key, item);
	}
	
	public T get(String id) {
		String key = id.toLowerCase();
		if (!dictionary.containsKey(key)) {
			throw new SmlSchemaException("Namespace does not contain " +typeStr+ " with ID '"+id+"'");
		}
		return dictionary.get(key);
	}
	
	public Collection<T> getValues() {
		return dictionary.values();
	}
	
	public boolean hasItem(String id, boolean isGlobal) {
		return getItemOrNull(id, isGlobal) != null;
	}
	
	public T getItemOrNull(String id, boolean isGlobal) {
		for (T element : dictionary.values()) {
			String curId = element.Id;
			if (isGlobal) {
				curId = namespace.Prefix + curId;
			}
			if (curId.equalsIgnoreCase(id)) {
				return element;
			}
		}
		return null;
	}
}
