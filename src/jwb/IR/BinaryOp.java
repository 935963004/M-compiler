package IR;

import java.util.Map;

public class BinaryOp extends Instruction
{
    public enum binaryOp {
        ADD, SUB, MUL, DIV, MOD, SHL, SHR, BITWISE_AND, BITWISE_OR, BITWISE_XOR
    }

    private Register destination;
    private binaryOp op;
    private RegValue lhs, rhs;

    public BinaryOp(BasicBlock parentBB, Register destination, binaryOp op, RegValue lhs, RegValue rhs)
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

    public RegValue getLhs()
    {
        return lhs;
    }

    public RegValue getRhs()
    {
        return rhs;
    }

    public boolean isExchangeable()
    {
        return op == binaryOp.ADD || op == binaryOp.MUL || op == binaryOp.BITWISE_AND || op == binaryOp.BITWISE_OR || op == binaryOp.BITWISE_XOR;
    }

    public void setLhs(RegValue lhs)
    {
        this.lhs = lhs;
        updateUsed();
    }

    public void setRhs(RegValue rhs)
    {
        this.rhs = rhs;
        updateUsed();
    }

    public boolean isDivMod()
    {
        return op == binaryOp.DIV || op == binaryOp.MOD;
    }

    public binaryOp getOp()
    {
        return op;
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
        return new BinaryOp((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), op, (RegValue) renameMap.getOrDefault(lhs, lhs), (RegValue) renameMap.getOrDefault(rhs, rhs));
    }
}
