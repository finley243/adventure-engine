package com.github.finley243.adventureengine.script.parse.nodes;

public sealed interface ScriptASTNode permits ASTBinaryOp, ASTBreak, ASTCollection, ASTCompound, ASTContinue, ASTError, ASTFor, ASTFunctionCall, ASTGameRef, ASTGlobalAssignment, ASTGlobalRef, ASTIf, ASTListIndexAssignment, ASTListIndexRef, ASTLiteral, ASTLog, ASTReturn, ASTStatAssignment, ASTStatHolderRef, ASTStatRef, ASTTernaryOp, ASTUnaryOp, ASTVar, ASTVarAssignment, ASTVarDeclaration {
}
