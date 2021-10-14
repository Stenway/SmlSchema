package com.stenway.sml.schema;

import com.stenway.sml.SmlDocument;
import java.io.IOException;
import java.util.ArrayList;

public class SmlSchema {
	public SsElement RootElement;
	public ArrayList<SsNamespace> Namespaces = new ArrayList<>();
	
	public static SmlSchema parse(String content) throws IOException {
		SmlDocument document = SmlDocument.parse(content);
		return new SmlSchemaLoader().parse(document);
	}
	
	public static SmlSchema load(String filePath) throws IOException {
		return new SmlSchemaLoader().load(filePath);
	}
	
	public boolean hasElement(String globalId) {
		for (SsNamespace namespace : Namespaces) {
			if (namespace.Elements.hasItem(globalId, true)) {
				return true;
			}
		}
		return false;
	}
	
	public SsElement getElement(String id, SsNamespace namespace) {
		if (namespace != null && namespace.Elements.hasItem(id, false)) {
			return namespace.Elements.getItemOrNull(id, false);
		}
		for (SsNamespace curNamespace : Namespaces) {
			if (curNamespace.Elements.hasItem(id, true)) {
				return curNamespace.Elements.getItemOrNull(id, true);
			}
		}
		throw new SmlSchemaException("Element with global ID '"+id+"' could not be found");
	}
	
	public boolean hasAttribute(String globalId) {
		for (SsNamespace namespace : Namespaces) {
			if (namespace.Attributes.hasItem(globalId, true)) {
				return true;
			}
		}
		return false;
	}
	
	public SsAttribute getAttribute(String id, SsNamespace namespace) {
		if (namespace != null && namespace.Attributes.hasItem(id, false)) {
			return namespace.Attributes.getItemOrNull(id, false);
		}
		for (SsNamespace curNamespace : Namespaces) {
			if (curNamespace.Attributes.hasItem(id, true)) {
				return curNamespace.Attributes.getItemOrNull(id, true);
			}
		}
		throw new SmlSchemaException("Attribute with global ID '"+id+"' could not be found");
	}
	
	public boolean hasStruct(String globalId) {
		for (SsNamespace namespace : Namespaces) {
			if (namespace.Structs.hasItem(globalId, true)) {
				return true;
			}
		}
		return false;
	}
	
	public SsStruct getStruct(String id, SsNamespace namespace) {
		if (namespace != null && namespace.Structs.hasItem(id, false)) {
			return namespace.Structs.getItemOrNull(id, false);
		}
		for (SsNamespace curNamespace : Namespaces) {
			if (curNamespace.Structs.hasItem(id, true)) {
				return curNamespace.Structs.getItemOrNull(id, true);
			}
		}
		throw new SmlSchemaException("Struct with global ID '"+id+"' could not be found");
	}
	
	public boolean hasValueType(String globalId) {
		for (SsNamespace namespace : Namespaces) {
			if (namespace.ValueTypes.hasItem(globalId, true)) {
				return true;
			}
		}
		return false;
	}
	
	public SsValueType getValueType(String id, SsNamespace namespace) {
		if (namespace != null && namespace.ValueTypes.hasItem(id, false)) {
			return namespace.ValueTypes.getItemOrNull(id, false);
		}
		for (SsNamespace curNamespace : Namespaces) {
			if (curNamespace.ValueTypes.hasItem(id, true)) {
				return curNamespace.ValueTypes.getItemOrNull(id, true);
			}
		}
		throw new SmlSchemaException("Value type with global ID '"+id+"' could not be found");
	}
}
