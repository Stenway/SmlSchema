package com.stenway.sml.schema;

public class SsAttribute extends SsItem {
	public String Name;
	public SsPredefinedValueType PredefinedValueType;
	public SsStruct Struct;
	public SsValueType ValueType;
	public boolean Nullable;
	public SsBounds ArrayBounds;
	public boolean ArrayNullable;
	
	public SsAttribute(String id) {
		super(id);
	}
	
	public boolean isArray() {
		return ArrayBounds != null;
	}
	
	public boolean isPredefinedValueType() {
		return PredefinedValueType != null;
	}
	
	public boolean isStruct() {
		return Struct != null;
	}
	
	public boolean isValueType() {
		return ValueType != null;
	}
	
	@Override
	public String toString() {
		return "Attribute "+Id;
	}
}
