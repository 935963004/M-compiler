package Backend;

import IR.*;
import Utils.CompilerError;

import java.util.*;

public class RegisterAllocator
{
    private class VrInfo
    {
        Set<VirtualRegister> neighbours = new HashSet<>();
        boolean isRemoved = false;
        Register color = null;
        int degree = 0;
        Set<VirtualRegister> suggestSameVrs = new HashSet<>();
    }

    private IRRoot irRoot;
    private List<PhysicalRegister> physicalRegisters;
    private PhysicalRegister pr0, pr1;
    private int colorsNum;
    private Map<VirtualRegister, VrInfo> vrInfoMap = new HashMap<>();
    private Set<VirtualRegister> vrNodes = new HashSet<>();
    private Set<VirtualRegister> smallDegreeVrNodes = new HashSet<>();
    private Stack<VirtualRegister> stack = new Stack<>();
    private Set<PhysicalRegister> usedColors = new HashSet<>();
    private Map<Register, Register> renameMap = new HashMap<>();

    public RegisterAllocator(IRRoot irRoot)
    {
        this.irRoot = irRoot;
        physicalRegisters = new ArrayList<>(NASMRegisterSet.generalRegs);
        int maxNumFuncArgs = 3;
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            if (irFunction.getArgVrList().size() > maxNumFuncArgs) maxNumFuncArgs = irFunction.getArgVrList().size();
        }
        if (maxNumFuncArgs >= 5) physicalRegisters.remove(NASMRegisterSet.r8);
        if (maxNumFuncArgs >= 6) physicalRegisters.remove(NASMRegisterSet.r9);
        if (irRoot.isHasDivShiftInst()) {
            pr0 = physicalRegisters.get(0);
            pr1 = physicalRegisters.get(1);
        }
        else {
            pr0 = NASMRegisterSet.rbx;
            pr1 = physicalRegisters.get(0);
        }
        irRoot.setPr(pr0);
        physicalRegisters.remove(pr0);
        physicalRegisters.remove(pr1);
        colorsNum = physicalRegisters.size();
    }

    public void run()
    {
        registerPreProcess();
        //graphColoringAllocate();
        naiveAllocate();
    }

    private void registerPreProcess()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            Instruction instruction = irFunction.getStartBB().getHead();
            for (int i = 6; i < irFunction.getArgVrList().size(); ++i) {
                VirtualRegister vr = irFunction.getArgVrList().get(i);
                StackSlot stackSlot = new StackSlot(irFunction, "arg" + i, true);
                irFunction.getArgsStackSlotMap().put(vr, stackSlot);
                instruction.prepend(new Load(instruction.getParentBB(), vr, 8, stackSlot, 0));
            }
            if (irFunction.getArgVrList().size() > 0) irFunction.getArgVrList().get(0).setForcedPr(NASMRegisterSet.rdi);
            if (irFunction.getArgVrList().size() > 1) irFunction.getArgVrList().get(1).setForcedPr(NASMRegisterSet.rsi);
            if (irFunction.getArgVrList().size() > 2) irFunction.getArgVrList().get(2).setForcedPr(NASMRegisterSet.rdx);
            if (irFunction.getArgVrList().size() > 3) irFunction.getArgVrList().get(3).setForcedPr(NASMRegisterSet.rcx);
            if (irFunction.getArgVrList().size() > 4) irFunction.getArgVrList().get(4).setForcedPr(NASMRegisterSet.r8);
            if (irFunction.getArgVrList().size() > 5) irFunction.getArgVrList().get(5).setForcedPr(NASMRegisterSet.r9);
        }
    }

    private void livelinessAnalyse()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            for (BasicBlock bb : irFunction.getReversePreOrder()) {
                for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    inst.getLiveIn().clear();
                    inst.getLiveOut().clear();
                }
            }
            Set<VirtualRegister> liveIn = new HashSet<>(), liveOut = new HashSet<>();
            boolean flag = true;
            while (flag) {
                flag = false;
                for (BasicBlock bb : irFunction.getReversePreOrder()) {
                    for (Instruction inst = bb.getTail(); inst != null; inst = inst.getPrev()) {
                        liveIn.clear();
                        liveOut.clear();
                        if (inst instanceof JumpInstruction) {
                            if (inst instanceof Jump) liveOut.addAll(((Jump) inst).getDestBB().getHead().getLiveIn());
                            else if (inst instanceof Branch) {
                                liveOut.addAll(((Branch) inst).getThenBB().getHead().getLiveIn());
                                liveOut.addAll(((Branch) inst).getElseBB().getHead().getLiveIn());
                            }
                        }
                        else if (inst.getNext() != null) liveOut.addAll(inst.getNext().getLiveIn());
                        liveIn.addAll(liveOut);
                        if (inst.getDefinedRegister() instanceof VirtualRegister) liveIn.remove(inst.getDefinedRegister());
                        for (Register register : inst.getUsedRegisters()) {
                            if (register instanceof VirtualRegister) liveIn.add((VirtualRegister) register);
                        }
                        if (!inst.getLiveIn().equals(liveIn)) {
                            flag = true;
                            inst.getLiveIn().clear();
                            inst.getLiveIn().addAll(liveIn);
                        }
                        if (!inst.getLiveOut().equals(liveOut)) {
                            flag = true;
                            inst.getLiveOut().clear();
                            inst.getLiveOut().addAll(liveOut);
                        }
                    }
                }
            }
        }
    }

    private void graphColoringAllocate()
    {
        livelinessAnalyse();
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            vrInfoMap.clear();
            vrNodes.clear();
            smallDegreeVrNodes.clear();
            buildGraph(irFunction);
            colorize(irFunction);
            updateInstruction(irFunction);
        }
    }

    private VrInfo getVrInfo(VirtualRegister vr)
    {
        VrInfo vrInfo = vrInfoMap.get(vr);
        if (vrInfo == null) {
            vrInfo = new VrInfo();
            vrInfoMap.put(vr, vrInfo);
        }
        return vrInfo;
    }

    private void removeVrNode(VirtualRegister vr)
    {
        VrInfo vrInfo = vrInfoMap.get(vr), neighbourInfo;
        vrInfo.isRemoved = true;
        vrNodes.remove(vr);
        for (VirtualRegister neighbour : vrInfo.neighbours) {
            neighbourInfo = vrInfoMap.get(neighbour);
            if (!neighbourInfo.isRemoved) {
                --neighbourInfo.degree;
                if (neighbourInfo.degree < colorsNum) smallDegreeVrNodes.add(neighbour);
            }
        }
    }

    private void buildGraph(IRFunction irFunction)
    {
        for (VirtualRegister vr : irFunction.getArgVrList()) getVrInfo(vr);
        for (BasicBlock bb : irFunction.getReversePreOrder()) {
            for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                Register definedRegister = inst.getDefinedRegister();
                if (!(definedRegister instanceof VirtualRegister)) continue;
                VrInfo vrInfo = getVrInfo((VirtualRegister) definedRegister);
                if (inst instanceof Move) {
                    RegValue rhs = ((Move) inst).getRhs();
                    if (rhs instanceof VirtualRegister) {
                        vrInfo.suggestSameVrs.add((VirtualRegister) rhs);
                        getVrInfo((VirtualRegister) rhs).suggestSameVrs.add((VirtualRegister) definedRegister);
                    }
                    for (VirtualRegister vr : inst.getLiveOut()) {
                        if (vr != definedRegister && vr != rhs) {
                            getVrInfo(vr).neighbours.add((VirtualRegister) definedRegister);
                            getVrInfo((VirtualRegister) definedRegister).neighbours.add(vr);
                        }
                    }
                }
                else {
                    for (VirtualRegister vr : inst.getLiveOut()) {
                        if (vr != definedRegister) {
                            getVrInfo(vr).neighbours.add((VirtualRegister) definedRegister);
                            getVrInfo((VirtualRegister) definedRegister).neighbours.add(vr);
                        }
                    }
                }
            }
        }
        for (VrInfo vrInfo : vrInfoMap.values()) vrInfo.degree = vrInfo.neighbours.size();
        vrNodes.addAll(vrInfoMap.keySet());
        for (VirtualRegister vr : vrNodes) {
            if (vrInfoMap.get(vr).degree < colorsNum) smallDegreeVrNodes.add(vr);
        }
    }

    private void colorize(IRFunction irFunction)
    {
        stack.clear();
        while (!vrNodes.isEmpty()) {
            while (!smallDegreeVrNodes.isEmpty()) {
                Iterator<VirtualRegister> iterator = smallDegreeVrNodes.iterator();
                VirtualRegister vr = iterator.next();
                iterator.remove();
                removeVrNode(vr);
                stack.push(vr);
            }
            if (vrNodes.isEmpty()) break;
            Iterator<VirtualRegister> iterator = vrNodes.iterator();
            VirtualRegister vr = iterator.next();
            iterator.remove();
            removeVrNode(vr);
            stack.push(vr);
        }
        while (!stack.isEmpty()) {
            VirtualRegister vr = stack.pop();
            VrInfo vrInfo = vrInfoMap.get(vr);
            vrInfo.isRemoved = false;
            usedColors.clear();
            for (VirtualRegister neighbours : vrInfo.neighbours) {
                VrInfo neighbourInfo = vrInfoMap.get(neighbours);
                if (!neighbourInfo.isRemoved && neighbourInfo.color instanceof PhysicalRegister) usedColors.add((PhysicalRegister) neighbourInfo.color);
            }
            PhysicalRegister forcedPr = vr.getForcedPr();
            if (forcedPr != null) {
                if (usedColors.contains(forcedPr)) throw new CompilerError("Invalid physical register");
                vrInfo.color = forcedPr;
            }
            else {
                for (VirtualRegister suggestSameVr : vrInfo.suggestSameVrs) {
                    Register color = getVrInfo(suggestSameVr).color;
                    if (color instanceof PhysicalRegister && !usedColors.contains(color)) {
                        vrInfo.color = color;
                        break;
                    }
                }
                if (vrInfo.color == null) {
                    for (PhysicalRegister pr : physicalRegisters) {
                        if (!usedColors.contains(pr)) {
                            vrInfo.color = pr;
                            break;
                        }
                    }
                    if (vrInfo.color == null) {
                        vrInfo.color = irFunction.getArgsStackSlotMap().get(vr);
                        if (vrInfo.color == null) vrInfo.color = new StackSlot(irFunction, vr.getName(), false);
                    }
                }
            }
        }
    }

    private void updateInstruction(IRFunction irFunction)
    {
        for (BasicBlock bb : irFunction.getReversePreOrder()) {
            for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                nextInst = inst.getNext();
                if (inst instanceof FunctionCall) {
                    List<RegValue> argsList = ((FunctionCall) inst).getArgsList();
                    for (int i = 0; i < argsList.size(); ++i) {
                        if ( argsList.get(i) instanceof VirtualRegister) argsList.set(i, vrInfoMap.get(argsList.get(i)).color);
                    }
                }
                else if (!inst.getUsedRegisters().isEmpty()) {
                    boolean usedPr0 = false;
                    renameMap.clear();
                    for (Register register : inst.getUsedRegisters()) {
                        if (register instanceof VirtualRegister) {
                            Register color = vrInfoMap.get(register).color;
                            if (color instanceof StackSlot) {
                                PhysicalRegister pr = usedPr0 ? pr1 : pr0;
                                usedPr0 = true;
                                inst.prepend(new Load(bb, pr, 8, color, 0));
                                renameMap.put(register, pr);
                                irFunction.getUsedPhysicalGeneralRegs().add(pr);
                            }
                            else {
                                renameMap.put(register, color);
                                irFunction.getUsedPhysicalGeneralRegs().add((PhysicalRegister) color);
                            }
                        }
                        else renameMap.put(register, register);
                    }
                    inst.setUsedRegisters(renameMap);
                }
                if (inst.getDefinedRegister() instanceof VirtualRegister) {
                    Register color = vrInfoMap.get(inst.getDefinedRegister()).color;
                    if (color instanceof StackSlot) {
                        inst.setDefinedRegister(pr0);
                        inst.append(new Store(bb, pr0, 8, color, 0));
                        irFunction.getUsedPhysicalGeneralRegs().add(pr0);
                    }
                    else {
                        inst.setDefinedRegister(color);
                        irFunction.getUsedPhysicalGeneralRegs().add((PhysicalRegister) color);
                    }
                }
            }
        }
    }

    private Map<VirtualRegister, StackSlot> vrStackSlotMap = new HashMap<>();

    private StackSlot getStackSlot(VirtualRegister vr, IRFunction irFunction)
    {
        StackSlot stackSlot = vrStackSlotMap.get(vr);
        if (stackSlot == null) {
            stackSlot = new StackSlot(irFunction, vr.getName(), false);
            vrStackSlotMap.put(vr, stackSlot);
        }
        return stackSlot;
    }

    private void naiveAllocate()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            vrStackSlotMap.clear();
            vrStackSlotMap.putAll(irFunction.getArgsStackSlotMap());
            for (BasicBlock bb : irFunction.getReversePostOrder()) {
                for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                    nextInst = inst.getNext();
                    int cnt = 0;
                    if (inst instanceof FunctionCall) {
                        List<RegValue> argsList = ((FunctionCall) inst).getArgsList();
                        for (int i = 0; i < argsList.size(); ++i) {
                            RegValue regValue = argsList.get(i);
                            if (regValue instanceof VirtualRegister) argsList.set(i, getStackSlot((VirtualRegister) regValue, irFunction));
                        }
                    }
                    else {
                        Collection<Register> usedRegisters = inst.getUsedRegisters();
                        if (!usedRegisters.isEmpty()) {
                            renameMap.clear();
                            for (Register register : usedRegisters) {
                                if (register instanceof VirtualRegister) {
                                    PhysicalRegister pr = ((VirtualRegister) register).getForcedPr();
                                    if (pr == null) pr = physicalRegisters.get(cnt++);
                                    renameMap.put(register, pr);
                                    irFunction.getUsedPhysicalGeneralRegs().add(pr);
                                    inst.prepend(new Load(bb, pr, 8, getStackSlot((VirtualRegister) register, irFunction), 0));
                                }
                                else renameMap.put(register, register);
                            }
                            inst.setUsedRegisters(renameMap);
                        }
                    }
                    Register definedRegister = inst.getDefinedRegister();
                    if (inst instanceof BinaryOp && !((BinaryOp) inst).isDivMod()) {
                        if (definedRegister instanceof  VirtualRegister) inst.append(new Store(bb, ((BinaryOp) inst).getLhs(), 8, getStackSlot((VirtualRegister) definedRegister, irFunction), 0));
                        inst.setDefinedRegister((Register) ((BinaryOp) inst).getLhs());
                        continue;
                    }
                    if (definedRegister instanceof VirtualRegister) {
                        PhysicalRegister pr = ((VirtualRegister) definedRegister).getForcedPr();
                        if (pr == null) pr = physicalRegisters.get(cnt++);
                        irFunction.getUsedPhysicalGeneralRegs().add(pr);
                        inst.setDefinedRegister(pr);
                        inst.append(new Store(bb, pr, 8, getStackSlot((VirtualRegister) definedRegister, irFunction), 0));
                    }
                }
            }
        }
    }
}
