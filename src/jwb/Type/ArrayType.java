package Type;

public class ArrayType extends Type
{
    private Type baseType;

    public ArrayType(Type baseType)
    {
        this.baseType = baseType;
        this.type = allTypes.ARRAY;
        this.varSize = 8;
    }

    public Type getBaseType()
    {
        return baseType;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ArrayType)) return false;
        return baseType.equals(((ArrayType) obj).baseType);
    }
}
