package Backend;

import IR.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalVarProcessor
{
    private class FuncInfo
    {
        Set<StaticData> definedStaticData = new HashSet<>();
        Set<StaticData> recursiveDefinedStaticData = new HashSet<>();
        Set<StaticData> recursiveUsedStaticData = new HashSet<>();
        Map<StaticData, VirtualRegister> staticDataVrMap = new HashMap<>();
    }

    private IRRoot irRoot;
    private Map<IRFunction, FuncInfo> funcInfoMap = new HashMap<>();

    public GlobalVarProcessor(IRRoot irRoot)
    {
        this.irRoot = irRoot;
    }

    private VirtualRegister getStaticDataVr(Map<StaticData, VirtualRegister> staticDataVrMap, StaticData staticData)
    {
        VirtualRegister vr = staticDataVrMap.get(staticData);
        if (vr == null) {
            vr = new VirtualRegister(null);
            staticDataVrMap.put(staticData, vr);
        }
        return vr;
    }

    public void run()
    {
        for (IRFunction irFunction : irRoot.getFunctions().values()) irFunction.updateCalleeSet();
        irRoot.updateCalleeSet();

        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = new FuncInfo();
            funcInfoMap.put(irFunction, funcInfo);
            Map<Register, Register> renameMap = new HashMap<>();
            for (BasicBlock bb : irFunction.getReversePostOrder()) {
                for (Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                    if (inst instanceof Load && ((Load) inst).isStaticData() || inst instanceof Store && ((Store) inst).isStaticData())
                        continue;
                    if (!inst.getUsedRegisters().isEmpty()) {
                        renameMap.clear();
                        for (Register register : inst.getUsedRegisters()) {
                            if (register instanceof StaticData && !(register instanceof StaticStr))
                                renameMap.put(register, getStaticDataVr(funcInfo.staticDataVrMap, (StaticData) register));
                            else renameMap.put(register, register);
                        }
                        inst.setUsedRegisters(renameMap);
                    }
                    Register definedRegister = inst.getDefinedRegister();
                    if (definedRegister != null && definedRegister instanceof StaticData) {
                        VirtualRegister vr = getStaticDataVr(funcInfo.staticDataVrMap, (StaticData) definedRegister);
                        inst.setDefinedRegister(vr);
                        funcInfo.definedStaticData.add((StaticData) definedRegister);
                    }
                }
            }
            BasicBlock startBB = irFunction.getStartBB();
            Instruction head = startBB.getHead();
            for (StaticData staticData : funcInfo.staticDataVrMap.keySet()) head.prepend(new Load(startBB, funcInfo.staticDataVrMap.get(staticData), 8, staticData, staticData instanceof StaticStr));
        }

        for (IRFunction builtInFunc : irRoot.getBuiltInFunctions().values()) funcInfoMap.put(builtInFunc, new FuncInfo());
        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            funcInfo.recursiveUsedStaticData.addAll(funcInfo.staticDataVrMap.keySet());
            funcInfo.recursiveDefinedStaticData.addAll(funcInfo.definedStaticData);
            for (IRFunction calleeFunc : irFunction.getRecursiveCalleeSet()) {
                FuncInfo calleeFuncInfo = funcInfoMap.get(calleeFunc);
                funcInfo.recursiveUsedStaticData.addAll(calleeFuncInfo.staticDataVrMap.keySet());
                funcInfo.recursiveDefinedStaticData.addAll(calleeFuncInfo.definedStaticData);
            }
        }

        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            if (funcInfo.staticDataVrMap.keySet().isEmpty()) continue;
            for (BasicBlock bb : irFunction.getReversePostOrder()) {
                for (Instruction inst = bb.getHead(), nextInst; inst != null; inst = nextInst) {
                    nextInst = inst.getNext();
                    if (!(inst instanceof FunctionCall)) continue;
                    FuncInfo calleeFuncInfo = funcInfoMap.get(((FunctionCall) inst).getFunction());
                    for (StaticData staticData : funcInfo.definedStaticData) {
                        if (!(staticData instanceof StaticStr) && calleeFuncInfo.recursiveUsedStaticData.contains(staticData))
                            inst.prepend(new Store(bb, funcInfo.staticDataVrMap.get(staticData), 8, staticData));
                    }
                    if (calleeFuncInfo.recursiveDefinedStaticData.isEmpty()) continue;
                    Set<StaticData> loadStaticDataSet = new HashSet<>();
                    loadStaticDataSet.addAll(calleeFuncInfo.recursiveDefinedStaticData);
                    loadStaticDataSet.retainAll(funcInfo.staticDataVrMap.keySet());
                    for (StaticData staticData : loadStaticDataSet) {
                        if (!(staticData instanceof StaticStr))
                            inst.append(new Load(bb, funcInfo.staticDataVrMap.get(staticData), 8, staticData, false));
                    }
                }
            }
        }

        for (IRFunction irFunction : irRoot.getFunctions().values()) {
            FuncInfo funcInfo = funcInfoMap.get(irFunction);
            Return returnInst = irFunction.getReturnList().get(0);
            for (StaticData staticData : funcInfo.definedStaticData) returnInst.prepend(new Store(returnInst.getParentBB(), funcInfo.staticDataVrMap.get(staticData), 8, staticData));
        }
    }
}
