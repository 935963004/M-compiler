package Scope;

import Type.Type;

abstract public class Entity
{
    private String name;
    private Type type;

    public Entity(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }
}
