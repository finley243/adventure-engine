package com.github.finley243.adventureengine.script.parse.nodes;

public sealed interface ASTNode permits ASTBinaryOp, ASTBreak, ASTCollection, ASTCompound, ASTContinue, ASTError, ASTFile, ASTFor, ASTFunction, ASTFunctionCall, ASTGlobalAssignment, ASTGlobalRef, ASTIf, ASTIfBranch, ASTListIndexAssignment, ASTListIndexRef, ASTLiteral, ASTLog, ASTMemberAccess, ASTParameter, ASTParameterDefinition, ASTReturn, ASTMemberAssignment, ASTStatHolderRef, ASTTernaryOp, ASTUnaryOp, ASTVar, ASTVarAssignment, ASTVarDeclaration {
}
