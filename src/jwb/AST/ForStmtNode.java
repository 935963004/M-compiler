package AST;

public class ForStmtNode extends StmtNode
{
    private ExprNode init, cond, update;
    private StmtNode stmt;

    public ForStmtNode(ExprNode init, ExprNode cond, ExprNode update, StmtNode stmt, Location loc)
    {
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.loc = loc;
    }

    public ExprNode getInit()
    {
        return init;
    }

    public ExprNode getCond()
    {
        return cond;
    }

    public ExprNode getUpdate()
    {
        return update;
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
