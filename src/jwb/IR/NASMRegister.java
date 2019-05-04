package IR;

public class NASMRegister extends PhysicalRegister
{
    private String name;
    private boolean isGeneral, isCallerSave, isCalleeSave;
    private int argIdx;

    public NASMRegister(String name, boolean isGeneral, boolean isCallerSave, int argIdx)
    {
        this.name = name;
        this.isGeneral = isGeneral;
        this.isCallerSave = isCallerSave;
        this.isCalleeSave = !isCallerSave;
        this.argIdx = argIdx;
        NASMRegisterSet.allRegs.add(this);
        if (this.isGeneral) NASMRegisterSet.generalRegs.add(this);
        if (this.isCallerSave) NASMRegisterSet.callerSaveRegs.add(this);
        if (this.isCalleeSave) NASMRegisterSet.calleeSaveRegs.add(this);
        if (this.isArg()) NASMRegisterSet.argRegs.add(this);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isGeneral()
    {
        return isGeneral;
    }

    @Override
    public boolean isCallerSave()
    {
        return isCallerSave;
    }

    @Override
    public boolean isCalleeSave()
    {
        return isCalleeSave;
    }

    @Override
    public int getArgIdx()
    {
        return argIdx;
    }

    @Override
    public boolean isArg()
    {
        return argIdx != -1;
    }
}
