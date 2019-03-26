package AST;

public class ArrayExprNode extends ExprNode
{
    private ExprNode arr, sub;

    public ArrayExprNode(ExprNode arr, ExprNode sub, Location loc)
    {
        this.arr = arr;
        this.sub = sub;
        this.loc = loc;
    }

    public ExprNode getArr()
    {
        return arr;
    }

    public ExprNode getSub()
    {
        return sub;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
