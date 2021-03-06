package Frontend;

import AST.ClassDeclNode;
import AST.DeclNode;
import AST.ProgramNode;
import AST.VarDeclNode;
import ScopeEntity.Scope;
import ScopeEntity.ClassEntity;
import ScopeEntity.VarEntity;
import Type.ClassType;
import Utils.CompilerError;

public class ClassVarMemBuilder extends ScopeBuilder
{
    private Scope globalScope, currentScope;
    private int offset;

    public ClassVarMemBuilder(Scope globalScope)
    {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(ProgramNode node)
    {
        for (DeclNode declNode : node.getDecls()){
            if (declNode instanceof ClassDeclNode) declNode.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclNode node)
    {
        ClassEntity entity = (ClassEntity) globalScope.get(node.getLocation(), node.getName(), "@C" + node.getName());
        currentScope = entity.getScope();
        offset = 0;
        for (VarDeclNode varDeclNode : node.getVarMember()) varDeclNode.accept(this);
        entity.setMemorySize(offset);
    }

    @Override
    public void visit(VarDeclNode node)
    {
        if (node.getType().getType() instanceof ClassType) {
            String className = ((ClassType) node.getType().getType()).getName();
            currentScope.get(node.getLocation(), className, "@C" + className);
        }
        if (node.getExpr() != null) throw new CompilerError(node.getLocation(), String.format("Variable \"%s\" should have no initialization", node.getName()));
        VarEntity entity = new VarEntity(node.getName(), node.getType().getType());
        entity.setAddrOffset(offset);
        offset += node.getType().getType().getVarSize();
        currentScope.put(node.getLocation(), node.getName(), "@V" + node.getName(), entity);
    }
}
