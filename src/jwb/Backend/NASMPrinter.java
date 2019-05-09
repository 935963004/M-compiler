package Backend;

import IR.*;
import Utils.CompilerError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class NASMPrinter implements IRVisitor
{
    private PrintStream outS;
    private PhysicalRegister pr;
    private Map<Object, String> idMap = new HashMap<>();
    private Map<String, Integer> idCnt = new HashMap<>();
    private boolean isBssSection = false, isDataSection = false;

    public NASMPrinter(PrintStream outS)
    {
        this.outS = outS;
    }

    private void printf(String format, Object... args)
    {
        outS.printf(format, args);
    }

    private String dataId(StaticData staticData)
    {
        String id = idMap.get(staticData);
        if (id == null) {
            id = "__static_data_" + newId(staticData.getName());
            idMap.put(staticData, id);
        }
        return id;
    }

    private String bbId(BasicBlock bb)
    {
        String id = idMap.get(bb);
        if (id == null) {
            id = "__block_" + newId(bb.getName());
            idMap.put(bb, id);
        }
        return id;
    }

    private String newId(String id)
    {
        int cnt = idCnt.getOrDefault(id, 0) + 1;
        idCnt.put(id, cnt);
        return id + "_" + cnt;
    }

    private String getStaticStr(String str)
    {
        StringBuilder ret = new StringBuilder();
        int len = str.length();
        for (int i = 1; i < len - 1; ++i) {
            char c = str.charAt(i);
            ret.append((int) c);
            ret.append(", ");
        }
        ret.append(0);
        return ret.toString();
    }

    private String getStrSize(int size)
    {
        String str;
        switch (size) {
            case 1:
                str = "byte";
                break;
            case 2:
                str = "word";
                break;
            case 4:
                str = "dword";
                break;
            case 8:
                str = "qword";
                break;
            default:
                throw new CompilerError("Invalid string size");
        }
        return str;
    }

    @Override
    public void visit(IRRoot node)
    {
        pr = node.getPr();
        idMap.put(node.getFunctions().get("main").getStartBB(), "main");
        printf("\t\tglobal\tmain\n\n");
        printf("\t\textern\tmalloc\n\n");
        if (node.getStaticDataList().size() > 0) {
            isBssSection = true;
            printf("\t\tsection\t.bss\n");
            for (StaticData staticData : node.getStaticDataList()) staticData.accept(this);
            printf("\n");
            isBssSection = false;
        }
        if (node.getStaticStrs().size() > 0) {
            isDataSection = true;
            printf("\t\tsection\t.data\n");
            for (StaticStr staticStr : node.getStaticStrs().values()) staticStr.accept(this);
            printf("\n");
            isDataSection = false;
        }
        printf("\t\tsection\t.text\n\n");
        for (IRFunction irFunction : node.getFunctions().values()) irFunction.accept(this);
        printf("\n");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("lib/builtin_functions.asm"));
            String line;
            while ((line = bufferedReader.readLine()) != null) printf("%s\n", line);
        }
        catch (IOException e) {
            throw new CompilerError("IO exception when reading builtin functions from file");
        }
    }

    @Override
    public void visit(IRFunction node)
    {
        printf("# function %s\n\n", node.getName());
        for (BasicBlock bb : node.getReversePostOrder()) bb.accept(this);
    }

    @Override
    public void visit(PhysicalRegister node)
    {
        printf("%s", node.getName());
    }

    @Override
    public void visit(StaticVar node)
    {
        if (isBssSection) {
            String size;
            switch (node.getSize()) {
                case 1:
                    size = "resb";
                    break;
                case 2:
                    size = "resw";
                    break;
                case 4:
                    size = "resd";
                    break;
                case 8:
                    size = "resq";
                    break;
                default:
                    throw new CompilerError("Invalid static data size");
            }
            printf("%s:\t%s\t1\n", dataId(node), size);
        }
        else printf("%s", dataId(node));
    }

    @Override
    public void visit(VirtualRegister node)
    {
        throw new CompilerError("There should be no virtual register in NASMPrinter");
    }

    @Override
    public void visit(Branch node)
    {
        if (node.getCondition() instanceof ImmediateInt) {
            printf("\t\tjmp\t\t%s\n", ((ImmediateInt) node.getCondition()).getValue() == 1 ? bbId(node.getThenBB()) : bbId(node.getElseBB()));
            return;
        }
        printf("\t\tcmp\t\t");
        node.getCondition().accept(this);
        printf(", 1\n");
        printf("\t\tje\t\t%s\n", bbId(node.getThenBB()));
        if (node.getElseBB().getPostOrderIndex() + 1 == node.getParentBB().getPostOrderIndex()) return;
        printf("\t\tjmp\t\t%s\n", bbId(node.getElseBB()));
    }

    @Override
    public void visit(Jump node)
    {
        if (node.getDestBB().getPostOrderIndex() + 1 == node.getParentBB().getPostOrderIndex()) return;
        printf("\t\tjmp\t\t%s\n", bbId(node.getDestBB()));
    }

    @Override
    public void visit(Return node)
    {
        printf("\t\tret\n\n");
    }

    @Override
    public void visit(ImmediateInt node)
    {
        printf("%d", node.getValue());
    }

    @Override
    public void visit(Move node)
    {
        printf("\t\tmov\t\t");
        node.getLhs().accept(this);
        printf(", ");
        node.getRhs().accept(this);
        printf("\n");
    }

    @Override
    public void visit(BinaryOp node)
    {
        if (node.getOp() == BinaryOp.binaryOp.DIV || node.getOp() == BinaryOp.binaryOp.MOD) {
            printf("\t\tmov\t\trbx, ");
            node.getRhs().accept(this);
            printf("\n\t\tmov\t\trax, ");
            node.getLhs().accept(this);
            printf("\n\t\tmov\t\t%s, rdx\n", pr.getName());
            printf("\t\tcdq\n");
            printf("\t\tidiv\trbx\n");
            printf("\t\tmov\t\t");
            node.getDestination().accept(this);
            if (node.getOp() == BinaryOp.binaryOp.DIV) printf(", rax\n");
            else printf(", rdx\n");
            printf("\t\tmov\t\trdx, %s\n", pr.getName());
        }
        else if (node.getOp() == BinaryOp.binaryOp.SHL || node.getOp() == BinaryOp.binaryOp.SHR) {
            printf("\t\tmov\t\trbx, rcx\n");
            printf("\t\tmov\t\trcx, ");
            node.getRhs().accept(this);
            if (node.getOp() == BinaryOp.binaryOp.SHL) printf("\n\t\tsal\t\t");
            else printf("\n\t\tsar\t\t");
            node.getLhs().accept(this);
            printf(", cl\n");
            printf("\t\tmov\t\trcx, rbx\n");
            printf("\t\tand\t\t");
            node.getLhs().accept(this);
            printf(", -1\n");
        }
        else {
            if (node.getDestination() != node.getLhs()) throw new CompilerError("Binary operation should have same dest and lhs");
            String op;
            switch (node.getOp()) {
                case ADD:
                    if (node.getRhs() instanceof ImmediateInt && ((ImmediateInt) node.getRhs()).getValue() == 1) {
                        printf("\t\tinc\t\t");
                        node.getLhs().accept(this);
                        printf("\n");
                        return;
                    }
                    op = "add\t";
                    break;
                case SUB:
                    if (node.getRhs() instanceof ImmediateInt && ((ImmediateInt) node.getRhs()).getValue() == 1) {
                        printf("\t\tdec\t\t");
                        node.getLhs().accept(this);
                        printf("\n");
                        return;
                    }
                    op = "sub\t";
                    break;
                case MUL:
                    if (node.getRhs() instanceof ImmediateInt && ((ImmediateInt) node.getRhs()).getValue() == 1) return;
                    op = "imul";
                    break;
                case BITWISE_OR:
                    op = "or\t";
                    break;
                case BITWISE_AND:
                    op = "and\t";
                    break;
                case BITWISE_XOR:
                    op = "xor\t";
                    break;
                default:
                    throw new CompilerError("Invalid binary operator");
            }
            printf("\t\t%s\t", op);
            node.getLhs().accept(this);
            printf(", ");
            node.getRhs().accept(this);
            printf("\n");
        }
    }

    @Override
    public void visit(FunctionCall node)
    {
        if (node.getFunction().isBuiltIn()) printf("\t\tcall\t%s\n", node.getFunction().getBuiltInFuncLabel());
        else printf("\t\tcall\t%s\n", bbId(node.getFunction().getStartBB()));
    }

    @Override
    public void visit(Store node)
    {
        if (node.getAddress() instanceof StaticStr) {
            printf("\t\tmov\t\t%s ", getStrSize(node.getSize()));
            node.getAddress().accept(this);
            printf(", ");
            node.getValue().accept(this);
            printf("\n");
            return;
        }
        printf("\t\tmov\t\t%s [", getStrSize(node.getSize()));
        node.getAddress().accept(this);
        if (node.getAddrOffset() > 0) printf("+%d", node.getAddrOffset());
        else if (node.getAddrOffset() < 0) printf("%d", node.getAddrOffset());
        printf("], ");
        node.getValue().accept(this);
        printf("\n");
    }

    @Override
    public void visit(Load node)
    {
        if (node.getAddress() instanceof StaticStr) {
            printf("\t\tmov\t\t");
            node.getDestination().accept(this);
            printf(", %s ", getStrSize(node.getSize()));
            node.getAddress().accept(this);
            printf("\n");
            return;
        }
        printf("\t\tmov\t\t");
        node.getDestination().accept(this);
        printf(", %s [", getStrSize(node.getSize()));
        node.getAddress().accept(this);
        if (node.getAddrOffset() > 0) printf("+%d", node.getAddrOffset());
        else if (node.getAddrOffset() < 0) printf("%d", node.getAddrOffset());
        printf("]\n");
    }

    @Override
    public void visit(UnaryOp node)
    {
        String op;
        switch (node.getOp()) {
            case NEG:
                op = "neg";
                break;
            case BITWISE_NOT:
                op = "not";
                break;
            default:
                throw new CompilerError("Invalid unary operator");
        }
        printf("\t\tmov\t\t");
        node.getDestination().accept(this);
        printf(", ");
        node.getRhs().accept(this);
        printf("\n\t\t%s\t\t", op);
        node.getDestination().accept(this);
        printf("\n");
    }

    @Override
    public void visit(HeapAlloc node)
    {
        printf("\t\tcall\tmalloc\n");
    }

    @Override
    public void visit(Comparison node)
    {
        printf("\t\txor\t\trax, rax\n");
        printf("\t\tcmp\t\t");
        node.getLhs().accept(this);
        printf(", ");
        node.getRhs().accept(this);
        printf("\n");
        String op;
        switch (node.getOp()) {
            case EQUAL:
                op = "sete";
                break;
            case UNEQUAL:
                op = "setne";
                break;
            case LESS:
                op = "setl";
                break;
            case LESS_EQUAL:
                op = "setle";
                break;
            case GREATER:
                op = "setg";
                break;
            case GREATER_EQUAL:
                op = "setge";
                break;
            default:
                throw new CompilerError("Invalid comparison operator");
        }
        printf("\t\t%s\tal\n", op);
        printf("\t\tmov\t\t");
        node.getDestination().accept(this);
        printf(", rax\n");
    }

    @Override
    public void visit(StaticStr node)
    {
        if (isDataSection) {
            printf("%s:\n", dataId(node));
            printf("\t\tdq\t\t%d\n", node.getValue().length() - 2);
            printf("\t\tdb\t\t%s\n", getStaticStr(node.getValue()));
        }
        else printf(dataId(node));
    }

    @Override
    public void visit(BasicBlock node)
    {
        printf("%s:\n", bbId(node));
        for (Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) inst.accept(this);
        printf("\n");
    }

    @Override
    public void visit(Push node)
    {
        printf("\t\tpush\t");
        node.getValue().accept(this);
        printf("\n");
    }

    @Override
    public void visit(Pop node)
    {
        printf("\t\tpop\t\t");
        node.getPr().accept(this);
        printf("\n");
    }
}
