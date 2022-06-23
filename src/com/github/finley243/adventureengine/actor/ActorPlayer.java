package com.github.finley243.adventureengine.actor;

import java.util.*;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.event.PlayerDeathEvent;
import com.github.finley243.adventureengine.event.ui.RenderAreaEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
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

}
