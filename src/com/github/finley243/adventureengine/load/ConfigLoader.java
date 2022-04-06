package com.github.finley243.adventureengine.load;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.github.finley243.adventureengine.Game;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.github.finley243.adventureengine.Data;

public class ConfigLoader {

	private static final String[] CONFIG_LIST = new String[] {"playerStats", "playerID", "playerStartArea", "gameName", "startTimeHours", "startTimeMinutes"};
	
	public static void loadConfig(Game game, File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		Element rootElement = document.getDocumentElement();
		for(String configName : CONFIG_LIST) {
			String value = LoadUtils.singleTag(rootElement, configName, null);
			game.data().addConfig(configName, value);
		}
	}

}
