package AST;

public class BreakStmtNode extends StmtNode
{
    public BreakStmtNode(Location loc)
    {
        this.loc = loc;
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visit(this);
    }
}
