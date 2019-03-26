package AST;

public class IfElseStmtNode extends StmtNode
{
    private ExprNode condition;
    private StmtNode thenStmt, elseStmt;

    public IfElseStmtNode(ExprNode condition, StmtNode thenStmt, StmtNode elseStmt, Location loc)
    {
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
        this.loc = loc;
    }

    public ExprNode getCondition()
    {
        return condition;
    }

    public StmtNode getThenStmt()
    {
        return thenStmt;
    }

    public StmtNode getElseStmt()
    {
        return elseStmt;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
