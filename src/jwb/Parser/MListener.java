// Generated from D:/QQPCmgr/Desktop/src/jwb/Parser\M.g4 by ANTLR 4.7.2
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MParser}.
 */
public interface MListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#programSection}.
	 * @param ctx the parse tree
	 */
	void enterProgramSection(MParser.ProgramSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#programSection}.
	 * @param ctx the parse tree
	 */
	void exitProgramSection(MParser.ProgramSectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(MParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(MParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(MParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(MParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void enterVariableDecl(MParser.VariableDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void exitVariableDecl(MParser.VariableDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void enterMemberDecl(MParser.MemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void exitMemberDecl(MParser.MemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#parameterListDecl}.
	 * @param ctx the parse tree
	 */
	void enterParameterListDecl(MParser.ParameterListDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#parameterListDecl}.
	 * @param ctx the parse tree
	 */
	void exitParameterListDecl(MParser.ParameterListDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#parameterDecl}.
	 * @param ctx the parse tree
	 */
	void enterParameterDecl(MParser.ParameterDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#parameterDecl}.
	 * @param ctx the parse tree
	 */
	void exitParameterDecl(MParser.ParameterDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(MParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(MParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MParser#typeType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(MParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link MParser#typeType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(MParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MParser#typeType}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayType}
	 * labeled alternative in {@link MParser#typeType}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#basicType}.
	 * @param ctx the parse tree
	 */
	void enterBasicType(MParser.BasicTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#basicType}.
	 * @param ctx the parse tree
	 */
	void exitBasicType(MParser.BasicTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStmt(MParser.BlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStmt(MParser.BlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code expressionStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStmt(MParser.ExpressionStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code expressionStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStmt(MParser.ExpressionStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifElseStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfElseStmt(MParser.IfElseStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifElseStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfElseStmt(MParser.IfElseStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(MParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(MParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(MParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(MParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(MParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(MParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(MParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(MParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(MParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link MParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(MParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmt}
	 * labeled alternative in {@link MParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterStmt(MParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmt}
	 * labeled alternative in {@link MParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitStmt(MParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDeclStmt}
	 * labeled alternative in {@link MParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStmt(MParser.VarDeclStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDeclStmt}
	 * labeled alternative in {@link MParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStmt(MParser.VarDeclStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code strExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStrExpr(MParser.StrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code strExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStrExpr(MParser.StrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(MParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(MParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nullExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNullExpr(MParser.NullExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nullExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNullExpr(MParser.NullExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpr(MParser.ArrayExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpr(MParser.ArrayExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code suffixExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSuffixExpr(MParser.SuffixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code suffixExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSuffixExpr(MParser.SuffixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemExpr(MParser.MemExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemExpr(MParser.MemExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFuncCallExpr(MParser.FuncCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcCallExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFuncCallExpr(MParser.FuncCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumExpr(MParser.NumExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumExpr(MParser.NumExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpr(MParser.PrefixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpr(MParser.PrefixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolExpr(MParser.BoolExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolExpr(MParser.BoolExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpr(MParser.AssignExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpr(MParser.AssignExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bracketsExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBracketsExpr(MParser.BracketsExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bracketsExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBracketsExpr(MParser.BracketsExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(MParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link MParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(MParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#nonArrayTypeCreator}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayTypeCreator(MParser.NonArrayTypeCreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#nonArrayTypeCreator}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayTypeCreator(MParser.NonArrayTypeCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterErrorCreator(MParser.ErrorCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code errorCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitErrorCreator(MParser.ErrorCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreator(MParser.ArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreator(MParser.ArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonArrayCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayCreator(MParser.NonArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonArrayCreator}
	 * labeled alternative in {@link MParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayCreator(MParser.NonArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MParser.ParameterListContext ctx);
}