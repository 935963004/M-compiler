package ScopeEntity;

import AST.VarDeclNode;
import IR.Register;
import Type.Type;

public class VarEntity extends Entity
{
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

    public void setAddrOffset(int addrOffset)
    {
        this.addrOffset = addrOffset;
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
