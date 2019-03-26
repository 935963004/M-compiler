package AST;

public class WhileStmtNode extends StmtNode
{
    private ExprNode condition;
    private StmtNode stmt;

    public WhileStmtNode(ExprNode condition, StmtNode stmt, Location loc)
    {
        this.condition = condition;
        this.stmt = stmt;
        this.loc = loc;
    }

    public ExprNode getCondition()
    {
        return condition;
    }

    public StmtNode getStmt()
    {
        return stmt;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
