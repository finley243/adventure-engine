package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.gamedata.ConfigHandler;
import com.github.finley243.adventureengine.gamedata.ConfigOption;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
	
	public Map<ConfigOption, String> loadConfig(File file) throws GameDataException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new GameDataException("Could not initialize config parser");
        }
        Document document;
        try {
            document = builder.parse(file);
        } catch (SAXException | IOException e) {
            throw new GameDataException("Could not parse config file: " + file.getAbsolutePath());
        }
        Element rootElement = document.getDocumentElement();
		Map<ConfigOption, String> configMap = new HashMap<>();
		for (ConfigOption configOption : ConfigOption.values()) {
			String configName = configOption.dataName;
			String value = LoadUtils.singleTag(rootElement, configName, null);
			if (value == null) throw new GameDataException("Config file missing value: " + configName + " - file: " + file.getAbsolutePath());
			configMap.put(configOption, value);
		}
		return configMap;
	}

}
