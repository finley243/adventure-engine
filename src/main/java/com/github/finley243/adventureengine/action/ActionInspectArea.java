package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataArea;
import com.github.finley243.adventureengine.textgen.MultiNoun;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.PluralNoun;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.path.PathDataArea;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        this.area = area;
    }

    @Override
    public String getID() {
        return "inspect_area";
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        return Context.builder(game).subject(subject).parentArea(area).parentAction(this).build();
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        Map<Area, Pathfinder.VisibleAreaData> visibleAreas = Pathfinder.getVisibleAreas(game, area, subject);
        List<Area> orderedAreaList = new ArrayList<>(visibleAreas.keySet());
        orderedAreaList.sort(Comparator.comparingInt(a -> visibleAreas.get(a).pathData().size()));
        TextGen.clearContext();
        for (Area currentArea : orderedAreaList) {
            Pathfinder.VisibleAreaData areaData = visibleAreas.get(currentArea);
            String directionName = areaData.direction() != null ? areaData.direction().name : null;
            Context context = Context.builder(game).subject(subject).target(subject).build();
            Area leadingArea = null;
            if (areaData.pathData().size() > 3) {
                leadingArea = ((PathDataArea) areaData.pathData().get(areaData.pathData().size() - 3)).getArea();
            }
            context.setLocalVariable("relativeName", Expression.constant(currentArea.getRelativeName()));
            context.setLocalVariable("area", Expression.constantNoun(currentArea));
            context.setLocalVariable("dir", Expression.constant(directionName));
            context.setLocalVariable("leadingArea", Expression.constantNoun(leadingArea));
            String phrase;
            if (currentArea.equals(area)) {
                phrase = "$actor $is $relativeName $area.";
            } else if (leadingArea != null) {
                phrase = "Beyond $leadingArea, there $is $area.";
            } else if (directionName != null) {
                phrase = "To the $dir, there $is $area.";
            } else {
                phrase = "There $is $area.";
            }
            game.eventBus().post(new RenderTextEvent(TextGen.generate(phrase, context, context.generateTextContext())));

            List<Noun> visibleObjects = currentArea.getObjects().stream()
                    .map(object -> (Noun) object)
                    .toList();
            List<Noun> visibleActors = currentArea.getActors().stream()
                    .filter(actor -> !actor.equals(subject)) // Exclude the subject actor
                    .map(actor -> (Noun) actor)
                    .toList();
            List<Noun> visibleItems = currentArea.getInventory().getItemMap().entrySet().stream()
                    .map(entry -> entry.getValue() == 1
                            ? (Noun) entry.getKey()
                            : new PluralNoun((Noun) entry.getKey(), entry.getValue()))
                    .toList();
            List<Noun> areaContents = new ArrayList<>();
            areaContents.addAll(visibleObjects);
            areaContents.addAll(visibleActors);
            areaContents.addAll(visibleItems);
            if (!areaContents.isEmpty()) {
                context.setLocalVariable("areaContents", Expression.constantNoun(new MultiNoun(areaContents)));
                String areaContentsPhrase = "$relativeName $area, there $is $areaContents.";
                game.eventBus().post(new RenderTextEvent(TextGen.generate(areaContentsPhrase, context, context.generateTextContext())));
            }
        }
        if (area.getRoom() != null && area.getRoom().getDescription() != null) {
            game.menuManager().sceneMenu(game, area.getRoom().getDescription(), Context.builder(game).subject(subject).target(subject).build(), false);
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            game.menuManager().sceneMenu(game, area.getDescription(), Context.builder(game).subject(subject).target(subject).build(), false);
            area.setKnown();
        }
        if (area.getRoom() != null) {
            area.getRoom().triggerScript("on_inspect", game, subject, subject);
        }
        //TextGen.clearContext();
        area.triggerScript("on_inspect", game, subject, subject);
    }

    @Override
    public CanChooseResult canChoose(Game game, Actor subject) {
        CanChooseResult resultSuper = super.canChoose(game, subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        /*if (area.getDescription() == null && (area.getRoom() == null || area.getRoom().getDescription() == null)) {
            return new CanChooseResult(false, "Nothing to see");
        }*/
        return new CanChooseResult(true, null);
    }

    @Override
    public int actionPoints(Game game, Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataArea(area, true);
    }

    @Override
    public String getPrompt(Game game, Actor subject) {
        return "Look Around";
    }

}
