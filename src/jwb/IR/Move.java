package IR;

import java.util.Map;

public class Move extends Instruction
{
    private Register lhs;
    private RegValue rhs;

    public Move(BasicBlock parentBB, Register lhs, RegValue rhs)
    {
        super(parentBB);
        this.lhs = lhs;
        this.rhs = rhs;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (rhs instanceof Register) usedRegisters.add((Register) rhs);
        usedRegValues.add(rhs);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Register getLhs()
    {
        return lhs;
    }

    public RegValue getRhs()
    {
        return rhs;
    }

    public void setRhs(RegValue rhs)
    {
        this.rhs = rhs;
    }

    @Override
    public Register getDefinedRegister()
    {
        return lhs;
    }

    @Override
    public void setDefinedRegister(Register register)
    {
        lhs = register;
    }

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap)
    {
        if (rhs instanceof Register) rhs = renameMap.get(rhs);
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return new Move((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(lhs, lhs), (RegValue) renameMap.getOrDefault(rhs, rhs));
    }
}
