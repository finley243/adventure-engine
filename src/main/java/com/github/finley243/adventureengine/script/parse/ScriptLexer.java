package com.github.finley243.adventureengine.script.parse;

import com.github.finley243.adventureengine.load.ScriptCompileException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptLexer {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?<BLOCKCOMMENT>/\\*[\\s\\S]*?\\*/)" +
                    "|(?<LINECOMMENT>//[^\n]*)" +
                    "|(?<STRING>\"(\\\\\"|[^\"])*\"|'(\\\\'|[^'])*')" +
                    "|(?<FLOAT>([0-9]*\\.[0-9]+|[0-9]+\\.?[0-9]*)f)" +
                    "|(?<INTEGER>[0-9]+)" +
                    "|(?<NAME>_?[a-zA-Z][a-zA-Z0-9_]*)" +
                    "|(?<SYMBOL>\\+=|-=|\\*=|/=|%=|==|!=|<=|>=|&&|\\|\\||[;=,.+\\-/*%^:?!<>(){}\\[\\]])" +
                    "|(?<UNKNOWN>\\S+)"
    );
    private static final Map<String, ScriptTokenType> SYMBOL_MAP = new HashMap<>() {
        {
            put(";", ScriptTokenType.END_LINE);
            put("=", ScriptTokenType.ASSIGNMENT);
            put("+=", ScriptTokenType.MODIFIER_PLUS);
            put("-=", ScriptTokenType.MODIFIER_MINUS);
            put("*=", ScriptTokenType.MODIFIER_MULTIPLY);
            put("/=", ScriptTokenType.MODIFIER_DIVIDE);
            put("%=", ScriptTokenType.MODIFIER_MODULO);
            put(",", ScriptTokenType.COMMA);
            put(".", ScriptTokenType.DOT);
            put("+", ScriptTokenType.PLUS);
            put("-", ScriptTokenType.MINUS);
            put("/", ScriptTokenType.DIVIDE);
            put("*", ScriptTokenType.MULTIPLY);
            put("%", ScriptTokenType.MODULO);
            put("^", ScriptTokenType.POWER);
            put(":", ScriptTokenType.COLON);
            put("?", ScriptTokenType.TERNARY_IF);
            put("!", ScriptTokenType.NOT);
            put("&&", ScriptTokenType.AND);
            put("||", ScriptTokenType.OR);
            put("==", ScriptTokenType.EQUAL);
            put("!=", ScriptTokenType.NOT_EQUAL);
            put("<=", ScriptTokenType.LESS_EQUAL);
            put(">=", ScriptTokenType.GREATER_EQUAL);
            put("<", ScriptTokenType.LESS);
            put(">", ScriptTokenType.GREATER);
            put("(", ScriptTokenType.PARENTHESIS_OPEN);
            put(")", ScriptTokenType.PARENTHESIS_CLOSE);
            put("{", ScriptTokenType.BRACKET_OPEN);
            put("}", ScriptTokenType.BRACKET_CLOSE);
            put("[", ScriptTokenType.BRACKET_SQUARE_OPEN);
            put("]", ScriptTokenType.BRACKET_SQUARE_CLOSE);
        }
    };
    private static final Map<String, ScriptTokenType> KEYWORD_MAP = new HashMap<>() {
        {
            put("true", ScriptTokenType.BOOLEAN_TRUE);
            put("false", ScriptTokenType.BOOLEAN_FALSE);
            put("null", ScriptTokenType.NULL);
            put("return", ScriptTokenType.RETURN);
            put("break", ScriptTokenType.BREAK);
            put("continue", ScriptTokenType.CONTINUE);
            put("error", ScriptTokenType.ERROR);
            put("log", ScriptTokenType.LOG);
            put("var", ScriptTokenType.VARIABLE);
            put("func", ScriptTokenType.FUNCTION);
            put("for", ScriptTokenType.FOR);
            put("if", ScriptTokenType.IF);
            put("else", ScriptTokenType.ELSE);
            put("stat", ScriptTokenType.STAT);
            put("statHolder", ScriptTokenType.STAT_HOLDER);
            put("game", ScriptTokenType.GAME);
            put("global", ScriptTokenType.GLOBAL);
            put("set", ScriptTokenType.SET);
            put("list", ScriptTokenType.LIST);
            put("gameData", ScriptTokenType.GAME_DATA);
            put("context", ScriptTokenType.CONTEXT);
        }
    };

    public List<ScriptToken> parseToTokens(String scriptText, String fileName) {
        List<ScriptToken> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(scriptText);
        int lastEnd = 0;
        int currentLine = 1;
        while (matcher.find()) {
            for (int i = lastEnd; i < matcher.start(); i++) {
                if (scriptText.charAt(i) == '\n') currentLine++;
            }
            lastEnd = matcher.end();
            int charStart = matcher.start();
            int charEnd = matcher.end();
            if (matcher.group("BLOCKCOMMENT") != null || matcher.group("LINECOMMENT") != null) {
                continue;
            } else if (matcher.group("STRING") != null) {
                String value = stringLiteralToValue(matcher.group());
                tokens.add(new ScriptToken(ScriptTokenType.STRING, value, currentLine, fileName, charStart, charEnd));
            } else if (matcher.group("FLOAT") != null) {
                String tokenText = matcher.group();
                String value = tokenText.substring(0, tokenText.length() - 1);
                tokens.add(new ScriptToken(ScriptTokenType.FLOAT, value, currentLine, fileName, charStart, charEnd));
            } else if (matcher.group("INTEGER") != null) {
                tokens.add(new ScriptToken(ScriptTokenType.INTEGER, matcher.group(), currentLine, fileName, charStart, charEnd));
            } else if (matcher.group("NAME") != null) {
                String tokenText = matcher.group();
                ScriptTokenType nameType = KEYWORD_MAP.getOrDefault(tokenText, ScriptTokenType.NAME);
                tokens.add(new ScriptToken(nameType, tokenText, currentLine, fileName, charStart, charEnd));
            } else if (matcher.group("SYMBOL") != null) {
                ScriptTokenType symbolType = SYMBOL_MAP.get(matcher.group());
                if (symbolType == null) throw new IllegalArgumentException("Script token pattern matched a symbol that does not have a definition");
                tokens.add(new ScriptToken(symbolType, null, currentLine, fileName, charStart, charEnd));
            } else if (matcher.group("UNKNOWN") != null) {
                throw new ScriptCompileException("Invalid token found", fileName, currentLine);
            }
        }
        return tokens;
    }

    private String stringLiteralToValue(String token) {
        return token.substring(1, token.length() - 1).replaceAll("\\\\(.)", "$1");
    }

}
