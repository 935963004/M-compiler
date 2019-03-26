package AST;

public class StrExprNode extends ExprNode
{
    String str;

    public StrExprNode(String str, Location loc)
    {
        this.str = str;
        this.loc = loc;
    }

    public String getStr()
    {
        return str;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
