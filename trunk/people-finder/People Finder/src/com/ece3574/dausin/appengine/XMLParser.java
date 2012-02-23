package com.ece3574.dausin.appengine;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {
	
	public static HashMap<String,String> parseUidPackagePairsXML(String xml) {
		HashMap<String,String> pairs = new HashMap<String,String>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			InputSource source = new InputSource(new StringReader(xml));
			Document doc = dbFactory.newDocumentBuilder().parse(source);
			doc.getDocumentElement().normalize();
			
			NodeList eventList = doc.getElementsByTagName("pair");

			for(int temp = 0; temp < eventList.getLength(); ++temp) {
				String app = "";
				String uid = "";
				Node nNode = eventList.item(temp);
				if(nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					app = getTagValue("app",eElement);
					uid = getTagValue("uid",eElement);
				}
				pairs.put(uid, app);			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pairs;
	}
	
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		
		Node nValue = (Node) nlList.item(0);
		if(nValue != null ) {
			return nValue.getNodeValue();
		} else {
			return "";
		}
	}

}
