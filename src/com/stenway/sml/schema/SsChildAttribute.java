package com.stenway.sml.schema;

public class SsChildAttribute extends SsBoundChild {
	public SsAttribute Attribute;

	@Override
	public String toString() {
		String prefix = "Attribute";
		if (Occurrence.isOptional()) {
			prefix = "OptionalAttribute";
		} else if (Occurrence.isRequired()) {
			prefix = "RequiredAttribute";
		} else if (Occurrence.isRepeatStar()) {
			prefix = "RepeatedAttribute*";
		}
		return prefix + " " + Attribute.Id;
	}
}
