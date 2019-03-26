package AST;

public class VarDeclNode extends DeclNode
{
    private TypeNode type;
    private ExprNode expr;

    public VarDeclNode(TypeNode type, String name, ExprNode expr, Location loc)
    {
        this.type = type;
        this.name = name;
        this.expr = expr;
        this.loc = loc;
    }

    public TypeNode getType()
    {
        return type;
    }

    public ExprNode getExpr()
    {
        return expr;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
