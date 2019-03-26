package AST;

import Type.Type;

abstract public class ExprNode extends Node
{
    private Type type;
    private boolean isLeftValue;

    public void setType(Type type)
    {
        this.type = type;
    }

    public void setLeftValue(boolean isLeftValue)
    {
        this.isLeftValue = isLeftValue;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isLeftValue()
    {
        return isLeftValue;
    }
}
