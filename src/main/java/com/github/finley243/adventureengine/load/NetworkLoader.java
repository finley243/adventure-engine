package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.network.ControlNetworkNode;
import com.github.finley243.adventureengine.network.DataNetworkNode;
import com.github.finley243.adventureengine.network.GroupNetworkNode;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NetworkLoader {

    private static final String NAME_NETWORK_NODE = "node";

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
                return new DataNetworkNode(ID, name, dataSceneID);
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
            default -> throw new GameDataException("NetworkNode has invalid type");
        }
    }

}
