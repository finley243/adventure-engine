package com.github.finley243.adventureengine.script.parse.nodes;

import com.github.finley243.adventureengine.script.parse.SourceRange;

public sealed interface ASTNode permits ASTBinaryOp, ASTBreak, ASTCollection, ASTCompound, ASTContextRef, ASTContinue, ASTError, ASTFile, ASTFor, ASTFunction, ASTFunctionCall, ASTGameDataRef, ASTGlobalAssignment, ASTGlobalRef, ASTIf, ASTIfBranch, ASTListIndexAssignment, ASTListIndexRef, ASTLiteral, ASTLog, ASTMemberAccess, ASTMemberAssignment, ASTParameter, ASTParameterDefinition, ASTPlayerRef, ASTReturn, ASTStatHolderRef, ASTTernaryOp, ASTUnaryOp, ASTVar, ASTVarAssignment, ASTVarDeclaration {
    SourceRange range();
}
