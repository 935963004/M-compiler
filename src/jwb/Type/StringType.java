package Type;

public class StringType extends Type
{
    private static StringType instance = new StringType();

    private StringType()
    {
        this.type = allTypes.STRING;
        this.varSize = 8;
    }

    public static StringType getStringType()
    {
        return instance;
    }
}
