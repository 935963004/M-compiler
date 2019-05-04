package Frontend;

import IR.*;

public class BinaryOpTransformer
{
    private IRRoot irRoot;

    public BinaryOpTransformer(IRRoot irRoot)
    {
        this.irRoot = irRoot;
    }

    public void run()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values())
            for (BasicBlock bb : irFunction.getReversePostOrder())
                for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                    nextInst = inst.getNext();
                    if (!(inst instanceof BinaryOp)) continue;
                    BinaryOp binaryInst = (BinaryOp) inst;
                    if (binaryInst.getDestination() == binaryInst.getLhs()) continue;
                    if (binaryInst.getDestination() == binaryInst.getRhs()) {
                        if (binaryInst.isExchangeable()) {
                            binaryInst.setRhs(binaryInst.getLhs());
                            binaryInst.setLhs(binaryInst.getDestination());
                        }
                        else {
                            VirtualRegister vr = new VirtualRegister("rhsBack");
                            binaryInst.prepend(new Move(binaryInst.getParentBB(), vr, binaryInst.getRhs()));
                            binaryInst.prepend(new Move(binaryInst.getParentBB(), binaryInst.getDestination(), binaryInst.getLhs()));
                            binaryInst.setLhs(binaryInst.getDestination());
                            binaryInst.setRhs(vr);
                        }
                    }
                    else if (!binaryInst.isDivMod()) {
                        binaryInst.prepend(new Move(binaryInst.getParentBB(), binaryInst.getDestination(), binaryInst.getLhs()));
                        binaryInst.setLhs(binaryInst.getDestination());
                    }
                }
    }
}
