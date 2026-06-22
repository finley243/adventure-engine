package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public record ASTStatHolderRef(Type type, String reference, SourceRange range) implements ScriptASTNode {
    public enum Type {
        PARENT_SUBJECT, PARENT_TARGET, PARENT_AREA, PARENT_ITEM, PARENT_OBJECT, PLAYER, ACTOR, AREA, ITEM, ITEM_TEMPLATE, OBJECT, SCENE
    }
}
