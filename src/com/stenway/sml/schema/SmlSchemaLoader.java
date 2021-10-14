package com.stenway.sml.schema;

import com.stenway.sml.SmlAttribute;
import com.stenway.sml.SmlDocument;
import com.stenway.sml.SmlElement;
import com.stenway.sml.SmlNamedNode;
import com.stenway.sml.SmlNode;
import java.io.IOException;
import java.util.HashMap;

public class SmlSchemaLoader {
	SmlSchema schema;
	HashMap<SmlNode, SsItem> items = new HashMap<>();
	HashMap<SmlElement, SsNamespace> namespaces = new HashMap<>();
	
	private void loadChildAttribute(SmlAttribute attribute, SsElement ssElement, SsNamespace namespace) {
		String id = attribute.getString();
		SsChildAttribute childAttribute = new SsChildAttribute();
		childAttribute.Attribute = schema.getAttribute(id, namespace);
		if (attribute.hasName("OptionalAttribute")) {
			childAttribute.Occurrence.setOptional();
		} else if (attribute.hasName("RequiredAttribute")) {
			childAttribute.Occurrence.setRequired();
		} else if (attribute.hasName("RepeatedAttribute*")) {
			childAttribute.Occurrence.setRepeatStar();
		} else if (attribute.hasName("RepeatedAttribute+")) {
			childAttribute.Occurrence.setRepeatPlus();
		}
		ssElement.UnorderedChildren.add(childAttribute);
	}
	
	private void loadChildElement(SmlAttribute attribute, SsElement ssElement, SsNamespace namespace) {
		String id = attribute.getString();
		SsChildElement childElement = new SsChildElement();
		childElement.Element = schema.getElement(id, namespace);
		if (attribute.hasName("OptionalElement")) {
			childElement.Occurrence.setOptional();
		} else if (attribute.hasName("RequiredElement")) {
			childElement.Occurrence.setRequired();
		} else if (attribute.hasName("RepeatedElement*")) {
			childElement.Occurrence.setRepeatStar();
		} else if (attribute.hasName("RepeatedElement+")) {
			childElement.Occurrence.setRepeatPlus();
		}
		ssElement.UnorderedChildren.add(childElement);
	}
	
	private void loadChildList(SsElement ssElement, SmlElement listElement, SsNamespace namespace) {
		SsChildList childList = new SsChildList();
		if (listElement.hasAttribute("Name")) {
			childList.Name = listElement.getString("Name");
		}
		
		for (SmlAttribute attribute : listElement.attributes()) {
			if (attribute.hasName("Element")) {
				String id = attribute.getString();
				SsElement childElement = schema.getElement(id, namespace);
				childList.Items.add(childElement);
			} else if (attribute.hasName("Attribute")) {
				String id = attribute.getString();
				SsAttribute childAttribute = schema.getAttribute(id, namespace);
				childList.Items.add(childAttribute);
			}
		}
	
		ssElement.UnorderedChildren.add(childList);
	}
	
	private void loadSynonym(SsElement ssElement, SmlAttribute synonymAttribute, SsNamespace namespace) {
		String id = synonymAttribute.getString();
		SsElement synonymElement = schema.getElement(id, namespace);
		ssElement.Synonym = synonymElement;
	}
	
	private void loadElement(SmlElement elementElement, SsElement ssElement, SsNamespace namespace) {
		ssElement.Name = elementElement.getString("Name", ssElement.Id);
		
		for (SmlAttribute attribute : elementElement.attributes()) {
			if (attribute.hasName("RequiredAttribute") || attribute.hasName("OptionalAttribute") ||
					attribute.hasName("RepeatedAttribute*") ||
					attribute.hasName("RepeatedAttribute+")) {
				loadChildAttribute(attribute, ssElement, namespace);
			}
		}
		
		for (SmlAttribute attribute : elementElement.attributes()) {
			if (attribute.hasName("RequiredElement") || attribute.hasName("OptionalElement") ||
					attribute.hasName("RepeatedElement*")||
					attribute.hasName("RepeatedElement+")) {
				loadChildElement(attribute, ssElement, namespace);
			}
		}
		
		if (elementElement.hasElement("List")) {
			loadChildList(ssElement, elementElement.element("List"), namespace);
		}
		
		if (elementElement.hasAttribute("Synonym")) {
			loadSynonym(ssElement, elementElement.attribute("Synonym"), namespace);
		}
	}
	
	private SsPredefinedValueType tryGetPredefinedValueType(String type) {
		if (type.equalsIgnoreCase("String")) { return SsPredefinedValueType.String; }
		else if (type.equalsIgnoreCase("Bool")) { return SsPredefinedValueType.Bool; }
		return null;
	}
	
	private void setAttributeType(SsAttribute ssAttribute, String type, SsNamespace namespace) {
		if (type.endsWith("]?")) {
			type = type.substring(0, type.length()-1);
			ssAttribute.ArrayNullable = true;
		}
		if (type.endsWith("]")) {
			int index = type.indexOf('[');
			String arrayPart = type.substring(index+1, type.length()-1);
			type = type.substring(0, index);
			ssAttribute.ArrayBounds = new SsBounds();
			if (arrayPart.length() > 0) {
				if (arrayPart.contains("..")) {
					String[] parts = arrayPart.split("..");
					int minSize = Integer.parseInt(parts[0]);
					int maxSize = Integer.parseInt(parts[1]);
					ssAttribute.ArrayBounds.set(minSize, maxSize);
				} else {
					int size = Integer.parseInt(arrayPart);
					ssAttribute.ArrayBounds.setFixedSize(size);
				}
			}
		}
		if (type.endsWith("?")) {
			ssAttribute.Nullable = true;
			type = type.substring(0, type.length()-1);
		}
		SsPredefinedValueType predefinedValueType = tryGetPredefinedValueType(type);
		if (predefinedValueType != null) {
			ssAttribute.PredefinedValueType = predefinedValueType;
			return;
		}
		SsValueType valueType = schema.getValueType(type, namespace);
		if (valueType != null) {
			ssAttribute.ValueType = valueType;
			return;
		}
		SsStruct struct = schema.getStruct(type, namespace);
		if (struct != null) {
			ssAttribute.Struct = struct;
			return;
		}
		throw new RuntimeException("Todo");
	}
	
	private void loadAttribute(SmlElement attributeElement, SsAttribute ssAttribute, SsNamespace namespace) {
		ssAttribute.Name = attributeElement.getString("Name", ssAttribute.Id);
		String type = attributeElement.getString("Value");
		setAttributeType(ssAttribute, type, namespace);
	}
		
	private void loadAttribute(SmlAttribute attributeAttribute, SsAttribute ssAttribute, SsNamespace namespace) {
		ssAttribute.Name = ssAttribute.Id;
		String type = attributeAttribute.getString(1);
		setAttributeType(ssAttribute, type, namespace);
	}
	
	private void loadStruct(SmlElement structElement, SsStruct ssStruct, SsNamespace namespace) {
		throw new RuntimeException("Todo");
	}
	
	private void loadValueType(SmlElement valueTypeElement, SsValueType ssValueType, SsNamespace namespace) {
		throw new RuntimeException("Todo");
	}
	
	private void loadValueType(SmlAttribute valueTypeAttribute, SsValueType ssValueType, SsNamespace namespace) {
		if (valueTypeAttribute.getString(1).equalsIgnoreCase("Enum")) {
			SsEnum enumeration = new SsEnum();
			enumeration.IsCaseSensitive = true;
			String[] values = valueTypeAttribute.getValues();
			for (int i=2; i<values.length; i++) {
				enumeration.Values.add(values[i]);
			}
			ssValueType.Enumeration = enumeration;
		} else {
			throw new RuntimeException("Todo");
		}
	}
	
	private void loadNamespace(SmlElement parentElement, SsNamespace namespace) {
		for (SmlElement elementElement : parentElement.elements("Element")) {
			SsElement ssElement = (SsElement)items.get(elementElement);
			loadElement(elementElement, ssElement, namespace);
		}
		
		for (SmlElement attributeElement : parentElement.elements("Attribute")) {
			SsAttribute ssAttribute = (SsAttribute)items.get(attributeElement);
			loadAttribute(attributeElement, ssAttribute, namespace);
		}
		for (SmlAttribute attributeAttribute : parentElement.attributes("Attribute")) {
			SsAttribute ssAttribute = (SsAttribute)items.get(attributeAttribute);
			loadAttribute(attributeAttribute, ssAttribute, namespace);
		}
		
		for (SmlElement structElement : parentElement.elements("Struct")) {
			SsStruct ssStruct = (SsStruct)items.get(structElement);
			loadStruct(structElement, ssStruct, namespace);
		}
		
		for (SmlElement valueTypeElement : parentElement.elements("ValueType")) {
			SsValueType ssValueType = (SsValueType)items.get(valueTypeElement);
			loadValueType(valueTypeElement, ssValueType, namespace);
		}
		for (SmlAttribute valueTypeAttribute : parentElement.attributes("ValueType")) {
			SsValueType ssValueType = (SsValueType)items.get(valueTypeAttribute);
			loadValueType(valueTypeAttribute, ssValueType, namespace);
		}
	}
	
	private void load(SmlElement parentElement) {
		SsNamespace rootNamespace = namespaces.get(parentElement);
		loadNamespace(parentElement, rootNamespace);
		
		for (SmlElement namespaceElement : parentElement.elements("Namespace")) {
			SsNamespace namespace = namespaces.get(namespaceElement);
			loadNamespace(namespaceElement, namespace);
		}
	}
	
	private String getId(SmlElement element) {
		if (element.hasAttribute("Id")) {
			return element.getString("Id");
		}
		return element.getString("Name");
	}
	
	private String getId(SmlNamedNode namedNode) {
		if (namedNode instanceof SmlElement) {
			return getId((SmlElement)namedNode);
		} else {
			return ((SmlAttribute)namedNode).getString();
		}
	}
	
	private void preloadNamespace(SmlElement parentElement, SsNamespace namespace) {
		for (SmlElement elementElement : parentElement.elements("Element")) {
			String id = getId(elementElement);
			String globalId = namespace.Prefix+id;
			if (schema.hasElement(globalId)) {
				throw new SmlSchemaException("Element with global ID '"+globalId+"' already exists.");
			}
			SsElement ssElement = new SsElement(id);
			namespace.Elements.add(ssElement);
			items.put(elementElement, ssElement);
		}
		for (SmlNamedNode attributeNode : parentElement.nodes("Attribute")) {
			String id = getId(attributeNode);
			String globalId = namespace.Prefix+id;
			if (schema.hasAttribute(globalId)) {
				throw new SmlSchemaException("Attribute with global ID '"+globalId+"' already exists.");
			}
			SsAttribute ssAttribute = new SsAttribute(id);
			namespace.Attributes.add(ssAttribute);
			items.put(attributeNode, ssAttribute);
		}
		for (SmlElement structElement : parentElement.elements("Struct")) {
			String id = getId(structElement);
			String globalId = namespace.Prefix+id;
			if (schema.hasStruct(globalId)) {
				throw new SmlSchemaException("Struct with global ID '"+globalId+"' already exists.");
			}
			SsStruct ssStruct = new SsStruct(id);
			namespace.Structs.add(ssStruct);
			items.put(structElement, ssStruct);
		}
		for (SmlNamedNode valueTypeNode : parentElement.nodes("ValueType")) {
			String id = getId(valueTypeNode);
			String globalId = namespace.Prefix+id;
			if (schema.hasValueType(globalId)) {
				throw new SmlSchemaException("Value type with global ID '"+globalId+"' already exists.");
			}
			if (namespace.Structs.hasItem(id, false) || schema.hasStruct(globalId)) {
				throw new SmlSchemaException("Struct with global ID '"+globalId+"' already exists.");
			}
			SsValueType ssValueType = new SsValueType(id);
			namespace.ValueTypes.add(ssValueType);
			items.put(valueTypeNode, ssValueType);
		}
	}
	
	private void preload(SmlElement parentElement) {
		SsNamespace rootNamespace = new SsNamespace("");
		schema.Namespaces.add(rootNamespace);
		namespaces.put(parentElement, rootNamespace);
		preloadNamespace(parentElement, rootNamespace);
		
		for (SmlElement namespaceElement : parentElement.elements("Namespace")) {
			String prefix = namespaceElement.getString("Prefix", "");
			SsNamespace namespace = new SsNamespace(prefix);
			schema.Namespaces.add(namespace);
			namespaces.put(namespaceElement, namespace);
			preloadNamespace(namespaceElement, namespace);
		}
	}
	
	private void loadRootElement(SmlElement rootElement) {
		if (!rootElement.hasName("Schema")) {
			throw new SmlSchemaException("Not a schema file.");
		}
		
		preload(rootElement);
		load(rootElement);	
		
		String rootElementId = rootElement.getString("RootElement");
		schema.RootElement = schema.getElement(rootElementId, null);
	}
	
	public SmlSchema parse(SmlDocument document) {
		schema = new SmlSchema();
		loadRootElement(document.getRoot());
		return schema;
	}
	
	public SmlSchema load(String filePath) throws IOException {
		SmlDocument document = SmlDocument.load(filePath);
		return parse(document);
	}
}
