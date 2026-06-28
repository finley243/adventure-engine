package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.network.ControlNetworkNode;
import com.github.finley243.adventureengine.network.DataNetworkNode;
import com.github.finley243.adventureengine.network.GroupNetworkNode;
import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.scene.Scene;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NetworkLoader {

    private static final String NAME_NETWORK_NODE = "node";

    private final Registry<Scene> sceneRegistry;

    public NetworkLoader(Registry<Scene> sceneRegistry) {
        this.sceneRegistry = sceneRegistry;
    }

    public Map<String, NetworkNode> load(Element element) {
        return LoadUtils.loadAll(element, NAME_NETWORK_NODE, this::parseNetworkNode, NetworkNode::getID);
    }

    private NetworkNode parseNetworkNode(Element element) {
        String type = LoadUtils.attribute(element, "type", null);
        String ID = LoadUtils.attribute(element, "id", null);
        String name = LoadUtils.singleTag(element, "name", null);
        switch (type) {
            case "data" -> {
                String dataSceneID = LoadUtils.attribute(element, "scene", null);
                Scene dataScene = sceneRegistry.getFromID(dataSceneID);
                if (dataScene == null) throw new GameDataException("DataNetworkNode has invalid scene reference: " + dataSceneID);
                return new DataNetworkNode(ID, name, dataScene);
            }
            case "control" -> {
                String controlObjectID = LoadUtils.attribute(element, "object", null);
                return new ControlNetworkNode(ID, name, controlObjectID);
            }
            case null -> {
                Set<NetworkNode> groupNodes = new HashSet<>();
                for (Element childNodeElement : LoadUtils.directChildrenWithName(element, "node")) {
                    NetworkNode childNode = parseNetworkNode(childNodeElement);
                    groupNodes.add(childNode);
                }
                return new GroupNetworkNode(ID, name, groupNodes);
            }
            default -> throw new GameDataException("NetworkNode has invalid type: " + type);
        }
    }

}
