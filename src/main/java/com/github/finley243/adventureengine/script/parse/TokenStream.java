package com.github.finley243.adventureengine.script.parse;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TokenStream {

    private final List<ScriptToken> tokens;
    private int index;

    public TokenStream(List<ScriptToken> tokens) {
        this.tokens = tokens;
    }

    ScriptToken peek() { return tokens.get(index); }
    ScriptToken current() { return index == 0 ? null : tokens.get(index - 1); }
    ScriptToken consume() { return tokens.get(index++); }
    boolean hasNext() { return index < tokens.size(); }

    boolean expect(ScriptTokenType type) {
        if (peek().type() != type) {
            return false;
        }
        consume();
        return true;
    }

    TokenStream consumeBlock(ScriptTokenType open, ScriptTokenType close) {
        boolean openIsPresent = expect(open);
        if (!openIsPresent) return null;
        int start = index;
        int depth = 1;
        while (hasNext() && depth > 0) {
            ScriptTokenType t = consume().type();
            if (t == open) depth++;
            else if (t == close) depth--;
        }
        if (depth > 0) return null;
        return new TokenStream(tokens.subList(start, index - 1));
    }

    TokenStream consumeUntil(ScriptTokenType end) {
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        List<ScriptToken> contents = new ArrayList<>();
        while (hasNext()) {
            ScriptTokenType type = peek().type();
            if (bracketStack.isEmpty() && type == end) {
                consume();
                break;
            }
            if (type == ScriptTokenType.PARENTHESIS_OPEN || type == ScriptTokenType.BRACKET_OPEN || type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.push(type);
            } else if (type == ScriptTokenType.PARENTHESIS_CLOSE && !bracketStack.isEmpty() && bracketStack.peek() == ScriptTokenType.PARENTHESIS_OPEN) {
                bracketStack.pop();
            } else if (type == ScriptTokenType.BRACKET_CLOSE && !bracketStack.isEmpty() && bracketStack.peek() == ScriptTokenType.BRACKET_OPEN) {
                bracketStack.pop();
            } else if (type == ScriptTokenType.BRACKET_SQUARE_CLOSE && !bracketStack.isEmpty() && bracketStack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                bracketStack.pop();
            }
            contents.add(consume());
        }
        if (!bracketStack.isEmpty()) {
            return null;
        }
        return new TokenStream(contents);
    }

    void syncToEndOfBlock() {
        Deque<ScriptTokenType> stack = new ArrayDeque<>();
        while (hasNext()) {
            ScriptTokenType type = consume().type();
            if (type == ScriptTokenType.PARENTHESIS_OPEN || type == ScriptTokenType.BRACKET_OPEN || type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                stack.push(type);
            } else if (type == ScriptTokenType.BRACKET_CLOSE) {
                if (stack.isEmpty()) break;
                if (stack.peek() == ScriptTokenType.BRACKET_OPEN) stack.pop();
            } else if (type == ScriptTokenType.PARENTHESIS_CLOSE) {
                if (!stack.isEmpty() && stack.peek() == ScriptTokenType.PARENTHESIS_OPEN) stack.pop();
            } else if (type == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
                if (!stack.isEmpty() && stack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) stack.pop();
            }
        }
    }

    void syncToNextStatement() {
        Deque<ScriptTokenType> stack = new ArrayDeque<>();
        while (hasNext()) {
            ScriptTokenType type = consume().type();
            if (type == ScriptTokenType.PARENTHESIS_OPEN || type == ScriptTokenType.BRACKET_OPEN || type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                stack.push(type);
            } else if (type == ScriptTokenType.BRACKET_CLOSE) {
                if (stack.isEmpty()) break;
                if (stack.peek() == ScriptTokenType.BRACKET_OPEN) stack.pop();
            } else if (type == ScriptTokenType.PARENTHESIS_CLOSE) {
                if (!stack.isEmpty() && stack.peek() == ScriptTokenType.PARENTHESIS_OPEN) stack.pop();
            } else if (type == ScriptTokenType.BRACKET_SQUARE_CLOSE) {
                if (!stack.isEmpty() && stack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) stack.pop();
            } else if (type == ScriptTokenType.END_LINE && stack.isEmpty()) {
                break;
            }
        }
    }

}
