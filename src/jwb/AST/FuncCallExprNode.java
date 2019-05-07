package AST;

import ScopeEntity.FuncEntity;

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

    public FuncEntity getFuncEntity()
    {
        return funcEntity;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FuncCallExprNode)) return false;
        for (int i = 0; i < paraList.size(); ++i) {
            if (!paraList.get(i).equals(((FuncCallExprNode) obj).getParaList().get(i))) return false;
        }
        return expr.equals(((FuncCallExprNode) obj).getExpr()) && funcEntity.getClassName().equals(((FuncCallExprNode) obj).getFuncEntity().getClassName()) && funcEntity.getName() == ((FuncCallExprNode) obj).getFuncEntity().getName();
    }
}
