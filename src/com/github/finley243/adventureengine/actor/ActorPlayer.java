package com.github.finley243.adventureengine.actor;

import java.util.List;

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
			Context context = new Context(actor, false, this, false);
			String line;
			if(actor.isDead()) {
				line = "<subject> lie<s> dead " + (getArea() == actor.getArea() ? "next to <object>" : actor.getArea().getRelativeName() + ", to the " + getArea().getRelativeDirectionOf(actor.getArea()).toString().toLowerCase());
			} else if(actor.isUnconscious()) {
				line = "<subject> lie<s> unconscious " + (getArea() == actor.getArea() ? "next to <object>" : actor.getArea().getRelativeName() + ", to the " + getArea().getRelativeDirectionOf(actor.getArea()).toString().toLowerCase());
			} else {
				line = "<subject> <is> " + (getArea() == actor.getArea() ? "next to <object>" : actor.getArea().getRelativeName() + ", to the " + getArea().getRelativeDirectionOf(actor.getArea()).toString().toLowerCase());
			}
			String description = TextGen.generate(line, context);
			Game.EVENT_BUS.post(new RenderTextEvent(description));
		}
		Game.EVENT_BUS.post(new RenderTextEvent(""));
		TextGen.clearContext();
	}

}
