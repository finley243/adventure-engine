package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.ItemFactory;
import com.github.finley243.adventureengine.world.item.stats.StatsItem;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.io.Serializable;

public class SaveData implements Serializable {

    public enum DataType {
        AREA, ROOM, ACTOR, OBJECT, TOPIC, VARIABLE, SCENE, ITEM_STATS, ITEM_INSTANCE
    }

    private enum ValueType {
        STRING, INT, BOOL
    }

    private final DataType type;
    private final String id;
    private final String parameter;

    private final ValueType valueType;
    private final String valueString;
    private final int valueInt;
    private final boolean valueBoolean;

    public SaveData(DataType type, String id, String parameter, String valueString) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.STRING;
        this.valueString = valueString;
        this.valueInt = 0;
        this.valueBoolean = false;
    }

    public SaveData(DataType type, String id, String parameter, int valueInt) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.INT;
        this.valueString = null;
        this.valueInt = valueInt;
        this.valueBoolean = false;
    }

    public SaveData(DataType type, String id, String parameter, boolean valueBoolean) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.INT;
        this.valueString = null;
        this.valueInt = 0;
        this.valueBoolean = valueBoolean;
    }

    public void apply(Data data) {
        switch(type) {
            case AREA:
                Area area = data.getArea(id);
                area.loadState(this);
                break;
            case ROOM:
                Room room = data.getRoom(id);
                room.loadState(this);
                break;
            case ACTOR:
                Actor actor = data.getActor(id);
                actor.loadState(this);
                break;
            case OBJECT:
                WorldObject object = data.getObject(id);
                object.loadState(this);
                break;
            case TOPIC:
                DialogueTopic topic = data.getTopic(id);
                topic.loadState(this);
                break;
            case VARIABLE:
                data.setVariable(id, valueInt);
                break;
            case SCENE:
                Scene scene = data.getScene(id);
                scene.loadState(this);
                break;
            case ITEM_INSTANCE:
                data.addObject(id, ItemFactory.create(data.game(), valueString, id));
                break;
            case ITEM_STATS:
                StatsItem statsItem = data.getItem(id);
                statsItem.loadState(this);
                break;
        }
    }

    public String getParameter() {
        return parameter;
    }

    public String getValueString() {
        if(valueType != ValueType.STRING) throw new UnsupportedOperationException("Cannot get String value from non-String SaveData.");
        return valueString;
    }

    public int getValueInt() {
        if(valueType != ValueType.INT) throw new UnsupportedOperationException("Cannot get integer value from non-integer SaveData.");
        return valueInt;
    }

    public boolean getValueBoolean() {
        if(valueType != ValueType.BOOL) throw new UnsupportedOperationException("Cannot get boolean value from non-boolean SaveData.");
        return valueBoolean;
    }

}
