package com.stenway.sml.schema;

public class SsNamespace {
	public String Prefix;
	
	public SsItems<SsElement> Elements = new SsItems<>("element", this);
	public SsItems<SsAttribute> Attributes = new SsItems<>("attribute", this);
	public SsItems<SsStruct> Structs = new SsItems<>("struct", this);
	public SsItems<SsValueType> ValueTypes = new SsItems<>("value type", this);
	
	public SsNamespace(String name) {
		Prefix = name;
	}

	@Override
	public String toString() {
		return "Namespace "+Prefix;
	}
}
