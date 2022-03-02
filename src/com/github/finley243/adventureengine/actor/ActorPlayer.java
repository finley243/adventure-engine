package com.github.finley243.adventureengine.actor;

import java.util.List;
import java.util.Map;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	private final MenuManager menuManager;
	
	public ActorPlayer(String ID, Area area, StatsActor stats) {
		super(ID, area, stats, null, null, false, false, false);
		this.menuManager = new MenuManager();
		Game.EVENT_BUS.register(menuManager);
	}
	
	@Override
	public void onVisualEvent(VisualEvent event) {
		Game.EVENT_BUS.post(new RenderTextEvent(event.getText()));
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void move(Area area) {
		boolean shouldShowDescription = getArea() != null;
		boolean newRoom = !getArea().getRoom().equals(area.getRoom());
		super.move(area);
		Game.EVENT_BUS.post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
		if(shouldShowDescription) {
			this.updateAreaDescription();
			this.describeSurroundings();
		}
		if(newRoom) {
			getArea().getRoom().triggerScript("on_player_enter", this);
		}
		getArea().triggerScript("on_player_enter", this);
	}
	
	@Override
	public void kill() {
		super.kill();
		Game.EVENT_BUS.post(new PlayerDeathEvent());
	}
	
	@Override
	public Action chooseAction(List<Action> actions) {
		return menuManager.actionMenu(actions, this);
	}
	
	public void startDialogue(Actor subject, String startTopic) {
		menuManager.dialogueMenu(subject, startTopic);
	}

	public void updateAreaDescription() {
		if(this.getArea().getDescription() != null) {
			Game.EVENT_BUS.post(new RenderTextEvent(this.getArea().getDescription()));
			Game.EVENT_BUS.post(new RenderTextEvent(""));
		}
	}

	public void describeSurroundings() {
		for(Actor actor : getVisibleActors()) {
			boolean adjacent = getArea() == actor.getArea() || getArea().getDistanceTo(actor.getArea().getID()) == 0;
			Context context;
			if(adjacent) {
				context = new Context(Map.of("inLocation", actor.getArea().getRelativeName()), actor, this);
			} else {
				context = new Context(Map.of("inLocation", actor.getArea().getRelativeName(), "direction", getArea().getRelativeDirectionOf(actor.getArea()).toString().toLowerCase()), actor, this);
			}
			String line;
			if(actor.isDead()) {
				line = "$subject lie$s dead " + (adjacent ? "next to $object1" : "$inLocation, to the $direction");
			} else if(actor.isUnconscious()) {
				line = "$subject lie$s unconscious " + (adjacent ? "next to $object1" : "$inLocation, to the $direction");
			} else {
				line = "$subject $is " + (adjacent ? "next to $object1" : "$inLocation, to the $direction");
			}
			String description = TextGen.generate(line, context);
			Game.EVENT_BUS.post(new RenderTextEvent(description));
		}
		Game.EVENT_BUS.post(new RenderTextEvent(""));
		TextGen.clearContext();
	}

}
