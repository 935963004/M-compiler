package AST;

import java.util.List;

public class NewExprNode extends ExprNode
{
    private TypeNode newType;
    private List<ExprNode> exprList;
    private int dimNum;

    public NewExprNode(TypeNode type, List<ExprNode> exprList, int dimNum, Location loc)
    {
        this.newType = type;
        this.exprList = exprList;
        this.dimNum = dimNum;
        this.loc = loc;
    }

    public TypeNode getNewType()
    {
        return newType;
    }

    public List<ExprNode> getExprList()
    {
        return exprList;
    }

    public int getDimNum() {
        return dimNum;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
