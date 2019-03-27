package AST;

import ScopeEntity.VarEntity;

public class IdExprNode extends ExprNode
{
    String name;
    VarEntity entity;

    public IdExprNode(String name, Location loc)
    {
        this.name = name;
        this.loc = loc;
    }

    public String getName()
    {
        return name;
    }

    public void setEntity(VarEntity entity)
    {
        this.entity = entity;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
