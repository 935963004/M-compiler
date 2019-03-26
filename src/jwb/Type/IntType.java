package Type;

public class IntType extends Type
{
    private static IntType instance = new IntType();

    private IntType()
    {
        this.type = allTypes.INT;
        this.varSize = 8;
    }

    public static IntType getIntType()
    {
        return instance;
    }
}
