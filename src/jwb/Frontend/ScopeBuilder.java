package Frontend;

import AST.*;

abstract public class ScopeBuilder implements ASTVisitor
{
    @Override
    public void visit(ProgramNode node) {}

    @Override
    public void visit(FuncDeclNode node) {}

    @Override
    public void visit(ClassDeclNode node) {}

    @Override
    public void visit(VarDeclNode node) {}

    @Override
    public void visit(BlockStmtNode node) {}

    @Override
    public void visit(ExprStmtNode node) {}

    @Override
    public void visit(IfElseStmtNode node) {}

    @Override
    public void visit(WhileStmtNode node) {}

    @Override
    public void visit(ForStmtNode node) {}

    @Override
    public void visit(ContinueStmtNode node) {}

    @Override
    public void visit(BreakStmtNode node) {}

    @Override
    public void visit(ReturnStmtNode node) {}

    @Override
    public void visit(SuffixExprNode node) {}

    @Override
    public void visit(FuncCallExprNode node) {}

    @Override
    public void visit(ArrayExprNode node) {}

    @Override
    public void visit(MemExprNode node) {}

    @Override
    public void visit(PrefixExprNode node) {}

    @Override
    public void visit(NewExprNode node) {}

    @Override
    public void visit(BinaryExprNode node) {}

    @Override
    public void visit(AssignExprNode node) {}

    @Override
    public void visit(IdExprNode node) {}

    @Override
    public void visit(ThisExprNode node) {}

    @Override
    public void visit(NumExprNode node) {}

    @Override
    public void visit(StrExprNode node) {}

    @Override
    public void visit(BoolConstExprNode node) {}

    @Override
    public void visit(NullExprNode node) {}

    @Override
    public void visit(TypeNode node) {}
}
