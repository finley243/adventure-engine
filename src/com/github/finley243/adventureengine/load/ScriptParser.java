package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, BRACKET_OPEN, BRACKET_CLOSE, FUNCTION_CALL, PARAMETER_BLOCK
    }

    private static final Pattern pattern = Pattern.compile(";|/\\*[.*]+\\*/|//|[a-zA-Z0-9_]+\\(|[a-zA-Z0-9_]+|\\)|=|,|\\.|\\+|-|/|\\*|\"[a-zA-Z0-9_ \t]+\"");

    public static List<ScriptData> parseScripts(String scriptText) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText);
        return scripts;
    }

    private static List<ScriptToken> parseToTokens(String scriptText) {
        List<ScriptToken> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(scriptText);
        while (matcher.find()) {
            String currentToken = matcher.group();
            System.out.println("Token: " + currentToken);
        }
        return tokens;
    }

    public record ScriptData(String name, Expression.DataType returnType, Script script) {}

    private static class ScriptToken {
        public final ScriptTokenType type;

        public ScriptToken(ScriptTokenType type) {
            this.type = type;
        }
    }

}
