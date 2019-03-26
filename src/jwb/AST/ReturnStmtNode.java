package AST;

public class ReturnStmtNode extends StmtNode
{
    ExprNode expr;

    public ReturnStmtNode(ExprNode expr, Location loc)
    {
        this.expr = expr;
        this.loc = loc;
    }

    public ExprNode getExpr()
    {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
