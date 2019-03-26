package AST;

import Scope.FuncEntity;

import java.util.List;

public class FuncCallExprNode extends ExprNode
{
    private ExprNode expr;
    private List<ExprNode> paraList;
    private FuncEntity funcEntity;

    public FuncCallExprNode(ExprNode expr, List<ExprNode> paraList, Location loc)
    {
        this.expr = expr;
        this.paraList = paraList;
        this.loc = loc;
    }

    public ExprNode getExpr()
    {
        return expr;
    }

    public List<ExprNode> getParaList()
    {
        return paraList;
    }

    public void setFuncEntity(FuncEntity funcEntity)
    {
        this.funcEntity = funcEntity;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
