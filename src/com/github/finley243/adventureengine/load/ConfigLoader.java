package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ConfigLoader {

	private static final String[] CONFIG_LIST = new String[] {"playerStats", "playerID", "playerStartArea", "gameName", "defaultDamageType", "startTimeHours", "startTimeMinutes", "startDateYear", "startDateMonth", "startDateDay", "startDateWeekday", "interfaceType", "defaultLinkType"};
	
	public static void loadConfig(Game game, File file) throws ParserConfigurationException, SAXException, IOException, GameDataException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		Element rootElement = document.getDocumentElement();
		for (String configName : CONFIG_LIST) {
			String value = LoadUtils.singleTag(rootElement, configName, null);
			if (value == null) throw new GameDataException("Missing config value: " + configName);
			game.data().addConfig(configName, value);
		}
	}

}
