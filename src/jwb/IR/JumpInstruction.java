package IR;

public abstract class JumpInstruction extends Instruction
{
    public JumpInstruction(BasicBlock parentBB)
    {
        super(parentBB);
    }

    public abstract void accept(IRVisitor irVisitor);
}
