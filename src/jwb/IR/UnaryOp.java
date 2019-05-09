package IR;

import java.util.Map;

public class UnaryOp extends Instruction
{
    public enum unaryOp {
        BITWISE_NOT, NEG
    }

    private Register destination;
    private unaryOp op;
    private RegValue rhs;

    public UnaryOp(BasicBlock parentBB, Register destination, unaryOp op, RegValue rhs)
    {
        super(parentBB);
        this.destination = destination;
        this.op = op;
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

    public Register getDestination()
    {
        return destination;
    }

    public unaryOp getOp()
    {
        return op;
    }

    public RegValue getRhs()
    {
        return rhs;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    @Override
    public Register getDefinedRegister()
    {
        return destination;
    }

    @Override
    public void setDefinedRegister(Register register)
    {
        destination = register;
    }

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap)
    {
        if (rhs instanceof Register) rhs = renameMap.get(rhs);
        updateUsed();
    }

    @Override
    public UnaryOp copyRename(Map<Object, Object> renameMap) {
        return new UnaryOp((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), op, (RegValue) renameMap.getOrDefault(rhs, rhs));
    }
}
