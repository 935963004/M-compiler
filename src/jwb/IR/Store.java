package IR;

import java.util.Map;

public class Store extends Instruction
{
    private RegValue value, address;
    private int size, addrOffset;
    private boolean isStaticData;

    public Store(BasicBlock parentBB, RegValue value, int size, RegValue address, int addrOffset)
    {
        super(parentBB);
        this.value = value;
        this.size = size;
        this.address = address;
        this.addrOffset = addrOffset;
        isStaticData = false;
        updateUsed();
    }

    public Store(BasicBlock parentBB, RegValue value, int size, StaticData address)
    {
        super(parentBB);
        this.value = value;
        this.size = size;
        this.address = address;
        addrOffset = 0;
        isStaticData = true;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (address instanceof Register && !(address instanceof StackSlot)) usedRegisters.add((Register) address);
        if (value instanceof Register) usedRegisters.add((Register) value);
        usedRegValues.add(address);
        usedRegValues.add(value);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public RegValue getValue()
    {
        return value;
    }

    public RegValue getAddress()
    {
        return address;
    }

    public int getSize()
    {
        return size;
    }

    public int getAddrOffset()
    {
        return addrOffset;
    }

    @Override
    public Register getDefinedRegister()
    {
        return null;
    }

    @Override
    public void setDefinedRegister(Register register) {}

    public boolean isStaticData()
    {
        return isStaticData;
    }

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap)
    {
        if (address instanceof Register && !(address instanceof StackSlot)) address = renameMap.get(address);
        if (value instanceof Register) value = renameMap.get(value);
        updateUsed();
    }

    public void setAddress(RegValue address)
    {
        this.address = address;
    }

    public void setAddrOffset(int addrOffset)
    {
        this.addrOffset = addrOffset;
    }

    @Override
    public Store copyRename(Map<Object, Object> renameMap) {
        if (isStaticData) return new Store((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (RegValue) renameMap.getOrDefault(value, value), size, (StaticData) renameMap.getOrDefault(address, address));
        return new Store((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (RegValue) renameMap.getOrDefault(value, value), size, (RegValue) renameMap.getOrDefault(address, address), addrOffset);
    }
}
