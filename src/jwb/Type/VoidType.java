package Type;

public class VoidType extends Type
{
    private static VoidType instance = new VoidType();

    private VoidType()
    {
        this.type = allTypes.VOID;
    }

    public static VoidType getVoidType()
    {
        return instance;
    }
}
