package Type;

public class BoolType extends Type
{
    private static BoolType instance = new BoolType();

    private BoolType()
    {
        this.type = allTypes.BOOL;
        this.varSize = 8;
    }

    public static BoolType getBoolType()
    {
        return instance;
    }
}
