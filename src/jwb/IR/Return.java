package IR;

import java.util.Map;

public class Return extends JumpInstruction
{
    private RegValue retValue;

    public Return(BasicBlock parentBB, RegValue retValue)
    {
        super(parentBB);
        this.retValue = retValue;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (retValue != null) {
            if (retValue instanceof Register) usedRegisters.add((Register) retValue);
            usedRegValues.add(retValue);
        }
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public RegValue getRetValue()
    {
        return retValue;
    }

    @Override
    public Register getDefinedRegister()
    {
        return null;
    }

    @Override
    public void setDefinedRegister(Register register) {}

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap)
    {
        if (retValue != null && retValue instanceof Register) retValue = renameMap.get(retValue);
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return new Return((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (RegValue) renameMap.getOrDefault(retValue, retValue));
    }
}
