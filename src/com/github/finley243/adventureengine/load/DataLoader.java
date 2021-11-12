package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.LootTable;
import com.github.finley243.adventureengine.world.template.StatsActor;
import com.github.finley243.adventureengine.world.template.StatsItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataLoader {

    public static void loadFromDir(File dir) throws ParserConfigurationException, IOException, SAXException {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(File file : files) {
                System.out.println("Loading file: " + file.getName());
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element rootElement = document.getDocumentElement();
                List<Element> factions = LoadUtils.directChildrenWithName(rootElement, "faction");
                for (Element factionElement : factions) {
                    Faction faction = FactionLoader.loadFaction(factionElement);
                    Data.addFaction(faction.getID(), faction);
                }
                List<Element> topics = LoadUtils.directChildrenWithName(rootElement, "topic");
                for (Element topicElement : topics) {
                    DialogueTopic topic = DialogueLoader.loadTopic(topicElement);
                    Data.addTopic(topic.getID(), topic);
                }
                List<Element> actors = LoadUtils.directChildrenWithName(rootElement, "actor");
                for (Element actorElement : actors) {
                    StatsActor actor = ActorLoader.loadActor(actorElement);
                    Data.addActorStats(actor.getID(), actor);
                }
                List<Element> items = LoadUtils.directChildrenWithName(rootElement, "item");
                for (Element itemElement : items) {
                    StatsItem item = ItemLoader.loadItem(itemElement);
                    Data.addItem(item.getID(), item);
                }
                List<Element> tables = LoadUtils.directChildrenWithName(rootElement, "lootTable");
                for (Element tableElement : tables) {
                    LootTable table = LootTableLoader.loadTable(tableElement);
                    Data.addLootTable(table.getID(), table);
                }
                List<Element> scenes = LoadUtils.directChildrenWithName(rootElement, "scene");
                for (Element sceneElement : scenes) {
                    Scene scene = SceneLoader.loadScene(sceneElement);
                    Data.addScene(scene.getID(), scene);
                }
                List<Element> rooms = LoadUtils.directChildrenWithName(rootElement, "room");
                for (Element roomElement : rooms) {
                    Room room = WorldLoader.loadRoom(roomElement);
                    Data.addRoom(room.getID(), room);
                }
            }
        }
    }

}
