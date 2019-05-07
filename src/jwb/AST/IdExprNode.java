package AST;

import ScopeEntity.VarEntity;

public class IdExprNode extends ExprNode
{
    private String name;
    private VarEntity entity = null;
    private boolean isChecked = false;
    private boolean needMemOp;

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

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public boolean isNeedMemOp()
    {
        return needMemOp;
    }

    public void setNeedMemOp(boolean needMemOp)
    {
        this.needMemOp = needMemOp;
    }

    public VarEntity getEntity()
    {
        return entity;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof IdExprNode)) return false;
        return name.equals(((IdExprNode) obj).getName());
    }
}
