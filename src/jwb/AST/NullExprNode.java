package AST;

public class NullExprNode extends ExprNode
{
    public NullExprNode(Location loc)
    {
        this.loc = loc;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
