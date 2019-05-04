package IR;

public abstract class StaticData extends Register
{
    private String name;
    private int size;

    public StaticData(String name, int size)
    {
        this.name = name;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public int getSize()
    {
        return size;
    }

    public abstract void accept(IRVisitor irVisitor);

    @Override
    public StaticData copy()
    {
        return this;
    }
}
