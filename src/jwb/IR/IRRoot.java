package IR;

import java.util.*;

public class IRRoot
{
    private Map<String, IRFunction> builtInFunctions = new HashMap<>(), functions = new HashMap<>();
    private List<StaticData> staticDataList = new ArrayList<>();
    private boolean hasDivShiftInst = false;
    private Map<String, StaticStr> staticStrs = new HashMap<>();
    private PhysicalRegister pr;

    public IRRoot()
    {
        IRFunction irFunction;
        irFunction = new IRFunction("__builtin_string_concat", "__builtin_string_concat");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__builtin_string_equal", "__builtin_string_equal");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__builtin_string_unequal", "__builtin_string_unequal");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__builtin_string_less", "__builtin_string_less");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__builtin_string_less_equal", "__builtin_string_less_equal");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("print", "_Z5printPc");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("println", "_Z7printlnPc");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("getString", "_Z9getStringv");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("getInt", "_Z6getIntv");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("toString", "_Z8toStringi");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__member_string_substring", "_Z27__member___string_substringPcii");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__member_string_parseInt", "_Z26__member___string_parseIntPc");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("__member_string_ord", "_Z21__member___string_ordPci");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("printInt", "_Z8printInti");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
        irFunction = new IRFunction("printlnInt", "_Z10printlnInti");
        irFunction.getUsedPhysicalGeneralRegs().addAll(NASMRegisterSet.generalRegs);
        builtInFunctions.put(irFunction.getName(), irFunction);
    }

    public Map<String, IRFunction> getFunctions()
    {
        return functions;
    }

    public Map<String, IRFunction> getBuiltInFunctions()
    {
        return builtInFunctions;
    }

    public List<StaticData> getStaticDataList()
    {
        return staticDataList;
    }

    public Map<String, StaticStr> getStaticStrs()
    {
        return staticStrs;
    }

    public void setHasDivShiftInst(boolean hasDivShiftInst)
    {
        this.hasDivShiftInst = hasDivShiftInst;
    }

    public void updateCalleeSet()
    {
        Set<IRFunction> recursiveCalleeSet = new HashSet<>();
        for (IRFunction irFunction : functions.values()) irFunction.getRecursiveCalleeSet().clear();
        boolean flag = true;
        while (flag) {
            flag = false;
            for (IRFunction irFunction : functions.values()) {
                recursiveCalleeSet.clear();
                recursiveCalleeSet.addAll(irFunction.getCalleeSet());
                for (IRFunction calleeFunction : irFunction.getCalleeSet()) recursiveCalleeSet.addAll(calleeFunction.getRecursiveCalleeSet());
                if (!recursiveCalleeSet.equals(irFunction.getRecursiveCalleeSet())) {
                    irFunction.getRecursiveCalleeSet().clear();
                    irFunction.getRecursiveCalleeSet().addAll(recursiveCalleeSet);
                    flag = true;
                }
            }
        }
    }

    public boolean isHasDivShiftInst()
    {
        return hasDivShiftInst;
    }

    public void setPr(PhysicalRegister pr)
    {
        this.pr = pr;
    }

    public PhysicalRegister getPr()
    {
        return pr;
    }
}
