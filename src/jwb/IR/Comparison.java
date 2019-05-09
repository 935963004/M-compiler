package IR;

import java.util.Map;

public class Comparison extends Instruction
{
    public enum comparisonOp {
        GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, UNEQUAL
    }

    private Register destination;
    private comparisonOp op;
    private RegValue lhs, rhs;

    public Comparison(BasicBlock parentBB, Register destination, comparisonOp op, RegValue lhs, RegValue rhs)
    {
        super(parentBB);
        this.destination = destination;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (lhs instanceof Register) usedRegisters.add((Register) lhs);
        if (rhs instanceof Register) usedRegisters.add((Register) rhs);
        usedRegValues.add(lhs);
        usedRegValues.add(rhs);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Register getDestination()
    {
        return destination;
    }

    public comparisonOp getOp()
    {
        return op;
    }

    public RegValue getLhs()
    {
        return lhs;
    }

    public RegValue getRhs()
    {
        return rhs;
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
        if (lhs instanceof Register) lhs = renameMap.get(lhs);
        if (rhs instanceof Register) rhs = renameMap.get(rhs);
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return new Comparison((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), op, (RegValue) renameMap.getOrDefault(lhs, lhs), (RegValue) renameMap.getOrDefault(rhs, rhs));
    }
}
