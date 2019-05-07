package AST;

public class BoolConstExprNode extends ConstExprNode
{
    private boolean value;

    public BoolConstExprNode(boolean value, Location loc)
    {
        this.value = value;
        this.loc = loc;
    }

    public boolean getValue()
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
        if (!(obj instanceof BoolConstExprNode)) return false;
        return value == ((BoolConstExprNode) obj).getValue();
    }
}
