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
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.Room;
import com.github.finley243.adventureengine.world.template.StatsActor;

public class ActorPlayer extends Actor {

	private final MenuManager menuManager;
	
	public ActorPlayer(String ID, Area area, StatsActor stats) {
		super(ID, area, stats, null, null, false, false);
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
	public void move(Room room, int x, int y) {
		super.move(room, x, y);
		Game.EVENT_BUS.post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
		this.updateAreaDescription();
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

}
