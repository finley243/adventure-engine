package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.SoundEvent;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActorPlayer extends Actor {

	private final MenuManager menuManager;
	
	public ActorPlayer(Game gameInstance, String ID, Area area, StatsActor stats) {
		super(gameInstance, ID, area, stats, null, null, false, false, false);
		this.menuManager = new MenuManager();
		game().eventBus().register(menuManager);
	}

	@Override
	public boolean forcePronoun() {
		return true;
	}
	
	@Override
	public void onVisualEvent(AudioVisualEvent event) {
		game().eventBus().post(new RenderTextEvent(event.getTextVisible()));
	}
	
	@Override
	public void onSoundEvent(SoundEvent event) {
		
	}
	
	@Override
	public void move(Area area) {
		boolean shouldShowDescription = getArea() != null;
		boolean newRoom = !getArea().getRoom().equals(area.getRoom());
		super.move(area);
		game().eventBus().post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
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
		game().eventBus().post(new PlayerDeathEvent());
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
			game().eventBus().post(new RenderTextEvent(this.getArea().getDescription()));
			game().eventBus().post(new RenderTextEvent(""));
		}
	}

	public void describeSurroundings() {
		Context playerContext = new Context(Map.of("inLocation", getArea().getRelativeName()), this);
		game().eventBus().post(new RenderTextEvent(TextGen.generate(Phrases.get("location"), playerContext)));
		for(Area area : getArea().getVisibleAreas(this)) {
			List<Noun> nounsInArea = new ArrayList<>();
			Set<Actor> actorsInArea = new HashSet<>(area.getActors());
			actorsInArea.remove(this);
			nounsInArea.addAll(actorsInArea);
			nounsInArea.addAll(area.getObjects());
			if(!nounsInArea.isEmpty()) {
				MultiNoun multiNoun = new MultiNoun(nounsInArea);
				boolean adjacent = getArea() == area || getArea().getDistanceTo(area.getID()) == 0;
				if (adjacent) {
					Context areaContext = new Context(Map.of("inLocation", area.getRelativeName()), multiNoun, this);
					game().eventBus().post(new RenderTextEvent(TextGen.generate(Phrases.get("locationListNear"), areaContext)));
				} else {
					Context areaContext = new Context(Map.of("inLocation", area.getRelativeName(), "direction", getArea().getRelativeDirectionOf(area).toString().toLowerCase()), multiNoun);
					game().eventBus().post(new RenderTextEvent(TextGen.generate(Phrases.get("locationList"), areaContext)));
				}
			}
		}
		game().eventBus().post(new RenderTextEvent(""));
		TextGen.clearContext();
	}

}
