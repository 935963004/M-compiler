package IR;

import Utils.CompilerError;

import java.util.HashSet;
import java.util.Set;

public class BasicBlock
{
    private Instruction head = null, tail = null;
    private IRFunction parentFunc;
    private String name;
    private boolean hasJumpInst = false;
    private Set<BasicBlock> prevBBSet = new HashSet<>(), nextBBSet = new HashSet<>();
    private int postOrderIndex;

    public BasicBlock(IRFunction parentFunc, String name)
    {
        this.parentFunc = parentFunc;
        this.name = name;
    }

    public void addInst(Instruction instruction)
    {
        if (hasJumpInst) throw new CompilerError("Basic block has jump instruction");
        if (head == null) head = tail = instruction;
        else {
            tail.setNext(instruction);
            instruction.setPrev(tail);
            tail = instruction;
        }
    }

    public boolean isHasJumpInst()
    {
        return hasJumpInst;
    }

    public void setJumpInst(JumpInstruction jumpInst)
    {
        addInst(jumpInst);
        hasJumpInst = true;
        if (jumpInst instanceof Branch) {
            addNextBB(((Branch) jumpInst).getThenBB());
            addNextBB(((Branch) jumpInst).getElseBB());
        }
        else if (jumpInst instanceof Jump) addNextBB(((Jump) jumpInst).getDestBB());
        else if (jumpInst instanceof Return) parentFunc.getReturnList().add((Return) jumpInst);
        else throw new CompilerError("Invalid jump instruction");
    }

    private void addNextBB(BasicBlock bb)
    {
        nextBBSet.add(bb);
        if (bb != null) bb.getPrevBBSet().add(this);
    }

    public Set<BasicBlock> getPrevBBSet()
    {
        return prevBBSet;
    }

    public void setHead(Instruction instruction)
    {
        head = instruction;
    }

    public void setTail(Instruction instruction)
    {
        tail = instruction;
    }

    public void removeJumpInstruction()
    {
        hasJumpInst = false;
        if (tail instanceof Branch) {
            removeNextBB(((Branch) tail).getThenBB());
            removeNextBB(((Branch) tail).getElseBB());
        }
        else if (tail instanceof Jump) removeNextBB(((Jump) tail).getDestBB());
        else if (tail instanceof Return) parentFunc.getReturnList().remove(tail);
        else throw new CompilerError("Invalid jump instruction");
    }

    public void removeNextBB(BasicBlock bb)
    {
        nextBBSet.remove(bb);
        if (bb != null) bb.getPrevBBSet().remove(this);
    }

    public Instruction getHead()
    {
        return head;
    }

    public Instruction getTail()
    {
        return tail;
    }

    public boolean getHasJumpInst()
    {
        return hasJumpInst;
    }

    public Set<BasicBlock> getNextBBSet()
    {
        return nextBBSet;
    }

    public String getName()
    {
        return name;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public void setPostOrderIndex(int postOrderIndex)
    {
        this.postOrderIndex = postOrderIndex;
    }

    public int getPostOrderIndex()
    {
        return postOrderIndex;
    }
}
