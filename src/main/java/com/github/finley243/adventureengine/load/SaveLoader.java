package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.GameDataException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;

public class SaveLoader {

    public static void saveGame(File saveFile, Data data) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(saveFile);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
        List<SaveData> saveData = data.saveState();
        objectStream.writeObject(saveData);
        objectStream.close();
        fileStream.close();
    }

    public static void loadGame(File saveFile, Data data) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException, GameDataException {
        FileInputStream fileStream = new FileInputStream(saveFile);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);
        @SuppressWarnings("unchecked")
        List<SaveData> saveData = (List<SaveData>) objectStream.readObject();
        objectStream.close();
        fileStream.close();
        data.loadState(saveData);
    }

}
