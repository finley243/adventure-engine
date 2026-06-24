package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.script.parse.ScriptASTParser;
import com.github.finley243.adventureengine.script.parse.ScriptConverter;
import com.github.finley243.adventureengine.script.parse.ScriptLexer;
import com.github.finley243.adventureengine.script.parse.ScriptValidator;

public record ScriptPipeline(ScriptLexer lexer, ScriptASTParser parser, ScriptValidator validator, ScriptConverter converter) {
}
