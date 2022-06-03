package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActorPlayer extends Actor {

	private final MenuManager menuManager;
	
	public ActorPlayer(Game gameInstance, String ID, Area area, ActorTemplate stats) {
		super(gameInstance, ID, area, stats, null, null, false, false);
		this.menuManager = new MenuManager();
		game().eventBus().register(menuManager);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public boolean forcePronoun() {
		return true;
	}
	
	@Override
	public void onSensoryEvent(SensoryEvent event, boolean visible) {
		if(isActive()) {
			if (visible) {
				game().eventBus().post(new RenderTextEvent(event.getTextVisible()));
			} else if(event.getTextAudible() != null) {
				game().eventBus().post(new RenderTextEvent(event.getTextAudible()));
			}
		}
	}
	
	@Override
	public void setArea(Area area) {
		boolean shouldShowDescription = getArea() != null;
		boolean newRoom = !getArea().getRoom().equals(area.getRoom());
		super.setArea(area);
		game().eventBus().post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
		if(shouldShowDescription) {
			this.updateAreaDescription();
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
			//game().eventBus().post(new RenderTextEvent(this.getArea().getDescription()));
			//game().eventBus().post(new RenderTextEvent(""));
			SceneManager.trigger(game(), List.of(this.getArea().getDescription()));
		}
	}

	public void describeSurroundings() {
		if(!isActive() || !isEnabled()) return;
		Context areaContext = new Context(new NounMapper().put("actor", this).put("landmark", getArea().getLandmark()).build());
		game().eventBus().post(new RenderTextEvent(TextGen.generate("$_actor $is_actor adjacent to $landmark" + (getArea().getActors(this).isEmpty() ? "" : " [" + getArea().getActorList(this) + "]"), areaContext)));
		Set<Area> nearbyAreas = getArea().getMovableAreas();
		if(!nearbyAreas.isEmpty()) {
			List<Area> nearbyAreasList = new ArrayList<>(nearbyAreas);
			StringBuilder phrase = new StringBuilder("Nearby, there is ");
			for(int i = 0; i < nearbyAreasList.size(); i++) {
				Area currentArea = nearbyAreasList.get(i);
				if(i > 0 && i < nearbyAreasList.size() - 1) {
					phrase.append(", ");
				} else if(i == nearbyAreasList.size() - 1) {
					phrase.append(", and ");
				}
				phrase.append(currentArea.getFormattedName());
				if(!currentArea.getActors(this).isEmpty()) {
					phrase.append(" [").append(currentArea.getActorList(this)).append("]");
				}
				currentArea.setKnown();
			}
			phrase.append(".");
			game().eventBus().post(new RenderTextEvent(phrase.toString()));
		}
		// TODO - Add distant area descriptions based on closest nearby area (BFS)
		game().eventBus().post(new RenderTextEvent(""));
		TextGen.clearContext();
	}

}
