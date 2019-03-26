package Type;

abstract public class Type
{
    public enum allTypes {
        VOID, INT, BOOL, STRING, CLASS, ARRAY, FUNCTION, NULL
    }

    int varSize;
    allTypes type;

    public allTypes getType()
    {
        return type;
    }

    public int getVarSize()
    {
        return varSize;
    }
}
