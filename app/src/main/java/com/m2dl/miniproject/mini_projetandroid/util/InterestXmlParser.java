package com.m2dl.miniproject.mini_projetandroid.util;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by marine on 19/01/15.
 */
public class InterestXmlParser {
    private Map<String, String> elements;
    private Map<String, List<String>> elementDescendants;
    private Document document;
    private List<String> elementIDs;

    public InterestXmlParser(InputStream stream) {
        document = getDocument(stream);
        elements = new HashMap<>();
        elementDescendants = new HashMap<>();
        elementIDs = new ArrayList<>();

        fillElements();
        fillElementDescendants();
    }

    private Document getDocument(InputStream inputStream) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(inputStream);
            document = db.parse(inputSource);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return document;
    }

    private void fillElements() {
        NodeList allElements = document.getElementsByTagName("element");
        String attributeName;
        String elementName;
        for (int i = 0; i < allElements.getLength(); i++) {
            attributeName = getAttributeByName(allElements.item(i), "id");
            elementName = getFirstLevelTextContent(allElements.item(i));
            elements.put(attributeName, elementName);
            elementIDs.add(attributeName);
        }
    }

    private void fillElementDescendants() {
        for (int i = 0; i < elementIDs.size(); i++) {
            elementDescendants.put( elementIDs.get(i), getDescendants(document.getElementById(elementIDs.get(i))));
        }
    }

    private List<String> getDescendants(Element e) {
        NodeList childrenList = e.getChildNodes();
        List<String> children = new ArrayList<>();
        Node currentNode;
        for (int i = 1; i < childrenList.getLength(); i++) {
            currentNode = childrenList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                children.add(getAttributeByName(childrenList.item(i),"id"));
            }
        }

        return children;
    }

    private String getFirstLevelTextContent(Node node) {
        return node.getChildNodes().item(0).getTextContent().replaceAll("(\\r|\\n)", "");
    }

    private String getAttributeByName(Node node, String attrName) {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals(attrName)) {
                return attributes.item(i).getNodeValue();
            }
        }
        return null;
    }

    public List<String> getDescendantsForElement(String elementId) {
        return elementDescendants.get(elementId);
    }

    public String getElementName(String elementId) {
        return elements.get(elementId);
    }

    public List<String> getFirstLevelElements() {
        return getDescendants(document.getDocumentElement());
    }
}
