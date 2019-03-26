package AST;

import java.util.List;

public class ClassDeclNode extends DeclNode
{
    private List<VarDeclNode> varMember;
    private List<FuncDeclNode> funcMember;

    public ClassDeclNode(String name, List<VarDeclNode> varMember, List<FuncDeclNode> funcMember, Location loc)
    {
        this.name = name;
        this.varMember = varMember;
        this.funcMember = funcMember;
        this.loc = loc;
    }

    public List<VarDeclNode> getVarMember()
    {
        return varMember;
    }

    public List<FuncDeclNode> getFuncMember()
    {
        return funcMember;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
