package IR;

public class VirtualRegister extends Register
{
    private String name;
    private PhysicalRegister forcedPr = null;

    public VirtualRegister(String name)
    {
        this.name = name;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public String getName()
    {
        return name;
    }

    public void setForcedPr(PhysicalRegister forcedPr)
    {
        this.forcedPr = forcedPr;
    }

    public PhysicalRegister getForcedPr()
    {
        return forcedPr;
    }

    @Override
    public VirtualRegister copy()
    {
        return new VirtualRegister(name);
    }
}
