package AST;

public class MemExprNode extends ExprNode
{
    private ExprNode expr;
    private String name;

    public MemExprNode(ExprNode expr, String name, Location loc)
    {
        this.expr = expr;
        this.name = name;
        this.loc = loc;
    }

    public ExprNode getExpr()
    {
        return expr;
    }

    public  String getName()
    {
        return name;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MemExprNode)) return false;
        return expr.equals(((MemExprNode) obj).getExpr()) && name == ((MemExprNode) obj).getName();
    }
}
