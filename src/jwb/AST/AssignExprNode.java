package AST;

public class AssignExprNode extends ExprNode
{
    private ExprNode lhs, rhs;

    public AssignExprNode(ExprNode lhs, ExprNode rhs, Location loc)
    {
        this.lhs = lhs;
        this.rhs = rhs;
        this.loc = loc;
    }

    public ExprNode getLhs()
    {
        return lhs;
    }

    public ExprNode getRhs()
    {
        return rhs;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
