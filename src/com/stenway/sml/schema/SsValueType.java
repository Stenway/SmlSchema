package com.stenway.sml.schema;

public class SsValueType extends SsItem {
	public SsEnum Enumeration;
	
	public SsValueType(String id) {
		super(id);
	}
	
	public boolean isEnumeration() {
		return Enumeration != null;
	}
	
	@Override
	public String toString() {
		return "ValueType "+Id;
	}
}
