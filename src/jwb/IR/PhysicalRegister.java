package IR;

public abstract class PhysicalRegister extends Register
{
    @Override
    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    @Override
    public RegValue copy()
    {
        return null;
    }

    public abstract String getName();
    public abstract boolean isGeneral();
    public abstract boolean isCallerSave();
    public abstract boolean isCalleeSave();
    public abstract boolean isArg();
    public abstract int getArgIdx();
}
