package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.script.parse.ASTParseResult;
import com.github.finley243.adventureengine.script.parse.CompileError;
import com.github.finley243.adventureengine.script.parse.ScriptFunction;
import com.github.finley243.adventureengine.script.parse.ScriptToken;
import com.github.finley243.adventureengine.script.parse.nodes.ASTFile;
import com.google.common.base.Functions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptLoader {

    private static final String SCRIPT_FILE_EXTENSION = "ascr";

    private final ScriptPipeline scriptPipeline;

    public ScriptLoader(ScriptPipeline scriptPipeline) {
        this.scriptPipeline = scriptPipeline;
    }

    public Map<String, ScriptFunction> loadFromDir(File dir, Set<String> reservedFunctionNames) {
        if (!dir.exists()) throw new IllegalArgumentException("Directory does not exist: " + dir.getAbsolutePath());
        if (!dir.isDirectory()) throw new IllegalArgumentException("Script path must be a directory: " + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        Objects.requireNonNull(files);
        List<ASTFile> fileASTs = new ArrayList<>();
        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if (fileExtension.equalsIgnoreCase(SCRIPT_FILE_EXTENSION)) {
                String fileContents;
                try {
                    fileContents = Files.readString(file.toPath());
                } catch (IOException e) {
                    throw new GameDataException("Failed to read script file: " + file.getAbsolutePath());
                }
                try {
                    List<ScriptToken> tokenList = scriptPipeline.lexer().parseToTokens(fileContents, file.getName());
                    ASTParseResult parseResult = scriptPipeline.parser().parse(tokenList);
                    if (!parseResult.errors().isEmpty()) {
                        StringBuilder sb = new StringBuilder("Script parsing failed:\n");
                        for (CompileError error : parseResult.errors()) {
                            sb.append(" [").append(error.range().fileName()).append(":").append(error.range().line()).append("] ").append(error.message()).append("\n");
                        }
                        throw new GameDataException(sb.toString());
                    }
                    ASTFile fileAST = (ASTFile) parseResult.node();
                    fileASTs.add(fileAST);
                } catch (ScriptCompileException e) {
                    throw new GameDataException("Script parsing failure:\n" + e.getFileName() + ":" + e.getLineNumber() + " - " + e.getMessage());
                }
            }
        }
        scriptPipeline.validator().validateOrThrow(fileASTs, reservedFunctionNames);
        List<ScriptFunction> scriptFunctionList = scriptPipeline.converter().convert(fileASTs);
        return scriptFunctionList.stream().collect(Collectors.toMap(ScriptFunction::name, Functions.identity()));
    }

    public Map<String, ScriptFunction> generateNativeFunctions() {
        List<ScriptFunction> functions = new ArrayList<>();
        functions.add(Script.builder("setArea", ScriptSetArea::new).parameter("object").parameter("area").build());
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
        functions.add(Script.builder("isCombatant", ScriptIsCombatant::new).parameter("actor").parameter("target").build());
        functions.add(Script.builder("isVisible", ScriptIsVisible::new).parameter("actor").parameter("target").build());
        functions.add(Script.builder("targetType", ScriptTargetType::new).parameter("actor").parameter("target").build());
        functions.add(Script.builder("randomBoolean", ScriptRandomBoolean::new).parameter("chance").build());
        functions.add(Script.builder("randomInteger", ScriptRandomInteger::new).parameter("min").parameter("max").build());
        functions.add(Script.builder("inventoryContains", ScriptInventoryContains::new).parameter("inventory").parameter("item").optionalParameter("requireAll", Expression.bool(false)).build());
        functions.add(Script.builder("round", ScriptRound::new).parameter("value").build());
        functions.add(Script.builder("scaleLinear", ScriptScaleLinear::new).parameter("input").parameter("inputMin").parameter("inputMax").parameter("outputMin").parameter("outputMax").build());
        functions.add(Script.builder("scaleLog", ScriptScaleLog::new).parameter("input").parameter("inputMin").parameter("inputMax").parameter("outputMin").parameter("outputMax").build());
        functions.add(Script.builder("setUnion", ScriptSetUnion::new).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setIntersect", ScriptSetIntersect::new).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setDifference", ScriptSetDifference::new).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("setSymmetricDifference", ScriptSetSymmetricDifference::new).parameter("setOne").parameter("setTwo").build());
        functions.add(Script.builder("contains", ScriptCollectionContains::new).parameter("collection").parameter("value").build());
        functions.add(Script.builder("size", ScriptCollectionSize::new).parameter("collection").build());
        functions.add(Script.builder("add", ScriptCollectionAdd::new).parameter("collection").parameter("value").build());
        functions.add(Script.builder("listAddIndex", ScriptListAddIndex::new).parameter("list").parameter("index").parameter("value").build());
        functions.add(Script.builder("remove", ScriptCollectionRemove::new).parameter("collection").parameter("value").build());
        functions.add(Script.builder("listRemoveIndex", ScriptListRemoveIndex::new).parameter("list").parameter("index").build());
        functions.add(Script.builder("listIndexOf", ScriptListIndexOf::new).parameter("list").parameter("value").build());
        functions.add(Script.builder("listSet", ScriptListIndexSet::new).parameter("list").parameter("index").parameter("value").build());
        functions.add(Script.builder("listGet", ScriptListIndexGet::new).parameter("list").parameter("index").build());
        functions.add(Script.builder("clear", ScriptCollectionClear::new).parameter("collection").build());
        functions.add(Script.builder("copy", ScriptCopy::new).parameter("collection").build());
        functions.add(Script.builder("subList", ScriptSubList::new).parameter("list").parameter("start").parameter("end").build());
        functions.add(Script.builder("listConcat", ScriptListConcat::new).parameter("listOne").parameter("listTwo").build());
        functions.add(Script.builder("selectRandom", ScriptRandomValueFromCollection::new).parameter("collection").build());
        functions.add(Script.builder("statHolderType", ScriptStatHolderType::new).parameter("holder").build());
        functions.add(Script.builder("isTimerActive", ScriptTimerActive::new).parameter("timer").build());
        functions.add(Script.builder("toString", ScriptToString::new).parameter("value").build());
        functions.add(Script.builder("subString", ScriptSubString::new).parameter("string").parameter("start").parameter("end").build());
        functions.add(Script.builder("toUpperCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.UPPER)).parameter("string").build());
        functions.add(Script.builder("toLowerCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.LOWER)).parameter("string").build());
        functions.add(Script.builder("toTitleCase", t -> new ScriptStringCase(t, ScriptStringCase.CaseType.TITLE)).parameter("string").build());
        functions.add(Script.builder("dataType", ScriptDataType::new).parameter("value").build());
        functions.add(Script.builder("sleep", ScriptSleep::new).parameter("actor").parameter("duration").build());
        return functions.stream().collect(Collectors.toMap(ScriptFunction::name, Functions.identity()));
    }

}
