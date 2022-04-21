package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.dialogue.DialogueTopic;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.item.ItemFactory;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {

    public enum DataType {
        AREA, ROOM, ACTOR, OBJECT, TOPIC, VARIABLE, SCENE, ITEM_STATS, ITEM_INSTANCE, TIME
    }

    private enum ValueType {
        STRING, INT, BOOL, MULTI
    }

    private final DataType type;
    private final String id;
    private final String parameter;

    private final ValueType valueType;
    private final String valueString;
    private final int valueInt;
    private final boolean valueBoolean;
    private final List<SaveData> valueMulti;

    public SaveData(DataType type, String id, String parameter, String valueString) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.STRING;
        this.valueString = valueString;
        this.valueInt = 0;
        this.valueBoolean = false;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, int valueInt) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.INT;
        this.valueString = null;
        this.valueInt = valueInt;
        this.valueBoolean = false;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, boolean valueBoolean) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.BOOL;
        this.valueString = null;
        this.valueInt = 0;
        this.valueBoolean = valueBoolean;
        this.valueMulti = null;
    }

    public SaveData(DataType type, String id, String parameter, List<SaveData> valueMulti) {
        this.type = type;
        this.id = id;
        this.parameter = parameter;
        this.valueType = ValueType.MULTI;
        this.valueString = null;
        this.valueInt = 0;
        this.valueBoolean = false;
        this.valueMulti = valueMulti;
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
                ItemFactory.load(data.game(), valueString, id);
                break;
            case ITEM_STATS:
                ItemTemplate itemTemplate = data.getItem(id);
                itemTemplate.loadState(this);
                break;
            case TIME:
                data.time().loadState(this);
                break;
        }
    }

    public boolean isItemInstance() {
        return type == DataType.ITEM_INSTANCE;
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

    public List<SaveData> getValueMulti() {
        if(valueType != ValueType.MULTI) throw new UnsupportedOperationException("Cannot get multi value from non-multi SaveData.");
        return valueMulti;
    }

}
