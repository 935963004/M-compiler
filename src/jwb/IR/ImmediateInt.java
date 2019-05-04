package IR;

public class ImmediateInt extends RegValue
{
    private int value;

    public ImmediateInt(int value)
    {
        this.value = value;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public ImmediateInt copy()
    {
        return new ImmediateInt(value);
    }
}
