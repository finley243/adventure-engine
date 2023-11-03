package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {

    private enum ScriptTokenType {
        END_LINE, COMMENT, STRING, FLOAT, INTEGER, NAME, EQUALS, COMMA, DOT, PLUS, MINUS, DIVIDE, MULTIPLY, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, BRACKET_OPEN, BRACKET_CLOSE
    }

    private static final String REGEX_PATTERN = "/\\*[.*]+\\*/|//.*\n|\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*'|_?[a-zA-Z][a-zA-Z0-9_]*|([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f|[0-9]+|;|=|,|\\.|\\+|-|/|\\*|\\(|\\)|\\{|\\}";

    public static List<ScriptData> parseScripts(String scriptText) {
        List<ScriptData> scripts = new ArrayList<>();
        List<ScriptToken> tokens = parseToTokens(scriptText);
        for (ScriptToken currentToken : tokens) {

        }
        return scripts;
    }

    private static List<ScriptToken> parseToTokens(String scriptText) {
        List<ScriptToken> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        Matcher matcher = pattern.matcher(scriptText);
        while (matcher.find()) {
            String currentToken = matcher.group();
            if (currentToken.matches("/\\*[.*]+\\*/|//.*\n")) {
                tokens.add(new ScriptToken(ScriptTokenType.COMMENT));
            } else if (currentToken.startsWith("\"") && currentToken.endsWith("\"") && currentToken.length() > 1) {
                String value = currentToken.substring(1, currentToken.length() - 1);
                value = value.replaceAll("\\\\\"", "\"");
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value));
            } else if (currentToken.startsWith("'") && currentToken.endsWith("'") && currentToken.length() > 1) {
                String value = currentToken.substring(1, currentToken.length() - 1);
                value = value.replaceAll("\\\\'", "'");
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value));
            } else if (currentToken.matches("([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f")) {
                String value = currentToken.substring(0, currentToken.length() - 1);
                tokens.add(new ScriptToken(ScriptTokenType.FLOAT, value));
            } else if (currentToken.matches("[0-9]+")) {
                tokens.add(new ScriptToken(ScriptTokenType.INTEGER, currentToken));
            } else if (currentToken.equals(";")) {
                tokens.add(new ScriptToken(ScriptTokenType.END_LINE));
            } else if (currentToken.equals("=")) {
                tokens.add(new ScriptToken(ScriptTokenType.EQUALS));
            } else if (currentToken.equals(",")) {
                tokens.add(new ScriptToken(ScriptTokenType.COMMA));
            } else if (currentToken.equals(".")) {
                tokens.add(new ScriptToken(ScriptTokenType.DOT));
            } else if (currentToken.equals("+")) {
                tokens.add(new ScriptToken(ScriptTokenType.PLUS));
            } else if (currentToken.equals("-")) {
                tokens.add(new ScriptToken(ScriptTokenType.MINUS));
            } else if (currentToken.equals("/")) {
                tokens.add(new ScriptToken(ScriptTokenType.DIVIDE));
            } else if (currentToken.equals("*")) {
                tokens.add(new ScriptToken(ScriptTokenType.MULTIPLY));
            } else if (currentToken.equals("(")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_OPEN));
            } else if (currentToken.equals(")")) {
                tokens.add(new ScriptToken(ScriptTokenType.PARENTHESIS_CLOSE));
            } else if (currentToken.equals("{")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_OPEN));
            } else if (currentToken.equals("}")) {
                tokens.add(new ScriptToken(ScriptTokenType.BRACKET_CLOSE));
            } else if (currentToken.matches("_?[a-zA-Z][a-zA-Z0-9_]*")) {
                tokens.add(new ScriptToken(ScriptTokenType.NAME, currentToken));
            }
        }
        return tokens;
    }

    public record ScriptData(String name, Expression.DataType returnType, Script script) {}

    private static class ScriptToken {
        public final ScriptTokenType type;
        public final String value;

        public ScriptToken(ScriptTokenType type) {
            this.type = type;
            this.value = null;
        }

        public ScriptToken(ScriptTokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

}
