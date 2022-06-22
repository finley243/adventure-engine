package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.component.ActionComponent;
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

	public ActorPlayer(Game gameInstance, String ID, Area area, ActorTemplate stats) {
		super(gameInstance, ID, area, stats, null, null, false, false, true);
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
		boolean newRoom = getArea() == null || !getArea().getRoom().equals(area.getRoom());
		super.setArea(area);
		game().eventBus().post(new RenderAreaEvent(LangUtils.titleCase(getArea().getRoom().getName()), LangUtils.titleCase(getArea().getName())));
		if(newRoom) {
			getArea().getRoom().triggerScript("on_player_enter", this);
		}
		getArea().triggerScript("on_player_enter", this);
	}

	@Override
	public void onMove(Area lastArea) {
		boolean isRoomChange = !lastArea.getRoom().equals(getArea().getRoom());
		boolean isAreaChange = isRoomChange || !lastArea.equals(getArea());
		if(isRoomChange && getArea().getRoom().getDescription() != null) {
			SceneManager.trigger(game(), this, getArea().getRoom().getDescription());
			getArea().getRoom().setKnown();
			for (Area area : getArea().getRoom().getAreas()) {
				area.setKnown();
			}
		}
		if(isAreaChange && getArea().getDescription() != null) {
			SceneManager.trigger(game(), this, getArea().getDescription());
			getArea().setKnown();
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		game().eventBus().post(new PlayerDeathEvent());
	}
	
	public void startDialogue(Actor subject, String startTopic) {
		menuManager.dialogueMenu(subject, startTopic);
	}

	public void describeSurroundings() {
		if(!isActive() || !isEnabled()) return;
		Context areaContext = new Context(new NounMapper().put("actor", this).put("landmark", getArea().getLandmark()).build());
		game().eventBus().post(new RenderTextEvent(TextGen.generate("$_actor $is_actor next to $landmark" + (getArea().getActors(this).isEmpty() ? "" : " [" + getArea().getActorList(this) + "]"), areaContext)));
		// TODO - Use only movable areas that are visible (no seeing around corners)
		Set<Area> nearbyAreas = getArea().getMovableAreas();
		if(!nearbyAreas.isEmpty()) {
			List<Area> nearbyAreasList = new ArrayList<>(nearbyAreas);
			StringBuilder phrase = new StringBuilder("Nearby, there is ");
			for(int i = 0; i < nearbyAreasList.size(); i++) {
				Area currentArea = nearbyAreasList.get(i);
				if(i > 0 && i < nearbyAreasList.size() - 1) {
					phrase.append(", ");
				} else if(i != 0 && i == nearbyAreasList.size() - 1) {
					phrase.append(", and ");
				}
				if (!currentArea.getRoom().equals(getArea().getRoom())) {
					phrase.append(currentArea.getRoom().getFormattedName());
					currentArea.getRoom().setKnown();
				} else {
					phrase.append(currentArea.getFormattedName());
					currentArea.setKnown();
				}
				if(!currentArea.getActors(this).isEmpty()) {
					phrase.append(" [").append(currentArea.getActorList(this)).append("]");
				}
			}
			phrase.append(".");
			game().eventBus().post(new RenderTextEvent(phrase.toString()));
		}
		// TODO - Add distant area descriptions based on closest nearby area (BFS)
		game().eventBus().post(new RenderTextEvent(""));
		TextGen.clearContext();
	}

}
