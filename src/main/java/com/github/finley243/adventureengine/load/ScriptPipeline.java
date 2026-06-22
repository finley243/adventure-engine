package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.script.parse.ScriptLexer;

public record ScriptPipeline(ScriptLexer lexer, ScriptParser parser) {
}
