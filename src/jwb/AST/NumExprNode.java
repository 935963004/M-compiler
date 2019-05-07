package AST;

public class NumExprNode extends ExprNode
{
    private int value;

    public NumExprNode(int value, Location loc)
    {
        this.value = value;
        this.loc = loc;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof NumExprNode)) return false;
        return value == ((NumExprNode) obj).getValue();
    }
}
