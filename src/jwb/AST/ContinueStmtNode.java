package AST;

public class ContinueStmtNode extends StmtNode
{
    public ContinueStmtNode(Location loc)
    {
        this.loc = loc;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
