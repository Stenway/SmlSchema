package com.stenway.sml.schema;

import java.util.ArrayList;

public class SsElement extends SsItem {
	public String Name;
	
	public ArrayList<SsChild> UnorderedChildren = new ArrayList<>();
	
	public SsElement Synonym;
	
	public SsElement(String id) {
		super(id);
	}

	@Override
	public String toString() {
		return "Element "+Id;
	}
}
