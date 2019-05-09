package IR;

import java.util.Map;

public abstract class JumpInstruction extends Instruction
{
    public JumpInstruction(BasicBlock parentBB)
    {
        super(parentBB);
    }

    public abstract void accept(IRVisitor irVisitor);

    @Override
    public abstract Instruction copyRename(Map<Object, Object> renameMap);
}
