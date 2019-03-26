package AST;

public class ExprStmtNode extends StmtNode
{
    private ExprNode expr;

    public ExprStmtNode(ExprNode expr, Location loc)
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
