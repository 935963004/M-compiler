package AST;

import IR.BasicBlock;
import IR.RegValue;
import Type.Type;

abstract public class ExprNode extends Node
{
    private Type type;
    private boolean isLeftValue;
    private BasicBlock trueBB = null, falseBB = null;
    private RegValue regValue, addrValue;
    private int addrOffset;

    public void setType(Type type)
    {
        this.type = type;
    }

    public void setLeftValue(boolean isLeftValue)
    {
        this.isLeftValue = isLeftValue;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isLeftValue()
    {
        return isLeftValue;
    }

    public void setTrueBB(BasicBlock basicBlock)
    {
        trueBB = basicBlock;
    }

    public void setFalseBB(BasicBlock basicBlock)
    {
        falseBB = basicBlock;
    }

    public BasicBlock getTrueBB()
    {
        return trueBB;
    }

    public BasicBlock getFalseBB()
    {
        return falseBB;
    }

    public RegValue getRegValue()
    {
        return regValue;
    }

    public void setRegValue(RegValue regValue)
    {
        this.regValue = regValue;
    }

    public RegValue getAddrValue()
    {
        return addrValue;
    }

    public int getAddrOffset()
    {
        return addrOffset;
    }

    public void setAddrValue(RegValue regValue)
    {
        addrValue = regValue;
    }

    public void setAddrOffset(int addrOffset)
    {
        this.addrOffset = addrOffset;
    }
}
