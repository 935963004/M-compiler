package AST;

public class BinaryExprNode extends ExprNode
{
    public enum binaryOp{
        MUL, DIV, MOD, ADD, SUB, SHL, SHR, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, EQUAL, UNEQUAL,
        BITWISE_AND, BITWISE_XOR, BITWISE_OR, LOGIC_AND, LOGIC_OR
    }

    private binaryOp op;
    private ExprNode lhs, rhs;

    public BinaryExprNode(binaryOp op, ExprNode lhs, ExprNode rhs, Location loc)
    {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.loc = loc;
    }

    public binaryOp getOp()
    {
        return op;
    }

    public ExprNode getLhs()
    {
        return lhs;
    }

    public ExprNode getRhs()
    {
        return rhs;
    }

    public void setLhs(ExprNode lhs)
    {
        this.lhs = lhs;
    }

    public void setRhs(ExprNode rhs)
    {
        this.rhs = rhs;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BinaryExprNode)) return false;
        return op == ((BinaryExprNode) obj).getOp() && lhs.equals(((BinaryExprNode) obj).getLhs()) && rhs.equals(((BinaryExprNode) obj).getRhs());
    }
}
