package AST;

import Type.Type;

public class TypeNode extends Node
{
    private Type type;

    public TypeNode(Type type, Location loc)
    {
        this.type = type;
        this.loc = loc;
    }

    public Type getType()
    {
        return this.type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
