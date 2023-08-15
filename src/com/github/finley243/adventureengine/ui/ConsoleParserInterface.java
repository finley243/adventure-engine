package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.ConsoleUtils;
import com.github.finley243.adventureengine.menu.MenuChoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleParserInterface implements UserInterface {

    private final Game game;

    public ConsoleParserInterface(Game game) {
        this.game = game;
    }

    @Override
    public void onTextEvent(RenderTextEvent event) {
        System.out.println(event.getText());
    }

    @Override
    public void onMenuEvent(RenderChoiceMenuEvent event) {
        List<MenuChoice> validChoices = new ArrayList<>();
        for (MenuChoice choice : event.getMenuChoices()) {
            if (choice.isEnabled()) {
                validChoices.add(choice);
            }
        }
        if (event.shouldForcePrompts()) {
            for(int i = 0; i < validChoices.size(); i++) {
                System.out.println((i + 1) + ") " + validChoices.get(i).getFullPrompt());
            }
            int response = ConsoleUtils.intInRange(1, validChoices.size());
            System.out.println();
            game.eventBus().post(new ChoiceMenuInputEvent(validChoices.get(response - 1).getIndex()));
        } else {
            for (MenuChoice menuChoice : validChoices) {
                for (String parserPrompt : menuChoice.getParserPrompts()) {
                    System.out.println("PROMPT: " + parserPrompt);
                }
            }
            while (true) {
                String response = ConsoleUtils.stringInput();
                response = response.trim();
                response = response.toLowerCase();
                List<String> words = new ArrayList<>(Arrays.asList(response.split("\\s+")));
                words.removeAll(List.of("the", "a"));
                StringBuilder builder = new StringBuilder();
                boolean precedingSpace = false;
                for (String word : words) {
                    if (precedingSpace) {
                        builder.append(" ");
                    } else {
                        precedingSpace = true;
                    }
                    builder.append(word);
                }
                String processedResponse = builder.toString();
                for (MenuChoice menuChoice : validChoices) {
                    for (String parserPrompt : menuChoice.getParserPrompts()) {
                        if (parserPrompt.equalsIgnoreCase(processedResponse)) {
                            System.out.println();
                            game.eventBus().post(new ChoiceMenuInputEvent(menuChoice.getIndex()));
                            return;
                        }
                    }
                }
                System.out.println("Command not recognized. Try again.");
            }
        }
    }

    @Override
    public void onNumericMenuEvent(RenderNumericMenuEvent event) {

    }

}
