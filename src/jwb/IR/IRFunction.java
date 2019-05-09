package IR;

import ScopeEntity.FuncEntity;

import java.util.*;

public class IRFunction
{
    private String name, builtInFuncLabel;
    private FuncEntity funcEntity;
    private boolean isBuiltIn = false;
    private Set<PhysicalRegister> usedPhysicalGeneralRegs = new HashSet<>();
    private BasicBlock startBB = null, endBB = null;
    private List<VirtualRegister> argVrList = new ArrayList<>();
    private List<Return> returnList = new ArrayList<>();
    private List<BasicBlock> reversePostOrder = null;
    private List<BasicBlock> reversePreOrder = null;
    private Set<BasicBlock> visited = new HashSet<>();
    private Set<IRFunction> calleeSet = new HashSet<>();
    private Set<IRFunction> recursiveCalleeSet = new HashSet<>();
    private List<StackSlot> stackSlots = new ArrayList<>();
    private Map<VirtualRegister, StackSlot> argsStackSlotMap = new HashMap<>();

    public IRFunction(String name, String builtInFuncLabel)
    {
        this.name = name;
        this.builtInFuncLabel = builtInFuncLabel;
        funcEntity = null;
        isBuiltIn = true;
    }

    public IRFunction(FuncEntity funcEntity)
    {
        this.funcEntity = funcEntity;
        name = funcEntity.getName();
        if (funcEntity.isMember()) name = "__member_" + funcEntity.getClassName() + "_" + name;
    }

    public void accept(IRVisitor irVisitor)
    {
        irVisitor.visit(this);
    }

    public Set<PhysicalRegister> getUsedPhysicalGeneralRegs()
    {
        return usedPhysicalGeneralRegs;
    }

    public String getName()
    {
        return name;
    }

    public BasicBlock getStartBB()
    {
        if (startBB == null) startBB = new BasicBlock(this, funcEntity.getName() + "_entry");
        return startBB;
    }

    public List<VirtualRegister> getArgVrList()
    {
        return argVrList;
    }

    public List<Return> getReturnList()
    {
        return returnList;
    }

    public void setEndBB(BasicBlock basicBlock)
    {
        endBB = basicBlock;
    }

    public FuncEntity getFuncEntity()
    {
        return funcEntity;
    }

    public List<BasicBlock> getReversePostOrder()
    {
        if (reversePostOrder == null) calcReversePostOrder();
        return reversePostOrder;
    }

    private void calcReversePostOrder()
    {
        reversePostOrder = new ArrayList<>();
        visited.clear();
        dfsPostOrder(startBB);
        for (int i = 0; i < reversePostOrder.size(); ++i) reversePostOrder.get(i).setPostOrderIndex(i);
        Collections.reverse(reversePostOrder);
    }

    private void dfsPostOrder(BasicBlock bb)
    {
        if (visited.contains(bb)) return;
        visited.add(bb);
        for (BasicBlock nextBB : bb.getNextBBSet()) dfsPostOrder(nextBB);
        reversePostOrder.add(bb);
    }

    public List<BasicBlock> getReversePreOrder()
    {
        if (reversePreOrder == null) calcReversePreOrder();
        return reversePreOrder;
    }

    private void calcReversePreOrder()
    {
        reversePreOrder = new ArrayList<>();
        visited.clear();
        dfsPreOrder(startBB);
        Collections.reverse(reversePreOrder);
    }

    private void dfsPreOrder(BasicBlock bb)
    {
        if (visited.contains(bb)) return;
        visited.add(bb);
        reversePreOrder.add(bb);
        for (BasicBlock nextBB : bb.getNextBBSet()) dfsPreOrder(nextBB);
    }

    public void updateCalleeSet()
    {
        calleeSet.clear();
        for (BasicBlock bb : getReversePostOrder())
            for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if (inst instanceof FunctionCall) calleeSet.add(((FunctionCall) inst).getFunction());
            }
    }

    public Set<IRFunction> getCalleeSet()
    {
        return calleeSet;
    }

    public Set<IRFunction> getRecursiveCalleeSet()
    {
        return recursiveCalleeSet;
    }

    public List<StackSlot> getStackSlots()
    {
        return stackSlots;
    }

    public Map<VirtualRegister, StackSlot> getArgsStackSlotMap()
    {
        return argsStackSlotMap;
    }

    public BasicBlock getEndBB()
    {
        return endBB;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }

    public String getBuiltInFuncLabel()
    {
        return builtInFuncLabel;
    }
}
