package com.stenway.sml.schema;

public class SsBoundChild extends SsChild {
	public SsBounds Occurrence = new SsBounds();
	
	public void setOccurrence(Integer min, Integer max) {
		Occurrence = new SsBounds(min,max);
	}
}
