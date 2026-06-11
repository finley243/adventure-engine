package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.network.NetworkNode;
import com.github.finley243.adventureengine.network.NetworkNodeControl;
import com.github.finley243.adventureengine.network.NetworkNodeData;
import com.github.finley243.adventureengine.network.NetworkNodeGroup;
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
                return new NetworkNodeData(ID, name, dataSceneID);
            }
            case "control" -> {
                String controlObjectID = LoadUtils.attribute(element, "object", null);
                return new NetworkNodeControl(ID, name, controlObjectID);
            }
            case null, default -> {
                Set<NetworkNode> groupNodes = new HashSet<>();
                for (Element childNodeElement : LoadUtils.directChildrenWithName(element, "node")) {
                    NetworkNode childNode = parseNetworkNode(childNodeElement);
                    groupNodes.add(childNode);
                }
                return new NetworkNodeGroup(ID, name, groupNodes);
            }
        }
    }

}
