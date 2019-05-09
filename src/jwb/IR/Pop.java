package IR;

import java.util.Map;

public class Pop extends Instruction
{
    private PhysicalRegister pr;

    public Pop(BasicBlock parentBB, PhysicalRegister pr)
    {
        super(parentBB);
        this.pr = pr;
    }

    @Override
    public void updateUsed() {}

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    @Override
    public Register getDefinedRegister()
    {
        return null;
    }

    @Override
    public void setDefinedRegister(Register register) {}

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap) {}

    public PhysicalRegister getPr()
    {
        return pr;
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return null;
    }
}
