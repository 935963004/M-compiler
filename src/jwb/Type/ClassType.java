package Type;

public class ClassType extends Type
{
    private String name;

    public ClassType(String name)
    {
        this.name = name;
        this.type = allTypes.CLASS;
        this.varSize = 8;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ClassType)) return false;
        return name.equals(((ClassType) obj).name);
    }
}
