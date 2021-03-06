/*
 * Copyright (c) 2015 Marine Carrara, Akana Mao, Randy Ratsimbazafy
 *
 * This file is part of Biodiversity.
 *
 * Biodiversity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Biodiversity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Biodiversity.  If not, see <http://www.gnu.org/licenses/>.
 */

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
 * Parseur XML de fichier de clés de détermination
 * Created by marine on 19/01/15.
 */
public class InterestXmlParser {
    /**
     * Textes et identifiants des éléments
     */
    private Map<String, String> elements;
    /**
     * Eléments et déscendants de l'élément
     */
    private Map<String, List<String>> elementDescendants;
    /**
     * Document XML
     */
    private Document document;
    /**
     * Identifiants de l'élément
     */
    private List<String> elementIDs;

    /**
     * Constructeur
     * @param stream inputstream du fichier XML
     */
    public InterestXmlParser(InputStream stream) {
        document = getDocument(stream);
        elements = new HashMap<>();
        elementDescendants = new HashMap<>();
        elementIDs = new ArrayList<>();

        fillElements();
        fillElementDescendants();
    }

    /**
     * Obtention du document XML
     * @param inputStream inputstream du fichier XML
     * @return
     */
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

    /**
     * Remplissage de la map d'identifiants avec les éléments
     */
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

    /**
     * Remplissage de la map avec les éléments et leurs descendants
     */
    private void fillElementDescendants() {
        for (int i = 0; i < elementIDs.size(); i++) {
            elementDescendants.put( elementIDs.get(i), getDescendants(document.getElementById(elementIDs.get(i))));
        }
    }

    /**
     * Obtenir les descendants d'un élément
     * @param e élément dont on souhaite obtenir les descendants
     * @return les descendants de l'élément
     */
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

    /**
     * Obtention du texte d'un noeud
     * @param node noeud sur lequel on souhaite obtenir le texte
     * @return le texte d'un noeud
     */
    private String getFirstLevelTextContent(Node node) {
        return node.getChildNodes().item(0).getTextContent().replaceAll("(\\r|\\n)", "");
    }

    /**
     * Obtention d'un attribut
     * @param node noeud sur lequel on souhaite avoir un attribut
     * @param attrName nom de l'attribut à obtenir
     * @return la valeur de l'attribut
     */
    private String getAttributeByName(Node node, String attrName) {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals(attrName)) {
                return attributes.item(i).getNodeValue();
            }
        }
        return null;
    }

    /**
     * Obtenir les descendants d'un élément
     * @param elementId identifiant de l'élément
     * @return les descendants de l'élément
     */
    public List<String> getDescendantsForElement(String elementId) {
        return elementDescendants.get(elementId);
    }

    /**
     * Obtenir le nom d'un élément
     * @param elementId identifiant de l'élément
     * @return nom de l'élément
     */
    public String getElementName(String elementId) {
        return elements.get(elementId);
    }

    /**
     * Obtenir les éléments de premier niveau de l'arbre
     * @return les éléments de premier niveau de l'arbre
     */
    public List<String> getFirstLevelElements() {
        return getDescendants(document.getDocumentElement());
    }
}
