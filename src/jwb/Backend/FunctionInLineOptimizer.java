package Backend;

import IR.*;

import java.util.*;

public class FunctionInLineOptimizer
{
    private class FuncInfo
    {
        int instNum = 0, calledNum = 0;
        boolean isRecursiveCall;
    }

    private IRRoot irRoot;
    private Map<IRFunction, FuncInfo> funcInfoMap = new HashMap<>();

    public FunctionInLineOptimizer(IRRoot irRoot)
    {
        this.irRoot = irRoot;
    }

    public void run()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) irFunction.updateCalleeSet();
        irRoot.updateCalleeSet();
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            irFunction.setRecursive(irFunction.getRecursiveCalleeSet().contains(irFunction));
            FuncInfo funcInfo = new FuncInfo();
            funcInfo.isRecursiveCall = irFunction.isRecursive();
            funcInfoMap.put(irFunction, funcInfo);
        }
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            for (BasicBlock bb : irFunction.getReversePostOrder()) {
                for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    ++funcInfo.instNum;
                    if (inst instanceof FunctionCall) {
                        FuncInfo calleeFuncInfo = funcInfoMap.get(((FunctionCall) inst).getFunction());
                        if (calleeFuncInfo != null) ++calleeFuncInfo.calledNum;
                    }
                }
            }
        }
        List<BasicBlock> reversePostOrder = new ArrayList<>();
        List<String> uncalledFunctions = new ArrayList<>();
        boolean isChanged = true, thisFuncChanged;
        while (isChanged) {
            isChanged = false;
            uncalledFunctions.clear();
            for (IRFunction irFunction : irRoot.getFunctions().values()) {
                FuncInfo funcInfo = funcInfoMap.get(irFunction);
                reversePostOrder.clear();
                reversePostOrder.addAll(irFunction.getReversePostOrder());
                thisFuncChanged = false;
                for (BasicBlock bb : reversePostOrder) {
                    for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                        nextInst = inst.getNext();
                        if (!(inst instanceof FunctionCall)) continue;
                        FuncInfo calleeFuncInfo = funcInfoMap.get(((FunctionCall) inst).getFunction());
                        if (calleeFuncInfo == null || calleeFuncInfo.isRecursiveCall || calleeFuncInfo.instNum > 30 || calleeFuncInfo.instNum + funcInfo.instNum > 1 << 12) continue;
                        nextInst = doInLine((FunctionCall) inst);
                        funcInfo.instNum += calleeFuncInfo.instNum;
                        isChanged = true;
                        thisFuncChanged = true;
                        --calleeFuncInfo.calledNum;
                        if (calleeFuncInfo.calledNum == 0) uncalledFunctions.add(((FunctionCall) inst).getFunction().getName());
                    }
                }
                if (thisFuncChanged) irFunction.calcReversePostOrder();
            }
            for (String funcName : uncalledFunctions) irRoot.getFunctions().remove(funcName);
        }
        for (IRFunction irFunction : irRoot.getFunctions().values()) irFunction.updateCalleeSet();
        irRoot.updateCalleeSet();
    }

    private Instruction doInLine(FunctionCall functionCall)
    {
        IRFunction callerFunction = functionCall.getParentBB().getParentFunc(), calleeFunction = functionCall.getFunction();
        List<BasicBlock> reversePostOrder = calleeFunction.getReversePostOrder();
        Map<Object, Object> renameMap = new HashMap<>();
        BasicBlock oldEndBB = calleeFunction.getEndBB(), newEndBB = new BasicBlock(callerFunction, oldEndBB.getName());
        renameMap.put(oldEndBB, newEndBB);
        renameMap.put(calleeFunction.getStartBB(), functionCall.getParentBB());
        if (callerFunction.getEndBB() == functionCall.getParentBB()) callerFunction.setEndBB(newEndBB);
        Map<Object, Object> callBBRenameMap = Collections.singletonMap(functionCall.getParentBB(), newEndBB);
        for (Instruction inst = functionCall.getNext(); inst != null; inst = inst.getNext()) {
            if (inst instanceof JumpInstruction) newEndBB.setJumpInst((JumpInstruction) inst.copyRename(callBBRenameMap));
            else newEndBB.addInst(inst.copyRename(callBBRenameMap));
            inst.remove();
        }
        Instruction newEndBBHead = newEndBB.getHead();
        for (int i = 0; i < functionCall.getArgsList().size(); ++i) {
            VirtualRegister oldArgVr = calleeFunction.getArgVrList().get(i), newArgVr = oldArgVr.copy();
            functionCall.prepend(new Move(functionCall.getParentBB(), newArgVr, functionCall.getArgsList().get(i)));
            renameMap.put(oldArgVr, newArgVr);
        }
        functionCall.remove();
        for (BasicBlock bb : reversePostOrder) {
            if (!renameMap.containsKey(bb)) renameMap.put(bb, new BasicBlock(callerFunction, bb.getName()));
        }
        for (BasicBlock oldBB : reversePostOrder) {
            BasicBlock newBB = (BasicBlock) renameMap.get(oldBB);
            for (Instruction inst = oldBB.getHead(); inst != null; inst = inst.getNext()) {
                for (RegValue regValue : inst.getUsedRegValues()) {
                    if (!renameMap.containsKey(regValue)) renameMap.put(regValue, regValue.copy());
                }
                Register definedRegister = inst.getDefinedRegister();
                if (definedRegister != null) {
                    if (!renameMap.containsKey(definedRegister)) renameMap.put(definedRegister, definedRegister.copy());
                }
                if (newBB == newEndBB) {
                    if (!(inst instanceof Return)) newEndBBHead.prepend(inst.copyRename(renameMap));
                }
                else {
                    if (inst instanceof JumpInstruction) {
                        if (!(inst instanceof Return)) newBB.setJumpInst((JumpInstruction) inst.copyRename(renameMap));
                    }
                    else newBB.addInst(inst.copyRename(renameMap));
                }
            }
        }
        if (!functionCall.getParentBB().isHasJumpInst()) functionCall.getParentBB().setJumpInst(new Jump(functionCall.getParentBB(), newEndBB));
        Return returnInst = calleeFunction.getReturnList().get(0);
        if (returnInst.getRetValue() != null && functionCall.getDestination() != null) newEndBBHead.prepend(new Move(newEndBB, functionCall.getDestination(), (RegValue) renameMap.get(returnInst.getRetValue())));
        return newEndBB.getHead();
    }
}
