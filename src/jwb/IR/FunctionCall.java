package IR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCall extends Instruction
{
    private IRFunction function;
    private List<RegValue> argsList;
    private Register destination;

    public FunctionCall(BasicBlock parentBB, IRFunction function, List<RegValue> argsList, VirtualRegister destination)
    {
        super(parentBB);
        this.function = function;
        this.argsList = argsList;
        this.destination = destination;
        updateUsed();
    }

    @Override
    public void updateUsed()
    {
        usedRegisters.clear();
        usedRegValues.clear();
        for (RegValue regValue : argsList) {
            if (regValue instanceof Register) usedRegisters.add((Register) regValue);
            usedRegValues.add(regValue);
        }
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Register getDestination()
    {
        return destination;
    }

    public IRFunction getFunction()
    {
        return function;
    }

    public List<RegValue> getArgsList()
    {
        return argsList;
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
        for (int i = 0; i < argsList.size(); ++i) {
            if (argsList.get(i) instanceof Register) argsList.set(i, renameMap.get((Register) argsList.get(i)));
        }
        updateUsed();
    }

    @Override
    public Instruction copyRename(Map<Object, Object> renameMap)
    {
        List<RegValue> args = new ArrayList<>();
        for (RegValue arg : argsList) args.add((RegValue) renameMap.getOrDefault(arg, arg));
        return new FunctionCall((BasicBlock) renameMap.getOrDefault(getParentBB(), getParentBB()), function, args, (VirtualRegister) renameMap.getOrDefault(destination, destination));
    }
}
