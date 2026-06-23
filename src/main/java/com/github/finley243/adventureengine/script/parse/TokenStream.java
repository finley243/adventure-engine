package com.github.finley243.adventureengine.script.parse;

import java.util.*;

public class TokenStream {

    private final List<ScriptToken> tokens;
    private int index;

    public TokenStream(List<ScriptToken> tokens) {
        this.tokens = tokens;
    }

    boolean hasNext() {
        return index < tokens.size();
    }

    ScriptToken peek() {
        if (tokens.isEmpty()) return null;
        return tokens.get(index);
    }

    ScriptToken current() {
        return index == 0 ? null : tokens.get(index - 1);
    }

    ScriptToken consume() {
        return tokens.get(index++);
    }

    ScriptToken expect(ScriptTokenType type) {
        if (!hasNext() || peek() == null || peek().type() != type) {
            return null;
        }
        return consume();
    }

    ScriptToken expectOneOf(Set<ScriptTokenType> types) {
        if (!hasNext() || peek() == null || !types.contains(peek().type())) {
            return null;
        }
        return consume();
    }

    String expectName() {
        if (!hasNext() || peek() == null || peek().type() != ScriptTokenType.NAME) {
            return null;
        }
        ScriptToken token = consume();
        return token.value();
    }

    BlockResult consumeBlock(ScriptTokenType open, ScriptTokenType close) {
        boolean openIsPresent = expect(open) != null;
        if (!openIsPresent) {
            ScriptToken invalidToken = hasNext() ? peek() : current();
            syncTo(close);
            return new BlockResult(null, invalidToken.charStart(), invalidToken.charEnd(), invalidToken.fileName(), invalidToken.line(), BlockError.MISSING_OPEN);
        }
        int start = index;
        ScriptToken startToken = current();
        int depth = 1;
        while (hasNext() && depth > 0) {
            ScriptTokenType t = consume().type();
            if (t == open) depth++;
            else if (t == close) depth--;
        }
        if (depth > 0) {
            ScriptToken fileEndToken = current();
            return new BlockResult(null, startToken.charStart(), fileEndToken.charEnd(), startToken.fileName(), startToken.line(), BlockError.MISSING_CLOSE);
        }
        TokenStream contents = new TokenStream(tokens.subList(start, index - 1));
        ScriptToken endToken = current();
        return new BlockResult(contents, startToken.charStart(), endToken.charEnd(), startToken.fileName(), startToken.line(), BlockError.NONE);
    }

    StatementResult consumeUntil(ScriptTokenType end) {
        String fileName = hasNext() ? peek().fileName() : tokens.getFirst().fileName();
        int line = hasNext() ? peek().line() : tokens.getFirst().line();
        int charStart = hasNext() ? peek().charStart() : tokens.getFirst().charStart();
        Deque<ScriptTokenType> bracketStack = new ArrayDeque<>();
        List<ScriptToken> contentsList = new ArrayList<>();
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
            contentsList.add(consume());
        }
        int charEnd = current().charEnd();
        if (!bracketStack.isEmpty()) {
            return new StatementResult(null, charStart, charEnd, fileName, line, StatementError.MISSING_END);
        }
        TokenStream contents = new TokenStream(contentsList);
        return new StatementResult(contents, charStart, charEnd, fileName, line, StatementError.NONE);
    }

    void syncTo(ScriptTokenType target) {
        Deque<ScriptTokenType> stack = new ArrayDeque<>();
        while (hasNext()) {
            ScriptTokenType type = hasNext() ? peek().type() : null;
            if (stack.isEmpty() && type == target) {
                consume();
                break;
            }
            consume();
            if (type == ScriptTokenType.PARENTHESIS_OPEN || type == ScriptTokenType.BRACKET_OPEN || type == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                stack.push(type);
            } else if (type == ScriptTokenType.PARENTHESIS_CLOSE && !stack.isEmpty() && stack.peek() == ScriptTokenType.PARENTHESIS_OPEN) {
                stack.pop();
            } else if (type == ScriptTokenType.BRACKET_CLOSE && !stack.isEmpty() && stack.peek() == ScriptTokenType.BRACKET_OPEN) {
                stack.pop();
            } else if (type == ScriptTokenType.BRACKET_SQUARE_CLOSE && !stack.isEmpty() && stack.peek() == ScriptTokenType.BRACKET_SQUARE_OPEN) {
                stack.pop();
            }
        }
    }

}
