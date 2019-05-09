package IR;

import java.util.Map;

public class Branch extends JumpInstruction
{
    private RegValue condition;
    private BasicBlock thenBB, elseBB;

    public Branch(BasicBlock parentBB, RegValue condition, BasicBlock thenBB, BasicBlock elseBB)
    {
        super(parentBB);
        this.condition = condition;
        this.thenBB = thenBB;
        this.elseBB = elseBB;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (condition instanceof Register) usedRegisters.add((Register) condition);
        usedRegValues.add(condition);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public BasicBlock getThenBB()
    {
        return thenBB;
    }

    public BasicBlock getElseBB()
    {
        return elseBB;
    }

    public RegValue getCondition()
    {
        return condition;
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
        if (condition instanceof Register) condition = renameMap.get(condition);
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return new Branch((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (RegValue) renameMap.getOrDefault(condition, condition), (BasicBlock) renameMap.getOrDefault(thenBB, thenBB), (BasicBlock) renameMap.getOrDefault(elseBB, elseBB));
    }
}
