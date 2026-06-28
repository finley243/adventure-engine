package com.github.finley243.adventureengine.stat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class StatUtils {

    public static <T> Set<String> objectSetToIDSet(Set<T> objectSet, Function<T, String> idFunction) {
        Set<String> idSet = new HashSet<>();
        for (T obj : objectSet) {
            idSet.add(idFunction.apply(obj));
        }
        return idSet;
    }

    public static <T> List<String> objectListToIDList(List<T> objectList, Function<T, String> idFunction) {
        List<String> idList = new ArrayList<>();
        for (T obj : objectList) {
            idList.add(idFunction.apply(obj));
        }
        return idList;
    }

}
