package com.github.finley243.adventureengine.script.parse.nodes;

public sealed interface ASTNode permits ASTBinaryOp, ASTBreak, ASTCollection, ASTCompound, ASTContinue, ASTError, ASTFile, ASTFor, ASTFunction, ASTFunctionCall, ASTGameRef, ASTGlobalAssignment, ASTGlobalRef, ASTIf, ASTIfBranch, ASTListIndexAssignment, ASTListIndexRef, ASTLiteral, ASTLog, ASTParameter, ASTParameterDefinition, ASTReturn, ASTStatAssignment, ASTStatHolderRef, ASTStatRef, ASTTernaryOp, ASTUnaryOp, ASTVar, ASTVarAssignment, ASTVarDeclaration {
}
