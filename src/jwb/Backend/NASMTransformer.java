package Backend;

import IR.*;

import java.util.*;

public class NASMTransformer
{
    private class FuncInfo
    {
        List<PhysicalRegister> usedCallerSaveRegs = new ArrayList<>();
        List<PhysicalRegister> usedCalleeSaveRegs = new ArrayList<>();
        Set<PhysicalRegister> recursiveUsedRegs = new HashSet<>();
        Map<StackSlot, Integer> stackSlotOffsetMap = new HashMap<>();
        int extraArgsNum, stackSlotNum;
    }

    private IRRoot irRoot;
    private Map<IRFunction, FuncInfo> funcInfoMap = new HashMap<>();

    public NASMTransformer(IRRoot irRoot)
    {
        this.irRoot = irRoot;
    }

    public void run()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = new FuncInfo();
            for (PhysicalRegister pr : irFunction.getUsedPhysicalGeneralRegs()) {
                if (pr.isCallerSave()) funcInfo.usedCallerSaveRegs.add(pr);
                if (pr.isCalleeSave()) funcInfo.usedCalleeSaveRegs.add(pr);
            }
            funcInfo.usedCalleeSaveRegs.add(NASMRegisterSet.rbx);
            funcInfo.usedCalleeSaveRegs.add(NASMRegisterSet.rbp);

            funcInfo.stackSlotNum = irFunction.getStackSlots().size();
            for (int i = 0; i < funcInfo.stackSlotNum; ++i) funcInfo.stackSlotOffsetMap.put(irFunction.getStackSlots().get(i), 8 * i);
            if ((funcInfo.usedCalleeSaveRegs.size() + funcInfo.stackSlotNum) % 2 == 0) ++funcInfo.stackSlotNum;

            funcInfo.extraArgsNum = irFunction.getArgVrList().size() > 6 ? irFunction.getArgVrList().size() - 6 : 0;
            int extraArgOffset = (funcInfo.usedCalleeSaveRegs.size() + funcInfo.stackSlotNum + 1) * 8;
            for (int i = 0; i < funcInfo.extraArgsNum; ++i) {
                funcInfo.stackSlotOffsetMap.put(irFunction.getArgsStackSlotMap().get(irFunction.getArgVrList().get(i + 6)), extraArgOffset);
                extraArgOffset += 8;
            }
            funcInfoMap.put(irFunction, funcInfo);
        }

        for (IRFunction irFunction : irRoot.getBuiltInFunctions().values()) funcInfoMap.put(irFunction, new FuncInfo());
        for (IRFunction irFunction : funcInfoMap.keySet()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            funcInfo.recursiveUsedRegs.addAll(irFunction.getUsedPhysicalGeneralRegs());
            for (IRFunction irFunction1 : irFunction.getRecursiveCalleeSet()) funcInfo.recursiveUsedRegs.addAll(irFunction1.getUsedPhysicalGeneralRegs());
        }

        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            BasicBlock startBB = irFunction.getStartBB();
            Instruction head = startBB.getHead();
            for (PhysicalRegister pr : funcInfo.usedCalleeSaveRegs) head.prepend(new Push(startBB, pr));
            if (funcInfo.stackSlotNum > 0) head.prepend(new BinaryOp(startBB, NASMRegisterSet.rsp, BinaryOp.binaryOp.SUB, NASMRegisterSet.rsp, new ImmediateInt(funcInfo.stackSlotNum * 8)));
            head.prepend(new Move(startBB, NASMRegisterSet.rbp, NASMRegisterSet.rsp));
            for (BasicBlock bb : irFunction.getReversePostOrder()) {
                for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                    nextInst = inst.getNext();
                    if (inst instanceof FunctionCall) {
                        FuncInfo calleeFuncInfo = funcInfoMap.get(((FunctionCall) inst).getFunction());
                        int pushCallerSaveRegsNum = 0;
                        for (PhysicalRegister pr : funcInfo.usedCallerSaveRegs) {
                            if (!(pr.isArg() && pr.getArgIdx() < irFunction.getArgVrList().size()) && calleeFuncInfo.recursiveUsedRegs.contains(pr)) {
                                inst.prepend(new Push(bb, pr));
                                ++pushCallerSaveRegsNum;
                            }
                        }
                        int pushArg6RegsNum = irFunction.getArgVrList().size() > 6 ? 6 : irFunction.getArgVrList().size();
                        for (int i = 0; i < pushArg6RegsNum; ++i) inst.prepend(new Push(bb, NASMRegisterSet.argRegs.get(i)));
                        pushCallerSaveRegsNum += pushArg6RegsNum;
                        boolean extraPush = false;
                        if ((pushCallerSaveRegsNum + calleeFuncInfo.extraArgsNum) % 2 == 1) {
                            inst.prepend(new Push(bb, new ImmediateInt(0)));
                            extraPush = true;
                        }
                        List<RegValue> argsList = ((FunctionCall) inst).getArgsList();
                        for (int i = argsList.size() - 1; i >= 6; --i) {
                            if (argsList.get(i) instanceof StackSlot) {
                                inst.prepend(new Load(bb, NASMRegisterSet.rax, 8, NASMRegisterSet.rbp, funcInfo.stackSlotOffsetMap.get(argsList.get(i))));
                                inst.prepend(new Push(bb, NASMRegisterSet.rax));
                            }
                            else inst.prepend(new Push(bb, argsList.get(i)));
                        }

                        int backOffset = 0, tmp = argsList.size() > 6 ? 6 : argsList.size();
                        List<Integer> argsBackOffset = new ArrayList<>();
                        Map<PhysicalRegister, Integer> argsBackOffsetMap = new HashMap<>();
                        for (int i = 0; i < tmp; ++i) {
                            if (argsList.get(i) instanceof PhysicalRegister && ((PhysicalRegister) argsList.get(i)).isArg() && ((PhysicalRegister) argsList.get(i)).getArgIdx() < argsList.size()) {
                                PhysicalRegister pr = (PhysicalRegister) argsList.get(i);
                                if (argsBackOffsetMap.containsKey(pr)) argsBackOffset.add(argsBackOffsetMap.get(pr));
                                else {
                                    argsBackOffset.add(backOffset);
                                    argsBackOffsetMap.put(pr, backOffset);
                                    inst.prepend(new Push(bb, pr));
                                    ++backOffset;
                                }
                            }
                            else argsBackOffset.add(-1);
                        }
                        for (int i = 0; i < tmp; ++i) {
                            if (argsBackOffset.get(i) == -1) {
                                if (argsList.get(i) instanceof StackSlot) {
                                    inst.prepend(new Load(bb, NASMRegisterSet.rax, 8, NASMRegisterSet.rbp, funcInfo.stackSlotOffsetMap.get(argsList.get(i))));
                                    inst.prepend(new Move(bb, NASMRegisterSet.argRegs.get(i), NASMRegisterSet.rax));
                                }
                                else inst.prepend(new Move(bb, NASMRegisterSet.argRegs.get(i), argsList.get(i)));
                            }
                            else inst.prepend(new Load(bb, NASMRegisterSet.argRegs.get(i), 8, NASMRegisterSet.rsp, (backOffset - argsBackOffset.get(i) - 1) * 8));
                        }
                        if (backOffset > 0) inst.prepend(new BinaryOp(bb, NASMRegisterSet.rsp, BinaryOp.binaryOp.ADD, NASMRegisterSet.rsp, new ImmediateInt(backOffset * 8)));

                        if (((FunctionCall) inst).getDestination() != null) inst.append(new Move(bb, ((FunctionCall) inst).getDestination(), NASMRegisterSet.rax));
                        for (PhysicalRegister pr : funcInfo.usedCallerSaveRegs) {
                            if (!(pr.isArg() && pr.getArgIdx() < irFunction.getArgVrList().size()) && calleeFuncInfo.recursiveUsedRegs.contains(pr)) inst.append(new Pop(bb, pr));
                        }
                        for (int i = 0; i < pushArg6RegsNum; ++i) inst.append(new Pop(bb, NASMRegisterSet.argRegs.get(i)));
                        if (extraPush || calleeFuncInfo.extraArgsNum > 0) {
                            int pushArgNum = extraPush ? calleeFuncInfo.extraArgsNum + 1 : calleeFuncInfo.extraArgsNum;
                            inst.append(new BinaryOp(bb, NASMRegisterSet.rsp, BinaryOp.binaryOp.ADD, NASMRegisterSet.rsp, new ImmediateInt(pushArgNum * 8)));
                        }
                    }
                    else if (inst instanceof HeapAlloc) {
                        int pushCallerSaveRegsNum = 0;
                        for (PhysicalRegister pr : funcInfo.usedCallerSaveRegs) {
                            inst.prepend(new Push(bb, pr));
                            ++pushCallerSaveRegsNum;
                        }
                        int arg6Num = irFunction.getArgVrList().size() > 6 ? 6 : irFunction.getArgVrList().size();
                        for (int i = 0; i < arg6Num; ++i) inst.prepend(new Push(bb, NASMRegisterSet.argRegs.get(i)));
                        pushCallerSaveRegsNum += arg6Num;
                        inst.prepend(new Move(bb, NASMRegisterSet.rdi, ((HeapAlloc) inst).getAllocSize()));
                        if (pushCallerSaveRegsNum % 2 == 1) inst.prepend(new Push(bb, new ImmediateInt(0)));
                        inst.append(new Move(bb, ((HeapAlloc) inst).getDestination(), NASMRegisterSet.rax));
                        for (PhysicalRegister pr : funcInfo.usedCallerSaveRegs) inst.append(new Pop(bb, pr));
                        for (int i = 0; i < arg6Num; ++i) inst.append(new Pop(bb, NASMRegisterSet.argRegs.get(i)));
                        if (pushCallerSaveRegsNum % 2 == 1) inst.append(new BinaryOp(bb, NASMRegisterSet.rsp, BinaryOp.binaryOp.ADD, NASMRegisterSet.rsp, new ImmediateInt(8)));
                    }
                    else if (inst instanceof Load) {
                        if (((Load) inst).getAddress() instanceof StackSlot) {
                            ((Load) inst).setAddrOffset(funcInfo.stackSlotOffsetMap.get(((Load) inst).getAddress()));
                            ((Load) inst).setAddress(NASMRegisterSet.rbp);
                        }
                    }
                    else if (inst instanceof Store) {
                        if (((Store) inst).getAddress() instanceof StackSlot) {
                            ((Store) inst).setAddrOffset(funcInfo.stackSlotOffsetMap.get(((Store) inst).getAddress()));
                            ((Store) inst).setAddress(NASMRegisterSet.rbp);
                        }
                    }
                    else if (inst instanceof Move) {
                        if (((Move) inst).getLhs() == ((Move) inst).getRhs()) inst.remove();
                    }
                }
            }

            Return returnInst = irFunction.getReturnList().get(0);
            if (returnInst.getRetValue() != null) returnInst.prepend(new Move(returnInst.getParentBB(), NASMRegisterSet.rax, returnInst.getRetValue()));
            BasicBlock endBB = irFunction.getEndBB();
            Instruction tail = endBB.getTail();
            if (funcInfo.stackSlotNum > 0) tail.prepend(new BinaryOp(endBB, NASMRegisterSet.rsp, BinaryOp.binaryOp.ADD, NASMRegisterSet.rsp, new ImmediateInt(funcInfo.stackSlotNum * 8)));
            for (int i = funcInfo.usedCalleeSaveRegs.size() - 1; i >= 0; --i) tail.prepend(new Pop(endBB, funcInfo.usedCalleeSaveRegs.get(i)));
        }
    }
}
