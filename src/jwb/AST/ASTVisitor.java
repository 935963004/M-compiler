package AST;

public interface ASTVisitor
{
    void visit(ProgramNode node);
    void visit(FuncDeclNode node);
    void visit(ClassDeclNode node);
    void visit(VarDeclNode node);
    void visit(BlockStmtNode node);
    void visit(ExprStmtNode node);
    void visit(IfElseStmtNode node);
    void visit(WhileStmtNode node);
    void visit(ForStmtNode node);
    void visit(ContinueStmtNode node);
    void visit(BreakStmtNode node);
    void visit(ReturnStmtNode node);
    void visit(SuffixExprNode node);
    void visit(FuncCallExprNode node);
    void visit(ArrayExprNode node);
    void visit(MemExprNode node);
    void visit(PrefixExprNode node);
    void visit(NewExprNode node);
    void visit(BinaryExprNode node);
    void visit(AssignExprNode node);
    void visit(IdExprNode node);
    void visit(ThisExprNode node);
    void visit(NumExprNode node);
    void visit(StrExprNode node);
    void visit(BoolConstExprNode node);
    void visit(NullExprNode node);
    void visit(TypeNode node);
}
