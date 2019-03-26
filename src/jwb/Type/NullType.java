package Type;

public class NullType extends Type
{
    static private NullType instance = new NullType();

    private NullType()
    {
        type = allTypes.NULL;
    }

    public static NullType getNullType()
    {
        return instance;
    }
}
