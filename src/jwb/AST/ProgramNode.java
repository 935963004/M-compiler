package AST;

import ScopeEntity.Scope;

import java.util.List;

public class ProgramNode extends Node
{
    private List<DeclNode> decls;
    private Scope scope;

    public ProgramNode(List<DeclNode> decls, Location loc)
    {
        this.decls = decls;
        this.loc = loc;
    }

    public List<DeclNode> getDecls()
    {
        return decls;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
