package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.*;
import com.google.common.base.Functions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptLoader {

    private static final String SCRIPT_FILE_EXTENSION = "ascr";
    private static final int NATIVE_FUNCTION_LINE = -1;
    private static final String NATIVE_FUNCTION_FILENAME = "NATIVE";

    private final ScriptParser scriptParser;

    public ScriptLoader(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public Map<String, ScriptParser.ScriptData> loadFromDir(File dir, Set<String> reservedFunctionNames) {
        if (!dir.exists()) throw new IllegalArgumentException("Directory does not exist: " + dir.getAbsolutePath());
        if (!dir.isDirectory()) throw new IllegalArgumentException("Script path must be a directory: " + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        Objects.requireNonNull(files);
        Map<String, ScriptParser.ScriptData> scriptDataMap = new HashMap<>();
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(SCRIPT_FILE_EXTENSION)) {
                String fileContents;
                try {
                    fileContents = Files.readString(file.toPath());
                } catch (IOException e) {
                    throw new GameDataException("Failed to read script file: " + file.getAbsolutePath());
                }
                List<ScriptParser.ScriptData> functions;
                try {
                    functions = scriptParser.parseFunctions(fileContents, file.getName());
                } catch (ScriptCompileException e) {
                    throw new GameDataException("Script parsing failure:\n" + e.getFileName() + ":" + e.getLineNumber() + " - " + e.getMessage());
                }
                for (ScriptParser.ScriptData function : functions) {
                    // TODO - Possibly move this check to ScriptParser
                    if (reservedFunctionNames.contains(function.name())) {
                        throw new GameDataException("Script function cannot use reserved function name: " + function.name());
                    }
                    scriptDataMap.put(function.name(), function);
                }
            }
        }
        return scriptDataMap;
    }

    public Map<String, ScriptParser.ScriptData> generateNativeFunctions() {
        List<ScriptParser.ScriptData> functions = new ArrayList<>();
        functions.add(Script.builder("setArea", ScriptSetArea::new).parameter("area").parameter("object").build());
        functions.add(Script.builder("attributeMenu", ScriptAttributeMenu::new).parameter("actor").parameter("points").build());
        functions.add(Script.builder("skillMenu", ScriptSkillMenu::new).parameter("actor").parameter("points").build());
        functions.add(Script.builder("startTimer", ScriptTimerStart::new).parameter("timer").parameter("duration").optionalParameter("scriptExpire").optionalParameter("scriptUpdate").build());
        functions.add(Script.builder("transferItem", ScriptTransferItem::new).optionalParameter("transferType", Expression.string("count")).optionalParameter("from").optionalParameter("to").optionalParameter("item").optionalParameter("count", Expression.integer(1)).build());
        functions.add(Script.builder("sendSensoryEvent", ScriptSensoryEvent::new).parameter("area").optionalParameter("phrase").optionalParameter("phraseAudible").optionalParameter("detectSelf", Expression.bool(true)).allowExtraParameters().build());
        functions.add(Script.builder("sendBark", ScriptBark::new).parameter("actor").parameter("bark").allowExtraParameters().build());
        functions.add(Script.builder("addEffect", ScriptEffectAdd::new).parameter("target").parameter("effect").build());
        functions.add(Script.builder("removeEffect", ScriptEffectRemove::new).parameter("target").parameter("effect").build());
        functions.add(Script.builder("startScene", ScriptScene::new).parameter("actor").parameter("scene").build());
        functions.add(Script.builder("setFactionRelation", ScriptFactionRelation::new).parameter("faction").parameter("relatedFaction").parameter("relation").build());
        functions.add(Script.builder("startCombat", ScriptCombat::new).parameter("actor").parameter("target").build());
        functions.add(Script.builder("isCombatant", ScriptIsCombatant::new).returnType(Expression.DataType.BOOLEAN).parameter("actor").parameter("target").build());
        functions.add(Script.builder("isVisible", ScriptIsVisible::new).returnType(Expression.DataType.BOOLEAN).parameter("actor").parameter("target").build());
        functions.add(Script.builder("targetType", ScriptTargetType::new).returnType(Expression.DataType.STRING).parameter("actor").parameter("target").build());
        functions.add(Script.builder("randomBoolean", ScriptRandomBoolean::new).returnType(Expression.DataType.BOOLEAN).parameter("chance").build());
        functions.add(Script.builder("randomInteger", ScriptRandomInteger::new).returnType(Expression.DataType.INTEGER).parameter("min").parameter("max").build());
        functions.add(Script.builder("inventoryContains", ScriptInventoryContains::new).returnType(Expression.DataType.BOOLEAN).parameter("inventory").parameter("item").optionalParameter("requireAll", Expression.bool(false)).build());
        functions.add(Script.builder("round", ScriptRound::new).returnType(Expression.DataType.INTEGER).parameter("value").build());
        functions.add(Script.builder("scaleLinear", ScriptScaleLinear::new).returnType(Expression.DataType.FLOAT).parameter("input").parameter("inputMin").parameter("inputMax").parameter("outputMin").parameter("outputMax").build());
        functions.add(Script.builder("scaleLog", ScriptScaleLog::new).returnType(Expression.DataType.FLOAT).parameter("input").parameter("inputMin").parameter("inputMax").parameter("outputMin").parameter("outputMax").build());
        functions.add(Script.builder("setUnion", ScriptSetUnion::new).returnType(Expression.DataType.SET).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setIntersect", ScriptSetIntersect::new).returnType(Expression.DataType.SET).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setDifference", ScriptSetDifference::new).returnType(Expression.DataType.SET).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setSymmetricDifference", ScriptSetSymmetricDifference::new).returnType(Expression.DataType.SET).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("contains", ScriptCollectionContains::new).returnType(Expression.DataType.BOOLEAN).parameter("collection").parameter("value").build());
        functions.add(Script.builder("size", ScriptCollectionSize::new).returnType(Expression.DataType.INTEGER).parameter("collection").build());
        functions.add(Script.builder("add", ScriptCollectionAdd::new).parameter("collection").parameter("value").build());
        functions.add(Script.builder("listAddIndex", ScriptListAddIndex::new).parameter("list").parameter("index").parameter("value").build());
        functions.add(Script.builder("remove", ScriptCollectionRemove::new).parameter("collection").parameter("value").build());
        functions.add(Script.builder("listRemoveIndex", ScriptListAddIndex::new).parameter("list").parameter("index").build());
        functions.add(Script.builder("listIndexOf", ScriptListIndexOf::new).returnType(Expression.DataType.INTEGER).parameter("list").parameter("value").build());
        functions.add(Script.builder("listSet", ScriptListIndexSet::new).parameter("list").parameter("index").parameter("value").build());
        functions.add(Script.builder("listGet", ScriptListIndexGet::new).allowAnyReturn().parameter("list").parameter("index").build());
        functions.add(Script.builder("clear", ScriptCollectionClear::new).parameter("collection").build());
        functions.add(Script.builder("copy", ScriptCopy::new).allowAnyReturn().parameter("collection").build());
        functions.add(Script.builder("subList", ScriptSubList::new).returnType(Expression.DataType.LIST).parameter("list").parameter("start").parameter("end").build());
        functions.add(Script.builder("listConcat", ScriptListConcat::new).returnType(Expression.DataType.LIST).parameter("listOne").parameter("listTwo").build());
        functions.add(Script.builder("selectRandom", ScriptRandomValueFromCollection::new).allowAnyReturn().parameter("collection").build());
        functions.add(Script.builder("statHolderType", ScriptStatHolderType::new).returnType(Expression.DataType.STRING).parameter("holder").build());
        functions.add(Script.builder("isTimerActive", ScriptTimerActive::new).returnType(Expression.DataType.BOOLEAN).parameter("timer").build());
        functions.add(Script.builder("toString", ScriptToString::new).returnType(Expression.DataType.STRING).parameter("value").build());
        functions.add(Script.builder("subString", ScriptSubString::new).returnType(Expression.DataType.STRING).parameter("string").parameter("start").parameter("end").build());
        functions.add(Script.builder("toUpperCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.UPPER)).returnType(Expression.DataType.STRING).parameter("string").build());
        functions.add(Script.builder("toLowerCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.LOWER)).returnType(Expression.DataType.STRING).parameter("string").build());
        functions.add(Script.builder("toTitleCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.TITLE)).returnType(Expression.DataType.STRING).parameter("string").build());
        functions.add(Script.builder("dataType", ScriptDataType::new).returnType(Expression.DataType.STRING).parameter("value").build());
        functions.add(Script.builder("sleep", ScriptSleep::new).parameter("actor").parameter("duration").build());
        return functions.stream().collect(Collectors.toMap(ScriptParser.ScriptData::name, Functions.identity()));
    }

}
