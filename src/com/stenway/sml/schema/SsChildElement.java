package com.stenway.sml.schema;

public class SsChildElement extends SsBoundChild {
	public SsElement Element;

	@Override
	public String toString() {
		String prefix = "Element";
		if (Occurrence.isOptional()) {
			prefix = "OptionalElement";
		} else if (Occurrence.isRequired()) {
			prefix = "RequiredElement";
		} else if (Occurrence.isRepeatStar()) {
			prefix = "RepeatedElement*";
		}
		return prefix + " " + Element.Id;
	}
}
