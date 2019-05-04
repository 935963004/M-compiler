package IR;

public interface IRVisitor
{
    void visit(IRRoot node);
    void visit(IRFunction node);
    void visit(PhysicalRegister node);
    void visit(StaticVar node);
    void visit(VirtualRegister node);
    void visit(Branch node);
    void visit(Jump node);
    void visit(Return node);
    void visit(ImmediateInt node);
    void visit(Move node);
    void visit(BinaryOp node);
    void visit(FunctionCall node);
    void visit(Store node);
    void visit(Load node);
    void visit(UnaryOp node);
    void visit(HeapAlloc node);
    void visit(Comparison node);
    void visit(StaticStr node);
    void visit(BasicBlock node);
    void visit(Push node);
    void visit(Pop node);
}
