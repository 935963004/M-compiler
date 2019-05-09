package IR;

import java.util.Map;

public class Push extends Instruction
{
    private RegValue value;

    public Push(BasicBlock parentBB, RegValue value)
    {
        super(parentBB);
        this.value = value;
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

    public RegValue getValue()
    {
        return value;
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return null;
    }
}
