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

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
