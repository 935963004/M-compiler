package ScopeEntity;

import AST.VarDeclNode;
import IR.Register;
import Type.Type;

public class VarEntity extends Entity
{
    private boolean isMember = false, isGlobal = false;
    private String className;
    private int addrOffset;
    private Register register;

    public VarEntity(String name, Type type)
    {
        super(name, type);
    }

    public VarEntity(VarDeclNode node)
    {
        super(node.getName(), node.getType().getType());
    }

    public VarEntity(String name, Type type, String className)
    {
        super(name, type);
        this.isMember = true;
        this.className = className;
    }

    public void setAddrOffset(int addrOffset)
    {
        this.addrOffset = addrOffset;
    }

    public void setGlobal(boolean isGlobal)
    {
        this.isGlobal = isGlobal;
    }

    public void setRegister(Register register)
    {
        this.register = register;
    }

    public Register getRegister()
    {
        return register;
    }

    public int getAddrOffset()
    {
        return addrOffset;
    }
}
