package IR;

import java.util.Map;

public class Load extends Instruction
{
    private Register destination;
    private int size, addrOffset;
    private RegValue address;
    private boolean isStaticData, isLoadAddr;

    public Load(BasicBlock parentBB, Register destination, int size, RegValue address, int addrOffset)
    {
        super(parentBB);
        this.destination = destination;
        this.size = size;
        this.address = address;
        this.addrOffset = addrOffset;
        isStaticData = false;
        updateUsed();
    }

    public Load(BasicBlock parentBB, Register destination, int size, StaticData address, boolean isLoadAddr)
    {
        super(parentBB);
        this.destination = destination;
        this.size = size;
        this.address = address;
        addrOffset = 0;
        isStaticData = true;
        this.isLoadAddr = isLoadAddr;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        if (address instanceof Register && !(address instanceof StackSlot)) usedRegisters.add((Register) address);
        usedRegValues.add(address);
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Register getDestination()
    {
        return destination;
    }

    public RegValue getAddress()
    {
        return address;
    }

    public int getAddrOffset()
    {
        return addrOffset;
    }

    public int getSize()
    {
        return size;
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

    public boolean isStaticData()
    {
        return isStaticData;
    }

    @Override
    public void setUsedRegisters(Map<Register, Register> renameMap)
    {
        if (address instanceof Register && !(address instanceof StackSlot)) address = renameMap.get(address);
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
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        if (isStaticData) return new Load((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), size, (StaticData) renameMap.getOrDefault(address, address), isLoadAddr);
        return new Load((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), (Register) renameMap.getOrDefault(destination, destination), size, (RegValue) renameMap.getOrDefault(address, address), addrOffset);
    }
}
