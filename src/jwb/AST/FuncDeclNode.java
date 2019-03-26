package AST;

import java.util.List;

public class FuncDeclNode extends DeclNode
{
    private boolean isConstruct;
    private TypeNode returnType;
    private List<VarDeclNode> parameterList;
    private BlockStmtNode body;

    public FuncDeclNode(TypeNode returnType, String name, List<VarDeclNode> parameterList, BlockStmtNode body, Location loc)
    {
        this.returnType = returnType;
        this.isConstruct = returnType == null;
        this.name = name;
        this.parameterList = parameterList;
        this.body = body;
        this.loc = loc;
    }

    public boolean getIsConstruct()
    {
        return isConstruct;
    }

    public TypeNode getReturnType()
    {
        return returnType;
    }

    public List<VarDeclNode> getParameterList()
    {
        return parameterList;
    }

    public BlockStmtNode getBody()
    {
        return body;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
