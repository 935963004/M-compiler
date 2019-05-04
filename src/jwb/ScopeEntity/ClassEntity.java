package ScopeEntity;

import AST.ClassDeclNode;
import AST.FuncDeclNode;
import Type.Type;
import Type.ClassType;

public class ClassEntity extends Entity
{
    private Scope scope;
    private int memorySize;

    public ClassEntity(String name, Type type, Scope parent)
    {
        super(name, type);
        scope = new Scope(parent, true);
    }

    public ClassEntity(ClassDeclNode node, Scope parent)
    {
        super(node.getName(), new ClassType(node.getName()));
        scope = new Scope(parent, true);
        for (FuncDeclNode funcDeclNode : node.getFuncMember()) {
            String name = funcDeclNode.getName();
            FuncEntity entity = new FuncEntity(funcDeclNode);
            entity.setClassName(node.getName());
            scope.put(funcDeclNode.getLocation(), name, "@F" + name, entity);
        }
    }

    public Scope getScope()
    {
        return scope;
    }

    public void setMemorySize(int memorySize)
    {
        this.memorySize = memorySize;
    }

    public int getMemorySize()
    {
        return memorySize;
    }
}
