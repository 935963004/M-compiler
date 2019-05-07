package AST;

public class ThisExprNode extends ExprNode
{
    public ThisExprNode(Location loc)
    {
        this.loc = loc;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return true;
    }
}
