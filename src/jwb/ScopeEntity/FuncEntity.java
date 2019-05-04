package ScopeEntity;

import AST.FuncDeclNode;
import AST.VarDeclNode;
import Type.Type;
import Type.FuncType;

import java.util.ArrayList;
import java.util.List;

public class FuncEntity extends Entity
{
    private List<VarEntity> parameters;
    private Type returnType;
    private boolean isBuiltIn = false, isMember = false, isConstruct = false;
    private String className = null;

    public FuncEntity(String name, Type type)
    {
        super(name, type);
    }

    public FuncEntity(FuncDeclNode node)
    {
        super(node.getName(), new FuncType(node.getName()));
        parameters = new ArrayList<>();
        for (VarDeclNode varDeclNode : node.getParameterList()) parameters.add(new VarEntity(varDeclNode));
        returnType = node.getReturnType() == null ? null : node.getReturnType().getType();
        isConstruct = node.getIsConstruct();
    }

    public void setParameters(List<VarEntity> parameters)
    {
        this.parameters = parameters;
    }

    public void setReturnType(Type returnType)
    {
        this.returnType = returnType;
    }

    public void setBuiltIn(boolean isBuiltIn)
    {
        this.isBuiltIn = isBuiltIn;
    }

    public void setMember(boolean isMember)
    {
        this.isMember = isMember;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public Type getReturnType()
    {
        return returnType;
    }

    public List<VarEntity> getParameters()
    {
        return parameters;
    }

    public String getClassName()
    {
        return className;
    }

    public boolean isMember()
    {
        return isMember;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }
}
