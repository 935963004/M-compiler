package Type;

public class FuncType extends Type
{
    private String name;

    public FuncType(String name)
    {
        this.type = allTypes.FUNCTION;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FuncType)) return false;
        return name.equals(((FuncType) obj).name);
    }
}
