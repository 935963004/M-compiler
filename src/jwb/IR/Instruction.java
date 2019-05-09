package IR;

import Utils.CompilerError;

import java.util.*;

public abstract class Instruction
{
    private Instruction prev = null, next = null;
    private BasicBlock parentBB;
    protected List<Register> usedRegisters = new ArrayList<>();
    protected List<RegValue> usedRegValues = new ArrayList<>();
    private boolean isRemoved = false;
    private Set<VirtualRegister> liveIn = new HashSet<>(), liveOut = new HashSet<>();

    public Instruction(BasicBlock parentBB)
    {
        this.parentBB = parentBB;
    }

    public void setPrev(Instruction prev)
    {
        this.prev = prev;
    }

    public void setNext(Instruction next)
    {
        this.next = next;
    }

    public Instruction getPrev()
    {
        return prev;
    }

    public Instruction getNext()
    {
        return next;
    }

    public abstract void updateUsed();

    public abstract void accept(IRVisitor irVisitor);

    public BasicBlock getParentBB()
    {
        return parentBB;
    }

    public void prepend(Instruction instruction)
    {
        if (prev == null) parentBB.setHead(instruction);
        else {
            prev.setNext(instruction);
            instruction.setPrev(prev);
        }
        setPrev(instruction);
        instruction.setNext(this);
    }

    public void append(Instruction instruction)
    {
        if (next == null) parentBB.setTail(instruction);
        else {
            next.setPrev(instruction);
            instruction.setNext(next);
        }
        setNext(instruction);
        instruction.setPrev(this);
    }

    public void remove()
    {
        if (isRemoved) throw new CompilerError("Instruction has been removed");
        isRemoved = true;
        if (prev != null) prev.setNext(next);
        if (next != null) next.setPrev(prev);
        if (this instanceof JumpInstruction) parentBB.removeJumpInstruction();
        if (this == parentBB.getHead()) parentBB.setHead(next);
        if (this == parentBB.getTail()) parentBB.setTail(prev);
    }

    public List<Register> getUsedRegisters()
    {
        return usedRegisters;
    }

    public abstract Register getDefinedRegister();

    public abstract void setDefinedRegister(Register register);

    public abstract void setUsedRegisters(Map<Register, Register> renameMap);

    public Set<VirtualRegister> getLiveIn()
    {
        return liveIn;
    }

    public Set<VirtualRegister> getLiveOut()
    {
        return liveOut;
    }

    public abstract Instruction copyRename(Map<Object, Object> renameMap);

    public List<RegValue> getUsedRegValues()
    {
        return usedRegValues;
    }
}
