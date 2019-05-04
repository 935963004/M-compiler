package IR;

import java.util.Map;

public class Jump extends JumpInstruction
{
    private BasicBlock destBB;

    public Jump(BasicBlock parentBB, BasicBlock destBB)
    {
        super(parentBB);
        this.destBB = destBB;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    @Override
    public void updateUsed() {}

    public BasicBlock getDestBB()
    {
        return destBB;
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
}
