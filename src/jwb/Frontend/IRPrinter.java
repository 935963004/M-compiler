package Frontend;

import IR.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IRPrinter implements IRVisitor
{
    private PrintStream outS;
    private boolean isStaticDataDef;
    private Map<StaticData, String> staticDataMap = new HashMap<>();
    private Map<String, Integer> staticDataCnt = new HashMap<>();
    private Map<VirtualRegister, String> vrMap = new HashMap<>();
    private Map<String, Integer> vrCnt = new HashMap<>();
    private Set<BasicBlock> visited = new HashSet<>();
    private Map<BasicBlock, String> bbMap = new HashMap<>();
    private Map<String, Integer> bbCnt = new HashMap<>();

    public IRPrinter(PrintStream outS)
    {
        this.outS = outS;
    }

    private void printf(String format, Object... args)
    {
        outS.printf(format, args);
    }

    private String getStaticDataID(StaticData staticData)
    {
        String id = staticDataMap.get(staticData);
        if (id == null) {
            if (staticData.getName() == null) id = genID("staticData", staticDataCnt);
            else id = genID(staticData.getName(), staticDataCnt);
            staticDataMap.put(staticData, id);
        }
        return id;
    }

    private String getVrID(VirtualRegister vr)
    {
        String id = vrMap.get(vr);
        if (id == null) {
            if (vr.getName() == null) id = genID("vr", vrCnt);
            else id = genID(vr.getName(), vrCnt);
            vrMap.put(vr, id);
        }
        return id;
    }

    private String getBBID(BasicBlock bb)
    {
        String id = bbMap.get(bb);
        if (id == null) {
            if (bb.getName() == null) id = genID("basicBlock", bbCnt);
            else id = genID(bb.getName(), bbCnt);
            bbMap.put(bb, id);
        }
        return id;
    }

    private String genID(String name, Map<String, Integer> cntMap)
    {
        int cnt = cntMap.getOrDefault(name, 0) + 1;
        cntMap.put(name, cnt);
        if (cnt == 1) return name;
        return name + "_" + cnt;
    }

    @Override
    public void visit(IRRoot node)
    {
        isStaticDataDef = true;
        for (StaticData staticData : node.getStaticDataList()) staticData.accept(this);
        isStaticDataDef = false;
        for (StaticStr staticStr : node.getStaticStrs().values()) staticStr.accept(this);
        printf("\n");
        for (IRFunction irFunction : node.getFunctions().values()) irFunction.accept(this);
    }

    @Override
    public void visit(IRFunction node)
    {
        printf("function %s ", node.getName());
        for (VirtualRegister vr : node.getArgVrList()) printf("$%s ", getVrID(vr));
        printf("\n{\n");
        for (BasicBlock bb : node.getReversePostOrder()) bb.accept(this);
        printf("}\n\n");
    }

    @Override
    public void visit(BasicBlock node)
    {
        if (visited.contains(node)) return;
        visited.add(node);
        printf("#%s:\n", getBBID(node));
        for (Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) inst.accept(this);
    }

    @Override
    public void visit(PhysicalRegister node) {}

    @Override
    public void visit(StaticVar node)
    {
        if (isStaticDataDef) printf("space @%s %d\n", getStaticDataID(node), node.getSize());
        else printf("@" + getStaticDataID(node));
    }

    @Override
    public void visit(VirtualRegister node)
    {
        printf("$%s", getVrID(node));
    }

    @Override
    public void visit(Branch node)
    {
        printf("\tbranch ");
        node.getCondition().accept(this);
        printf(" #%s #%s\n", getBBID(node.getThenBB()), getBBID(node.getElseBB()));
    }

    @Override
    public void visit(Jump node)
    {
        printf("\tjump #%s\n", getBBID(node.getDestBB()));
    }

    @Override
    public void visit(Return node)
    {
        printf("\treturn ");
        if (node.getRetValue() != null) node.getRetValue().accept(this);
        else printf("0");
        printf("\n");
    }

    @Override
    public void visit(ImmediateInt node)
    {
        printf("%d", node.getValue());
    }

    @Override
    public void visit(Move node)
    {
        printf("\t");
        node.getLhs().accept(this);
        printf(" = move ");
        node.getRhs().accept(this);
        printf("\n");
    }

    @Override
    public void visit(BinaryOp node)
    {
        printf("\t");
        String op = null;
        switch (node.getOp()) {
            case ADD:
                op = "add";
                break;
            case SUB:
                op = "sub";
                break;
            case MUL:
                op = "mul";
                break;
            case DIV:
                op = "div";
                break;
            case MOD:
                op = "mod";
                break;
            case SHL:
                op = "shl";
                break;
            case SHR:
                op = "shr";
                break;
            case BITWISE_AND:
                op = "and";
                break;
            case BITWISE_OR:
                op = "or";
                break;
            case BITWISE_XOR:
                op = "xor";
                break;
        }
        node.getDestination().accept(this);
        printf(" = %s ", op);
        node.getLhs().accept(this);
        printf(" ");
        node.getRhs().accept(this);
        printf("\n");
    }

    @Override
    public void visit(FunctionCall node)
    {
        printf("\t");
        if (node.getDestination() != null) {
            node.getDestination().accept(this);
            printf(" = ");
        }
        printf("call %s ", node.getFunction().getName());
        for (RegValue regValue : node.getArgsList()) {
            regValue.accept(this);
            printf(" ");
        }
        printf("\n");
    }

    @Override
    public void visit(Store node)
    {
        printf("\tstore %d ", node.getSize());
        node.getAddress().accept(this);
        printf(" ");
        node.getValue().accept(this);
        printf(" %d\n", node.getAddrOffset());
    }

    @Override
    public void visit(Load node)
    {
        printf("\t");
        node.getDestination().accept(this);
        printf(" = load %d ", node.getSize());
        node.getAddress().accept(this);
        printf(" %d\n", node.getAddrOffset());
    }

    @Override
    public void visit(UnaryOp node)
    {
        printf("\t");
        node.getDestination().accept(this);
        printf(" = %s ", node.getOp() == UnaryOp.unaryOp.NEG ? "neg" : "not");
        node.getRhs().accept(this);
        printf("\n");
    }

    @Override
    public void visit(HeapAlloc node)
    {
        printf("\t");
        node.getDestination().accept(this);
        printf(" = alloc ");
        node.getAllocSize().accept(this);
        printf("\n");
    }

    @Override
    public void visit(Comparison node)
    {
        printf("\t");
        String op = null;
        switch (node.getOp()) {
            case EQUAL:
                op = "seq";
                break;
            case UNEQUAL:
                op = "sne";
                break;
            case GREATER:
                op = "sgt";
                break;
            case GREATER_EQUAL:
                op = "sge";
                break;
            case LESS:
                op = "slt";
                break;
            case LESS_EQUAL:
                op = "sle";
                break;
        }
        node.getDestination().accept(this);
        printf(" = %s ", op);
        node.getLhs().accept(this);
        printf(" ");
        node.getRhs().accept(this);
        printf("\n");
    }

    @Override
    public void visit(StaticStr node)
    {
        if (isStaticDataDef) printf("asciiz @%s %s\n", getStaticDataID(node), node.getValue());
        else printf("@%s %s\n", getStaticDataID(node), node.getValue());
    }

    @Override
    public void visit(Push node) {}

    @Override
    public void visit(Pop node) {}
}
