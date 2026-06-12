package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.event.UIEventBus;
import com.github.finley243.adventureengine.event.UIEventBusImpl;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.gamedata.*;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.load.ConfigLoader;
import com.github.finley243.adventureengine.load.GameDataLoader;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntimeImpl;
import com.github.finley243.adventureengine.ui.ConsoleInterface;
import com.github.finley243.adventureengine.ui.ConsoleParserInterface;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;
import com.github.finley243.adventureengine.ui.UserInterface;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class Main {

	private static final String GAMEFILES = "src/gamefiles";
	private static final String DATA_DIRECTORY = "/data";
	private static final String LOG_DIRECTORY = "/logs";
	private static final String CONFIG_FILE = "/config.xml";

	public static void main(String[] args) {
		ConfigLoader configLoader = new ConfigLoader();
		File configFile = Path.of(GAMEFILES + CONFIG_FILE).toFile();
        Map<ConfigOption, String> configData = configLoader.loadConfig(configFile);
		ConfigHandler configHandler = new ConfigHandler(configData);

		UIEventBus eventBus = new UIEventBusImpl();
		MenuManager menuManager = new MenuManager(eventBus);
		eventBus.register(menuManager);
		QuestManager questManager = new QuestManager();
		DateTimeController dateTimeController = new DateTimeController();
		TimerManager timerManager = new TimerManager();

		MutableRegistry<Expression> globalExpressionRegistry = new MutableRegistry<>(Map.of());
		ScriptRuntimeImpl scriptRuntime = new ScriptRuntimeImpl(menuManager, timerManager, dateTimeController, globalExpressionRegistry);

		if (configHandler.get(ConfigOption.ENABLE_DEBUG_LOG).equalsIgnoreCase("true")) {
			DebugLogger.init(GAMEFILES + LOG_DIRECTORY);
		}

		MutableRegistry<Item> itemMutableRegistry = new MutableRegistry<>(Map.of());

		GameDataLoader gameDataLoader = new GameDataLoader(configHandler, scriptRuntime, itemMutableRegistry);
		File dataDirectory = Path.of(GAMEFILES + DATA_DIRECTORY).toFile();
		GameData gameData = gameDataLoader.loadData(dataDirectory);
		scriptRuntime.setGameData(gameData);

		UserInterface userInterface = switch (configHandler.get(ConfigOption.INTERFACE_TYPE)) {
			case "graphicalChoice" -> new GraphicalInterfaceComplex(eventBus, configHandler.get(ConfigOption.GAME_NAME));
			case "consoleParser" -> new ConsoleParserInterface(eventBus);
			case "consoleChoice" -> new ConsoleInterface(eventBus);
			default -> throw new GameDataException("Config has invalid interface type");
		};
		eventBus.register(userInterface);

		Game game = new Game(eventBus, menuManager, questManager, dateTimeController, scriptRuntime, gameData.actorRegistry(), gameData.objectRegistry(), gameData.areaRegistry(), timerManager);
		game.start();
	}

}
