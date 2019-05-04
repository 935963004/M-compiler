package IR;

public class StackSlot extends Register
{
    private IRFunction parentFunction;
    private String name;

    public StackSlot(IRFunction parentFunction, String name, boolean isArgSlot)
    {
        this.parentFunction = parentFunction;
        this.name = name;
        if (!isArgSlot) parentFunction.getStackSlots().add(this);
    }

    @Override
    public void accept(IRVisitor irVisitor) {}

    @Override
    public RegValue copy()
    {
        return null;
    }
}
