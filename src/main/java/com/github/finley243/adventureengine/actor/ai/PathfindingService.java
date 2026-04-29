package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PathfindingService {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors()),
            r -> {
                Thread t = new Thread(r, "pathfinding-worker");
                t.setDaemon(true);
                return t;
            });

    public static void precomputeVisibleAreas(Collection<Actor> actors) {
        List<Future<?>> futures = new ArrayList<>(actors.size());
        for (Actor actor : actors) {
            futures.add(EXECUTOR.submit(actor::refreshVisibleAreas));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ignored) {
            }
        }
    }

}
