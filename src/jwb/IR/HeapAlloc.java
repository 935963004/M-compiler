package IR;

import java.util.Map;

public class HeapAlloc extends Instruction
{
    private Register destination;
    private RegValue allocSize;

    public HeapAlloc(BasicBlock parentBB, Register destination, RegValue allocSize)
    {
        super(parentBB);
        this.destination = destination;
        this.allocSize = allocSize;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (allocSize instanceof Register) usedRegisters.add((Register) allocSize);
        usedRegValues.add(allocSize);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Register getDestination()
    {
        return destination;
    }

    public RegValue getAllocSize()
    {
        return allocSize;
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
        if (allocSize instanceof Register) allocSize = renameMap.get(allocSize);
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        return new HeapAlloc((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), (RegValue) renameMap.getOrDefault(allocSize, allocSize));
    }
}
