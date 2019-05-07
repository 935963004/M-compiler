package AST;

public class SuffixExprNode extends ExprNode
{
    public enum suffixOp{
        INC, DEC
    }

    private suffixOp op;
    private ExprNode expr;

    public SuffixExprNode(suffixOp op, ExprNode expr, Location loc)
    {
        this.op = op;
        this.expr = expr;
        this.loc = loc;
    }

    public suffixOp getOp()
    {
        return op;
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

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SuffixExprNode)) return false;
        return op == ((SuffixExprNode) obj).getOp() && expr.equals(((SuffixExprNode) obj).getExpr());
    }
}
