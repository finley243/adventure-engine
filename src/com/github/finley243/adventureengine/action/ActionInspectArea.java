package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataArea;
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
    public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, null, area);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (area.getRoom() != null && area.getRoom().getDescription() != null) {
            subject.game().menuManager().sceneMenu(subject.game(), area.getRoom().getDescription(), new Context(subject.game(), subject, subject), false);
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            subject.game().menuManager().sceneMenu(subject.game(), area.getDescription(), new Context(subject.game(), subject, subject), false);
            area.setKnown();
        }
        if (area.getRoom() != null) {
            area.getRoom().triggerScript("on_inspect", subject, subject);
        }
        Map<Area, Pathfinder.VisibleAreaData> visibleAreas = Pathfinder.getVisibleAreas(area, subject);
        List<Area> orderedAreaList = new ArrayList<>(visibleAreas.keySet());
        orderedAreaList.sort(Comparator.comparingInt(a -> visibleAreas.get(a).pathData().size()));
        TextGen.clearContext();
        for (Area currentArea : orderedAreaList) {
            Pathfinder.VisibleAreaData areaData = visibleAreas.get(currentArea);
            String directionName = areaData.direction() != null ? areaData.direction().name : null;
            Context context = new Context(subject.game(), subject, subject);
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
            subject.game().eventBus().post(new RenderTextEvent(TextGen.generate(phrase, context, context.generateTextContext())));
        }
        TextGen.clearContext();
        area.triggerScript("on_inspect", subject, subject);
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        /*if (area.getDescription() == null && (area.getRoom() == null || area.getRoom().getDescription() == null)) {
            return new CanChooseResult(false, "Nothing to see");
        }*/
        return new CanChooseResult(true, null);
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataArea(area, true);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Look Around";
    }

}
