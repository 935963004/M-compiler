package AST;

public class PrefixExprNode extends ExprNode
{
    public enum prefixOp {
        INC, DEC, POS, NEG, LOGIC_NOT, BITWISE_NOT
    }

    private prefixOp op;
    private ExprNode expr;

    public PrefixExprNode(prefixOp op, ExprNode expr, Location loc)
    {
        this.op = op;
        this.expr = expr;
        this.loc = loc;
    }

    public prefixOp getOp()
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
}
